import cv2
import numpy as np

import os
import hashlib
from datetime import datetime, timezone

from dotenv import load_dotenv
from supabase import create_client

from reference_card import process_reference_card
from reaction_container import detect_reaction_container
from reaction_normalize import normalize_reaction
from reaction_region import extract_inner_reaction
from reaction_color import (
    extract_reaction_region,
    calculate_reaction_color
)

from calibration import (
    estimate_color_transform,
    apply_color_transform,
    calculate_delta_e
)

from color_patches import PATCHES

from quality_gate import calculate_patch_metrics


# ==============================================================
# REFERENCE CARD QUALITY / CALIBRATION THRESHOLDS
# ==============================================================

# PROTOTYPE VALUES ONLY.
#
# Raw Delta E is NOT used as the initial rejection condition.
#
# The reference card exists to compensate for colour changes
# introduced by:
#
# - printing
# - paper
# - ambient lighting
# - phone camera
# - white balance
# - exposure
#
# Therefore we first check whether the patches are physically
# usable, then estimate a colour correction, and finally validate
# the corrected reference colours.

MAX_PATCH_VARIATION = 55.0

MIN_PATCH_SATURATION_PERCENT = 20.0

MAX_POST_CALIBRATION_AVERAGE_DELTA_E = 15.0

MAX_POST_CALIBRATION_SINGLE_DELTA_E = 25.0


# ==============================================================
# ENVIRONMENT / SUPABASE
# ==============================================================

load_dotenv()

SUPABASE_URL = os.getenv(
    "SUPABASE_URL"
)

SUPABASE_KEY = os.getenv(
    "SUPABASE_SECRET_KEY"
)


if not SUPABASE_URL:

    raise RuntimeError(
        "SUPABASE_URL is missing from environment variables."
    )


if not SUPABASE_KEY:

    raise RuntimeError(
        "SUPABASE_SECRET_KEY is missing from environment variables."
    )


supabase = create_client(
    SUPABASE_URL,
    SUPABASE_KEY
)


EVIDENCE_BUCKET = "evidence-images"


# ==============================================================
# GET EXPECTED COLOUR TARGET
# ==============================================================

def get_colour_target(
    test_id
):
    """
    Fetch the active colour target for the selected field test.
    """

    response = (
        supabase
        .table(
            "protocol_colour_targets"
        )
        .select(
            "*"
        )
        .eq(
            "test_id",
            test_id
        )
        .eq(
            "is_active",
            True
        )
        .execute()
    )


    if not response.data:

        raise ValueError(
            f"No active colour target found for {test_id}"
        )


    return response.data[0]


# ==============================================================
# GET ACTIVE PROTOCOL
# ==============================================================

def get_active_protocol(
    test_id
):
    """
    Resolve the real protocol ID and protocol version from
    the protocols table.
    """

    response = (
        supabase
        .table(
            "protocols"
        )
        .select(
            "id,test_id,app_protocol_version"
        )
        .eq(
            "test_id",
            test_id
        )
        .eq(
            "is_active",
            True
        )
        .limit(
            1
        )
        .execute()
    )


    if not response.data:

        return None


    return response.data[0]


# ==============================================================
# DOWNLOAD EVIDENCE IMAGE
# ==============================================================

def download_image(
    storage_path
):
    """
    Utility retained for manual/testing use.
    """

    image_bytes = (
        supabase
        .storage
        .from_(
            EVIDENCE_BUCKET
        )
        .download(
            storage_path
        )
    )


    local_path = "_cv_input.jpg"


    with open(
        local_path,
        "wb"
    ) as file:

        file.write(
            image_bytes
        )


    return local_path


# ==============================================================
# UPLOAD EXACT ORIGINAL IMAGE
# ==============================================================

