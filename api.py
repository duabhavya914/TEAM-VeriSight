import sys
import hashlib
import re
import tempfile
from pathlib import Path

from fastapi import FastAPI, UploadFile, File, Form

# --------------------------------------------------
# PROJECT PATHS
# --------------------------------------------------

PROJECT_ROOT = Path(__file__).resolve().parent
CV_FOLDER = PROJECT_ROOT / "cv"

sys.path.insert(0, str(CV_FOLDER))

from analyze_test import analyze_test, save_analysis_result


# --------------------------------------------------
# FASTAPI APP
# --------------------------------------------------

app = FastAPI(
    title="VeriSight CV API",
    description="Computer vision analysis API for VeriSight / FieldTest Secure",
    version="1.1"
)


# --------------------------------------------------
# HELPERS
# --------------------------------------------------

def safe_path_part(value: str) -> str:
    """
    Make officer_id / test_id safe to use inside
    a Supabase Storage object path.
    """

    cleaned = re.sub(
        r"[^A-Za-z0-9._-]",
        "_",
        value.strip()
    )

    return cleaned or "unknown"


def safe_filename(filename: str | None) -> str:
    """
    Prevent directory traversal and preserve the
    original image extension/name where possible.
    """

    if not filename:
        return "evidence.jpg"

    return Path(filename).name


# --------------------------------------------------
# HEALTH CHECK
# --------------------------------------------------

@app.get("/")
def home():
    return {
        "status": "running",
        "service": "VeriSight CV API"
    }


# --------------------------------------------------
# ANALYSE FIELD TEST IMAGE
# --------------------------------------------------

@app.post("/analyze")
async def analyze(
    image: UploadFile = File(...),
    test_id: str = Form(...),
    officer_id: str = Form(...),
    latitude: float = Form(...),
    longitude: float = Form(...),
    captured_at: str = Form(...)
):

    temp_path = None

    try:

        # --------------------------------------------------
        # 1. READ EXACT BYTES RECEIVED FROM ANDROID
        # --------------------------------------------------

        image_bytes = await image.read()

        if not image_bytes:
            return {
                "result": "ERROR",
                "reason": "Uploaded image is empty"
            }


        # --------------------------------------------------
        # 2. SHA-256 OF EXACT UPLOADED JPEG BYTES
        # --------------------------------------------------

        image_sha256 = hashlib.sha256(
            image_bytes
        ).hexdigest()


        # --------------------------------------------------
        # 3. PREPARE SAFE STORAGE INFORMATION
        # --------------------------------------------------

        clean_officer_id = safe_path_part(
            officer_id
        )

        clean_test_id = safe_path_part(
            test_id
        )

        clean_filename = safe_filename(
            image.filename
        )


        storage_path = (
            f"{clean_officer_id}/"
            f"{clean_test_id}/"
            f"{clean_filename}"
        )


        # --------------------------------------------------
        # 4. PRESERVE EXACT ORIGINAL FILE BYTES
        #
        # IMPORTANT:
        # We DO NOT use cv2.imwrite().
        #
        # That would decode + re-encode the image and would
        # create different bytes.
        #
        # Instead, write the exact uploaded bytes directly.
        # --------------------------------------------------

        suffix = Path(
            clean_filename
        ).suffix

        if not suffix:
            suffix = ".jpg"


        with tempfile.NamedTemporaryFile(
            suffix=suffix,
            delete=False
        ) as temp_file:

            temp_file.write(
                image_bytes
            )

            temp_file.flush()

            temp_path = Path(
                temp_file.name
            )


        # --------------------------------------------------
        # 5. VERIFY TEMP FILE HAS EXACT SAME HASH
        # --------------------------------------------------

        with open(
            temp_path,
            "rb"
        ) as saved_file:

            temp_bytes = saved_file.read()


        temp_sha256 = hashlib.sha256(
            temp_bytes
        ).hexdigest()


        if temp_sha256 != image_sha256:

            return {
                "result": "ERROR",
                "reason": "Image integrity verification failed"
            }


        # --------------------------------------------------
        # 6. RUN EXISTING COMPUTER-VISION PIPELINE
        # --------------------------------------------------

        result = analyze_test(
            str(temp_path),
            test_id
        )


        if result is None:

            return {
                "result": "ERROR",
                "reason": "CV pipeline returned no result"
            }


        # --------------------------------------------------
        # 7. ADD ANDROID EVIDENCE METADATA TO RESULT
        # --------------------------------------------------

        result["test_id"] = test_id
        result["officer_id"] = officer_id

        result["latitude"] = latitude
        result["longitude"] = longitude

        result["captured_at"] = captured_at

        result["image_sha256"] = image_sha256

        result["image_storage_path"] = storage_path

        result["original_filename"] = clean_filename


        # --------------------------------------------------
        # 8. RETAKE / QUALITY FAILURE
        #
        # IMPORTANT:
        #
        # RETAKE images must NOT:
        # - be uploaded to permanent evidence storage
        # - create a final test_records row
        # --------------------------------------------------

        analysis_result = str(
            result.get(
                "result",
                ""
            )
        ).upper()


        if analysis_result == "RETAKE":

            result["saved"] = False

            return result


        # --------------------------------------------------
        # 9. CV / PROCESSING ERROR
        #
        # Also do not save anything permanently.
        # --------------------------------------------------

        if analysis_result == "ERROR":

            result["saved"] = False

            return result


        # --------------------------------------------------
        # 10. VALID ANALYSIS
        #
        # Expected examples:
        #
        # PRESUMPTIVE_POSITIVE
        # INCONCLUSIVE
        #
        # save_analysis_result() should:
        #
        # - upload temp_path to private evidence-images bucket
        #   using storage_path
        #
        # - insert complete analysis into test_records
        #
        # Because temp_path contains the exact Android JPEG
        # bytes, the uploaded evidence remains unchanged.
        # --------------------------------------------------

        save_analysis_result(
            result,
            storage_path,
            str(temp_path)
        )


        result["saved"] = True


        # --------------------------------------------------
        # 11. RETURN RESULT TO ANDROID
        # --------------------------------------------------

        return result


    except Exception as exception:

        print(
            "API analysis error:",
            exception
        )

        return {
            "result": "ERROR",
            "reason": str(exception),
            "saved": False
        }


    finally:

        # --------------------------------------------------
        # 12. DELETE ONLY THE TEMPORARY LOCAL COPY
        #
        # This does NOT delete the Supabase Storage object.
        # --------------------------------------------------

        if temp_path is not None:

            try:

                if temp_path.exists():
                    temp_path.unlink()

            except Exception as cleanup_error:

                print(
                    "Temporary file cleanup failed:",
                    cleanup_error
                )