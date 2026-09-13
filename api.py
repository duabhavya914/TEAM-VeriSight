import sys
from pathlib import Path

from fastapi import FastAPI, UploadFile, File, Form
import cv2
import numpy as np


# Allow api.py to use the existing CV modules
PROJECT_ROOT = Path(__file__).resolve().parent
CV_FOLDER = PROJECT_ROOT / "cv"

sys.path.insert(0, str(CV_FOLDER))

from analyze_test import analyze_test, save_analysis_result


app = FastAPI(
    title="VeriSight CV API",
    description="Computer vision analysis API for VeriSight",
    version="1.0"
)


@app.get("/")
def home():
    return {
        "status": "running",
        "service": "VeriSight CV API"
    }


@app.post("/analyze")
async def analyze(
    image: UploadFile = File(...),
    test_id: str = Form(...),
    officer_id: str = Form(...),
    latitude: float = Form(...),
    longitude: float = Form(...),
    captured_at: str = Form(...)
):
    
    # Read uploaded image
    image_bytes = await image.read()

    # Convert uploaded bytes into an OpenCV image
    image_array = np.frombuffer(
        image_bytes,
        dtype=np.uint8
    )

    cv_image = cv2.imdecode(
        image_array,
        cv2.IMREAD_COLOR
    )

    if cv_image is None:
        return {
            "result": "ERROR",
            "reason": "Could not decode uploaded image"
        }

    # Temporarily save image for the existing CV pipeline
    temp_path = PROJECT_ROOT / "api_input.png"

    cv2.imwrite(
        str(temp_path),
        cv_image
    )

    # Run existing CV pipeline
    result = analyze_test(
    str(temp_path),
    test_id
)

    if result.get("result") != "ERROR":
      save_analysis_result(
    result,
    "TEST_OFFICER_001/OPI-001/test_capture.png",
    str(temp_path)
)

    return result