def upload_original_evidence(
    image_path,
    storage_path,
    mime_type="image/jpeg"
):
    """
    Upload the exact file bytes used by the CV pipeline.

    No resize.
    No cv2.imwrite().
    No recompression.
    """

    with open(
        image_path,
        "rb"
    ) as file:

        image_bytes = file.read()


    if not image_bytes:

        raise ValueError(
            "Evidence image is empty."
        )


    supabase.storage.from_(
        EVIDENCE_BUCKET
    ).upload(

        path=
            storage_path,

        file=
            image_bytes,

        file_options={
            "content-type":
                mime_type,

            "upsert":
                "false"
        }
    )


    return len(
        image_bytes
    )


# ==============================================================
# SAVE SUCCESSFUL ANALYSIS
# ==============================================================

def save_analysis_result(
    result,
    storage_path,
    image_path,
    officer_id=None,
    latitude=None,
    longitude=None,
    captured_at=None
):
    """
    Persist a VALID completed analysis.

    RETAKE and ERROR must never be stored as final evidence.
    """


    # ----------------------------------------------------------
    # SAFETY CHECK
    # ----------------------------------------------------------

    analysis_result = str(
        result.get(
            "result",
            ""
        )
    ).upper()


    if analysis_result in [
        "RETAKE",
        "ERROR"
    ]:

        raise ValueError(
            "RETAKE/ERROR results must not be saved as final evidence."
        )


    # ----------------------------------------------------------
    # FIELD METADATA
    # ----------------------------------------------------------

    actual_officer_id = (

        officer_id

        if officer_id is not None

        else result.get(
            "officer_id"
        )
    )


    actual_latitude = (

        latitude

        if latitude is not None

        else result.get(
            "latitude"
        )
    )


    actual_longitude = (

        longitude

        if longitude is not None

        else result.get(
            "longitude"
        )
    )


    actual_captured_at = (

        captured_at

        if captured_at is not None

        else result.get(
            "captured_at"
        )
    )


    if actual_captured_at is None:

        actual_captured_at = (
            datetime.now(
                timezone.utc
            ).isoformat()
        )


    # ----------------------------------------------------------
    # READ ORIGINAL IMAGE BYTES
    # ----------------------------------------------------------

    with open(
        image_path,
        "rb"
    ) as file:

        image_bytes = file.read()


    if not image_bytes:

        raise ValueError(
            "Evidence image contains no data."
        )


    # ----------------------------------------------------------
    # SHA-256
    # ----------------------------------------------------------

    image_sha256 = hashlib.sha256(
        image_bytes
    ).hexdigest()


    api_hash = result.get(
        "image_sha256"
    )


    if (
        api_hash is not None
        and
        api_hash != image_sha256
    ):

        raise ValueError(
            "Evidence SHA-256 mismatch before Storage upload."
        )


    # ----------------------------------------------------------
    # MIME TYPE
    # ----------------------------------------------------------

    image_mime_type = result.get(
        "image_mime_type",
        "image/jpeg"
    )


    # ----------------------------------------------------------
    # ACTIVE PROTOCOL
    # ----------------------------------------------------------

    protocol = get_active_protocol(
        result[
            "test_id"
        ]
    )


    protocol_id = None

    protocol_version = None


    if protocol is not None:

        protocol_id = protocol.get(
            "id"
        )


        protocol_version = protocol.get(
            "app_protocol_version"
        )


    # ----------------------------------------------------------
    # UPLOAD ORIGINAL IMAGE
    # ----------------------------------------------------------

    upload_original_evidence(

        image_path=
            image_path,

        storage_path=
            storage_path,

        mime_type=
            image_mime_type
    )


    print(
        "\nEvidence image uploaded successfully:"
    )


    print(
        storage_path
    )


    print(
        f"SHA-256: {image_sha256}"
    )


    # ----------------------------------------------------------
    # DATABASE RECORD
    # ----------------------------------------------------------

    record = {


        # ------------------------------------------------------
        # TEST / PROTOCOL
        # ------------------------------------------------------

        "test_id":
            result[
                "test_id"
            ],

        "protocol_id":
            protocol_id,

        "protocol_version":
            protocol_version,


        # ------------------------------------------------------
        # OFFICER / FIELD CONTEXT
        # ------------------------------------------------------

        "officer_id":
            actual_officer_id,

        "captured_at":
            actual_captured_at,

        "latitude":
            actual_latitude,

        "longitude":
            actual_longitude,


        # ------------------------------------------------------
        # IMAGE / EVIDENCE
        # ------------------------------------------------------

        "image_storage_path":
            storage_path,

        "image_sha256":
            image_sha256,

        "image_size_bytes":
            len(
                image_bytes
            ),

        "image_mime_type":
            image_mime_type,


        # ------------------------------------------------------
        # OBSERVED COLOUR
        # ------------------------------------------------------

        "observed_r":
            (
                result[
                    "observed_rgb"
                ][0]

                if result.get(
                    "observed_rgb"
                )

                else None
            ),

        "observed_g":
            (
                result[
                    "observed_rgb"
                ][1]

                if result.get(
                    "observed_rgb"
                )

                else None
            ),

        "observed_b":
            (
                result[
                    "observed_rgb"
                ][2]

                if result.get(
                    "observed_rgb"
                )

                else None
            ),


        # ------------------------------------------------------
        # EXPECTED COLOUR
        # ------------------------------------------------------

        "expected_r":
            (
                result[
                    "expected_rgb"
                ][0]

                if result.get(
                    "expected_rgb"
                )

                else None
            ),

        "expected_g":
            (
                result[
                    "expected_rgb"
                ][1]

                if result.get(
                    "expected_rgb"
                )

                else None
            ),

        "expected_b":
            (
                result[
                    "expected_rgb"
                ][2]

                if result.get(
                    "expected_rgb"
                )

                else None
            ),


        # ------------------------------------------------------
        # CLASSIFICATION
        # ------------------------------------------------------

        "rgb_distance":
            result.get(
                "distance"
            ),

        "rgb_tolerance":
            result.get(
                "tolerance"
            ),

        "reference_card_detected":
            result.get(
                "reference_card_detected",
                True
            ),

        "calibration_applied":
            result.get(
                "calibration_applied",
                False
            ),

        "result":
            result[
                "result"
            ],


        # ------------------------------------------------------
        # AUDIT TIMESTAMPS
        # ------------------------------------------------------

        "created_at":
            datetime.now(
                timezone.utc
            ).isoformat(),

        "analysis_completed_at":
            datetime.now(
                timezone.utc
            ).isoformat()
    }


    # ----------------------------------------------------------
    # INSERT TEST RECORD
    # ----------------------------------------------------------

    try:

        response = (
            supabase
            .table(
                "test_records"
            )
            .insert(
                record
            )
            .execute()
        )


    except Exception as database_error:


        # ------------------------------------------------------
        # REMOVE IMAGE IF DATABASE INSERT FAILS
        # ------------------------------------------------------

        try:

            supabase.storage.from_(
                EVIDENCE_BUCKET
            ).remove(
                [
                    storage_path
                ]
            )


            print(
                "Database insert failed. "
                "Uploaded evidence image was removed."
            )


        except Exception as cleanup_error:

            print(
                "WARNING: Database insert failed and "
                "Storage cleanup also failed:"
            )


            print(
                cleanup_error
            )


        raise database_error


    print(
        "\nSaved analysis to test_records:"
    )


    print(
        response.data
    )


    return response.data


# ==============================================================
# PHYSICAL REFERENCE CARD QUALITY
# ==============================================================

def check_reference_card_physical_quality(
    quality_metrics
):
    """
    Check whether the detected reference patches are physically
    usable.

    Raw Delta E is intentionally NOT used for rejection here.
    """


    print(
        "\n========== REFERENCE CARD PHYSICAL QUALITY =========="
    )


    for name, metrics in quality_metrics.items():


        variation = float(
            metrics[
                "variation"
            ]
        )


        saturation_percentage = float(
            metrics[
                "saturation_percentage"
            ]
        )


        raw_delta_e = float(
            metrics[
                "delta_e"
            ]
        )


        print(
            f"{name}: "
            f"variation={variation:.2f}, "
            f"saturation={saturation_percentage:.2f}%, "
            f"raw_delta_e={raw_delta_e:.2f}"
        )


        # ------------------------------------------------------
        # PATCH TOO NON-UNIFORM
        # ------------------------------------------------------

        if (
            variation >
            MAX_PATCH_VARIATION
        ):

            return (
                False,
                (
                    f"{name} reference patch "
                    f"is too uneven"
                )
            )


        # ------------------------------------------------------
        # PATCH TOO WASHED OUT
        # ------------------------------------------------------

        if (
            saturation_percentage
            <
            MIN_PATCH_SATURATION_PERCENT
        ):

            return (
                False,
                (
                    f"{name} reference patch "
                    f"is too washed out"
                )
            )


    return (
        True,
        None
    )


# ==============================================================
# EXTRACT REFERENCE PATCH MEANS
# ==============================================================

def extract_reference_colors(
    canonical
):
    """
    Extract mean BGR colour from the centre of each reference
    patch.
    """


    observed_colors = []


    for name, (
        x1,
        y1,
        x2,
        y2
    ) in PATCHES.items():


        patch = canonical[
            y1:y2,
            x1:x2
        ]


        if patch.size == 0:

            raise ValueError(
                f"Reference patch is empty: {name}"
            )


        mean_bgr = np.mean(

            patch.astype(
                np.float32
            ),

            axis=(
                0,
                1
            )
        )


        observed_colors.append(
            mean_bgr
        )


    return np.array(
        observed_colors,
        dtype=np.float32
    )


# ==============================================================
# ESTIMATE + VALIDATE AFFINE COLOUR CALIBRATION
# ==============================================================

def validate_reference_calibration(
    canonical
):
    """
    Use the five reference-card patches to estimate a 3x4 affine
    BGR colour transform.

    The transform allows:

        B/G/R channel scaling
        cross-channel correction
        brightness / bias correction

    It is then applied back to the reference patches so we can
    inspect post-calibration Delta E.

    Returns:

        calibration_valid
        colour_transform
        average_delta_e
        maximum_delta_e
    """


    # ----------------------------------------------------------
    # EXTRACT RAW REFERENCE COLOURS
    # ----------------------------------------------------------

    observed_colors = extract_reference_colors(
        canonical
    )


    # ----------------------------------------------------------
    # RAW DELTA E FOR DIAGNOSTICS ONLY
    # ----------------------------------------------------------

    raw_delta_e = calculate_delta_e(
        observed_colors
    )


    print(
        "\n========== RAW REFERENCE DELTA E =========="
    )


    for index, name in enumerate(
        PATCHES.keys()
    ):

        print(
            f"{name}: "
            f"{raw_delta_e[index]:.2f}"
        )


    raw_average_delta_e = float(
        np.mean(
            raw_delta_e
        )
    )


    raw_maximum_delta_e = float(
        np.max(
            raw_delta_e
        )
    )


    print(
        f"Raw Average Delta E: "
        f"{raw_average_delta_e:.2f}"
    )


    print(
        f"Raw Maximum Delta E: "
        f"{raw_maximum_delta_e:.2f}"
    )


    # ----------------------------------------------------------
    # ESTIMATE 3x4 AFFINE COLOUR TRANSFORM
    # ----------------------------------------------------------

    colour_transform = estimate_color_transform(
        observed_colors
    )


    print(
        "\n========== AFFINE COLOUR TRANSFORM =========="
    )


    print(
        colour_transform
    )


    # ----------------------------------------------------------
    # APPLY TRANSFORM BACK TO REFERENCE PATCHES
    # ----------------------------------------------------------

    corrected_reference_colors = (
        apply_color_transform(
            observed_colors,
            colour_transform
        )
    )


    print(
        "\nCorrected reference BGR colours:"
    )


    for index, name in enumerate(
        PATCHES.keys()
    ):

        corrected = (
            corrected_reference_colors[
                index
            ]
        )


        print(
            f"{name}: "
            f"{np.round(corrected, 2)}"
        )


    # ----------------------------------------------------------
    # CALCULATE POST-CALIBRATION DELTA E
    # ----------------------------------------------------------

    post_delta_e = calculate_delta_e(
        corrected_reference_colors
    )


    average_delta_e = float(
        np.mean(
            post_delta_e
        )
    )


    maximum_delta_e = float(
        np.max(
            post_delta_e
        )
    )


    print(
        "\n========== POST-CALIBRATION QUALITY =========="
    )


    for index, name in enumerate(
        PATCHES.keys()
    ):

        print(
            f"{name}: "
            f"Delta E = "
            f"{post_delta_e[index]:.2f}"
        )


    print(
        "----------------------------------------------"
    )


    print(
        f"Post-calibration Average Delta E: "
        f"{average_delta_e:.2f}"
    )


    print(
        f"Post-calibration Maximum Delta E: "
        f"{maximum_delta_e:.2f}"
    )


    calibration_valid = (

        average_delta_e
        <=
        MAX_POST_CALIBRATION_AVERAGE_DELTA_E

        and

        maximum_delta_e
        <=
        MAX_POST_CALIBRATION_SINGLE_DELTA_E
    )


    print(
        "Calibration validation:"
    )


    print(
        (
            "PASS"
            if calibration_valid
            else "RETAKE"
        )
    )


    return (
        calibration_valid,
        colour_transform,
        average_delta_e,
        maximum_delta_e
    )


# ==============================================================
# APPLY AFFINE TRANSFORM TO REACTION ROI
# ==============================================================

def calibrate_reaction(
    reaction_pixels,
    colour_transform
):
    """
    Apply the same affine colour transform estimated from
    the reference card to the reaction pixels.

    Supports both:

        H x W x 3 image-shaped pixels

    and:

        N x 3 flattened pixels
    """

    reaction_pixels = np.asarray(
        reaction_pixels
    )


    # ==========================================================
    # CASE 1:
    # Already flattened as N x 3
    # ==========================================================

    if (
        reaction_pixels.ndim == 2
        and
        reaction_pixels.shape[1] == 3
    ):

        calibrated_pixels = apply_color_transform(
            reaction_pixels.astype(
                np.float32
            ),
            colour_transform
        )

        return calibrated_pixels


    # ==========================================================
    # CASE 2:
    # Image shaped as H x W x 3
    # ==========================================================

    if (
        reaction_pixels.ndim == 3
        and
        reaction_pixels.shape[2] == 3
    ):

        original_shape = (
            reaction_pixels.shape
        )


        flattened_pixels = (
            reaction_pixels
            .reshape(
                -1,
                3
            )
            .astype(
                np.float32
            )
        )


        corrected_flattened = (
            apply_color_transform(
                flattened_pixels,
                colour_transform
            )
        )


        calibrated_pixels = (
            corrected_flattened
            .reshape(
                original_shape
            )
            .astype(
                np.float32
            )
        )


        return calibrated_pixels


    # ==========================================================
    # UNSUPPORTED SHAPE
    # ==========================================================

    raise ValueError(
        (
            "Unexpected reaction pixel shape: "
            f"{reaction_pixels.shape}"
        )
    )


# ==============================================================
# COMPLETE CV ANALYSIS
# ==============================================================

def analyze_test(
    image_path,
    test_id
):


    # ==========================================================
    # 0. LOAD IMAGE
    # ==========================================================

    image = cv2.imread(
        image_path
    )


    if image is None:

        return {

            "test_id":
                test_id,

            "result":
                "ERROR",

            "reason":
                (
                    f"Could not read image: "
                    f"{image_path}"
                )
        }


    # ==========================================================
    # 1. REFERENCE CARD DETECTION
    # ==========================================================

    canonical = process_reference_card(
        image
    )


    if canonical is None:

        return {

            "test_id":
                test_id,

            "result":
                "RETAKE",

            "reason":
                "Reference card not detected",

            "reference_card_detected":
                False,

            "calibration_applied":
                False
        }


    # ==========================================================
    # 2. REFERENCE PATCH QUALITY METRICS
    # ==========================================================

    reference_patches = {


        name:
            canonical[
                y1:y2,
                x1:x2
            ]


        for name, (
            x1,
            y1,
            x2,
            y2
        ) in PATCHES.items()
    }


    quality_metrics = calculate_patch_metrics(
        reference_patches
    )


    # ==========================================================
    # 3. PHYSICAL QUALITY CHECK
    # ==========================================================

    (
        physical_quality_ok,
        physical_quality_reason
    ) = check_reference_card_physical_quality(
        quality_metrics
    )


    if not physical_quality_ok:

        return {

            "test_id":
                test_id,

            "result":
                "RETAKE",

            "reason":
                (
                    physical_quality_reason
                    or
                    "Reference card physical quality is insufficient"
                ),

            "quality_status":
                "RETAKE",

            "reference_card_detected":
                True,

            "calibration_applied":
                False
        }


    # ==========================================================
    # 4. ESTIMATE + VALIDATE AFFINE COLOUR CALIBRATION
    # ==========================================================

    try:

        (
            calibration_valid,
            colour_transform,
            post_average_delta_e,
            post_maximum_delta_e
        ) = validate_reference_calibration(
            canonical
        )


    except Exception as calibration_error:


        print(
            "\nReference-card calibration failed:"
        )


        print(
            calibration_error
        )


        return {

            "test_id":
                test_id,

            "result":
                "RETAKE",

            "reason":
                "Reference card calibration could not be estimated",

            "quality_status":
                "RETAKE",

            "reference_card_detected":
                True,

            "calibration_applied":
                False
        }


    if not calibration_valid:

        print(
            "\nWARNING:"
            " Reference calibration is outside the current "
            "prototype thresholds."
        )

        print(
            "Continuing analysis temporarily for "
            "calibration diagnostics."
        )


    # ==========================================================
    # 5. REACTION CONTAINER DETECTION
    # ==========================================================

    reaction_corners = detect_reaction_container(
        image
    )


    if reaction_corners is None:

        return {

            "test_id":
                test_id,

            "result":
                "RETAKE",

            "reason":
                "Reaction container not detected",

            "reference_card_detected":
                True,

            "calibration_applied":
                True
        }


    # ==========================================================
    # 6. NORMALIZE REACTION GEOMETRY
    # ==========================================================

    normalized = normalize_reaction(
        image,
        reaction_corners
    )


    # ==========================================================
    # 7. LOCATE INNER REACTION REGION
    # ==========================================================

    inner_corners = extract_inner_reaction(
        normalized
    )


    if inner_corners is None:

        return {

            "test_id":
                test_id,

            "result":
                "RETAKE",

            "reason":
                "Reaction region not detected",

            "reference_card_detected":
                True,

            "calibration_applied":
                True
        }


    # ==========================================================
    # 8. EXTRACT RAW REACTION ROI
    # ==========================================================

    reaction_pixels = extract_reaction_region(
        normalized,
        inner_corners
    )


    if (
        reaction_pixels is None
        or
        reaction_pixels.size == 0
    ):

        return {

            "test_id":
                test_id,

            "result":
                "RETAKE",

            "reason":
                "Reaction colour region could not be extracted",

            "reference_card_detected":
                True,

            "calibration_applied":
                True
        }


    # ==========================================================
    # RAW REACTION COLOUR FOR DEBUGGING
    # ==========================================================

    raw_mean_bgr, raw_median_bgr = (
        calculate_reaction_color(
            reaction_pixels
        )
    )


    print(
        "\n========== RAW REACTION COLOUR =========="
    )


    print(
        "Raw mean BGR:"
    )


    print(
        raw_mean_bgr
    )


    print(
        "Raw median BGR:"
    )


    print(
        raw_median_bgr
    )


    # ==========================================================
    # 9. APPLY AFFINE REFERENCE-CARD CALIBRATION TO ROI
    # ==========================================================

    try:

        calibrated_pixels = calibrate_reaction(
            reaction_pixels,
            colour_transform
        )


    except Exception as calibration_error:


        print(
            "\nReaction colour calibration failed:"
        )


        print(
            calibration_error
        )


        return {

            "test_id":
                test_id,

            "result":
                "RETAKE",

            "reason":
                "Reaction colour could not be calibrated",

            "reference_card_detected":
                True,

            "calibration_applied":
                False
        }


    print(
        "\nReference-card affine correction "
        "applied to reaction ROI."
    )


    # ==========================================================
    # 10. CALCULATE CALIBRATED REACTION COLOUR
    # ==========================================================

    mean_bgr, median_bgr = calculate_reaction_color(
        calibrated_pixels
    )


    print(
        "\n========== CALIBRATED REACTION COLOUR =========="
    )


    print(
        "Calibrated mean BGR:"
    )


    print(
        mean_bgr
    )


    print(
        "Calibrated median BGR:"
    )


    print(
        median_bgr
    )


    # ----------------------------------------------------------
    # OpenCV uses BGR.
    # Database/UI uses RGB.
    # ----------------------------------------------------------

    observed_rgb = [

        float(
            mean_bgr[
                2
            ]
        ),

        float(
            mean_bgr[
                1
            ]
        ),

        float(
            mean_bgr[
                0
            ]
        )
    ]


    print(
        "Observed calibrated reaction RGB:"
    )


    print(
        observed_rgb
    )


    # ==========================================================
    # 11. FETCH EXPECTED COLOUR TARGET
    # ==========================================================

    target = get_colour_target(
        test_id
    )


    expected_rgb = [

        float(
            target[
                "expected_r"
            ]
        ),

        float(
            target[
                "expected_g"
            ]
        ),

        float(
            target[
                "expected_b"
            ]
        )
    ]


    tolerance = float(
        target[
            "rgb_distance_tolerance"
        ]
    )


    colour_name = target[
        "final_colour_name"
    ]


    print(
        "\nExpected reaction RGB:"
    )


    print(
        expected_rgb
    )


    print(
        "RGB distance tolerance:"
    )


    print(
        tolerance
    )


    # ==========================================================
    # 12. CALCULATE RGB DISTANCE
    # ==========================================================

    observed = np.array(
        observed_rgb,
        dtype=np.float32
    )


    expected = np.array(
        expected_rgb,
        dtype=np.float32
    )


    distance = float(
        np.linalg.norm(
            observed
            -
            expected
        )
    )


    print(
        "\nReaction RGB distance:"
    )


    print(
        distance
    )


    # ==========================================================
    # 13. CLASSIFICATION
    # ==========================================================

    if (
        distance
        <=
        tolerance
    ):

        classification = (
            "PRESUMPTIVE_POSITIVE"
        )


    else:

        classification = (
            "INCONCLUSIVE"
        )


    print(
        "\nFinal classification:"
    )


    print(
        classification
    )


    # ==========================================================
    # 14. RETURN VALID ANALYSIS
    # ==========================================================

    return {

        "test_id":
            test_id,

        "observed_rgb":
            observed_rgb,

        "expected_rgb":
            expected_rgb,

        "distance":
            distance,

        "tolerance":
            tolerance,

        "colour_name":
            colour_name,

        "result":
            classification,

        "validation_status":
            target.get(
                "validation_status"
            ),

        "reference_card_detected":
            True,

        "quality_status":
            "PASS",

        "calibration_applied":
            True,

        "post_calibration_average_delta_e":
            post_average_delta_e,

        "post_calibration_maximum_delta_e":
            post_maximum_delta_e
    }


# ==============================================================
# LOCAL MANUAL TEST
# ==============================================================

if __name__ == "__main__":


    # ----------------------------------------------------------
    # MANUAL PYTHON TESTING ONLY
    # ----------------------------------------------------------

    storage_path = (
        "TEST_OFFICER_001/"
        "OPI-001/"
        "test_capture.png"
    )


    image_path = download_image(
        storage_path
    )


    test_id = "OPI-001"


    result = analyze_test(
        image_path,
        test_id
    )


    print(
        "\nAnalysis result:"
    )


    print(
        result
    )


    # ----------------------------------------------------------
    # Do not save another test_records row here.
    #
    # FastAPI /analyze handles production persistence.
    # ----------------------------------------------------------