import cv2
import numpy as np

import os
from dotenv import load_dotenv
from supabase import create_client


from reference_card import process_reference_card
from reaction_container import detect_reaction_container
from reaction_normalize import normalize_reaction
from reaction_region import extract_inner_reaction
from reaction_color import extract_reaction_region, calculate_reaction_color

from calibration import (
    REFERENCE_COLORS,
    estimate_channel_gains,
    apply_channel_gains
)

from color_patches import PATCHES

from quality_gate import calculate_patch_metrics
from quality_decision import decide_quality

import hashlib
from datetime import datetime, timezone

load_dotenv()

SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_KEY = os.getenv("SUPABASE_SECRET_KEY")

supabase = create_client(
    SUPABASE_URL,
    SUPABASE_KEY
)

def get_colour_target(test_id):
    response = (
        supabase
        .table("protocol_colour_targets")
        .select("*")
        .eq("test_id", test_id)
        .eq("is_active", True)
        .execute()
    )

    if not response.data:
        raise ValueError(
            f"No active colour target found for {test_id}"
        )

    return response.data[0]

def download_image(storage_path):
    bucket = "evidence-images"

    image_bytes = (
        supabase
        .storage
        .from_(bucket)
        .download(storage_path)
    )

    local_path = "_cv_input.jpg"

    with open(local_path, "wb") as file:
        file.write(image_bytes)

    return local_path


def save_analysis_result(
    result,
    storage_path,
    image_path,
    officer_id="TEST_OFFICER_001",
    latitude=None,
    longitude=None,
    captured_at=None
):
    """
    Save the CV analysis result into test_records.
    """

    # Calculate SHA-256 of the image used for analysis
    with open(image_path, "rb") as file:
        image_bytes = file.read()

    image_sha256 = hashlib.sha256(
        image_bytes
    ).hexdigest()

    record = {
        "test_id": result["test_id"],

        # Protocol linkage
        "protocol_id": result["test_id"],
        "protocol_version": "prototype-v1",

        # Officer / field context
        "officer_id": officer_id,

        "captured_at": (
            captured_at
            if captured_at is not None
            else datetime.now(timezone.utc).isoformat()
        ),

        "latitude": latitude,
        "longitude": longitude,

        # Image / evidence
        "image_storage_path": storage_path,
        "image_sha256": image_sha256,
        "image_size_bytes": len(image_bytes),
        "image_mime_type": "image/png",

        # Observed colour
        "observed_r": (
            result["observed_rgb"][0]
            if "observed_rgb" in result
            else None
        ),
        "observed_g": (
            result["observed_rgb"][1]
            if "observed_rgb" in result
            else None
        ),
        "observed_b": (
            result["observed_rgb"][2]
            if "observed_rgb" in result
            else None
        ),

        # Expected colour
        "expected_r": (
            result["expected_rgb"][0]
            if "expected_rgb" in result
            else None
        ),
        "expected_g": (
            result["expected_rgb"][1]
            if "expected_rgb" in result
            else None
        ),
        "expected_b": (
            result["expected_rgb"][2]
            if "expected_rgb" in result
            else None
        ),

        # Classification
        "rgb_distance": result.get("distance"),
        "rgb_tolerance": result.get("tolerance"),

        "reference_card_detected": result.get(
            "reference_card_detected",
            False
        ),

        "calibration_applied": result.get(
            "calibration_applied",
            False
        ),

        "result": result["result"],

        # Audit timestamps
        "created_at": datetime.now(
            timezone.utc
        ).isoformat(),

        "analysis_completed_at": datetime.now(
            timezone.utc
        ).isoformat()
    }

    response = (
        supabase
        .table("test_records")
        .insert(record)
        .execute()
    )

    print("\nSaved analysis to test_records:")
    print(response.data)

    return response.data

def calibrate_reaction(canonical, reaction_pixels):
    """
    Estimate lighting correction from the five reference-card
    colour patches and apply the correction to reaction pixels.
    """

    observed_colors = []

    for _, (x1, y1, x2, y2) in PATCHES.items():
        patch = canonical[y1:y2, x1:x2]

        if patch.size == 0:
            raise ValueError("Reference colour patch is empty.")

        mean_bgr = np.mean(
            patch.astype(np.float32),
            axis=(0, 1)
        )

        observed_colors.append(mean_bgr)

    observed_colors = np.array(
        observed_colors,
        dtype=np.float32
    )

    gains = estimate_channel_gains(
        observed_colors
    )

    calibrated_pixels = apply_channel_gains(
        reaction_pixels,
        gains
    )

    return calibrated_pixels, gains

def analyze_test(image_path, test_id):
    image = cv2.imread(image_path)

    if image is None:
        raise FileNotFoundError(f"Could not read image: {image_path}")

    # 1. Detect reference card and create canonical image
    canonical = process_reference_card(image)

    if canonical is None:
        return {
            "test_id": test_id,
            "result": "RETAKE",
            "reason": "Reference card not detected"
        }

        # Quality gate
    quality_metrics = calculate_patch_metrics(
        {
            name: canonical[y1:y2, x1:x2]
            for name, (x1, y1, x2, y2) in PATCHES.items()
        }
    )

    quality_decision = decide_quality(
        quality_metrics
    )

    if quality_decision == "RETAKE":
        return {
        "test_id": test_id,
        "result": "RETAKE",
        "reason": "Reference card quality is insufficient",
        "quality_status": "RETAKE",
        "reference_card_detected": True,
        "calibration_applied": False
    }
        # Quality check before continuing with colour analysis

    
    # 2. Detect reaction container
    reaction_corners = detect_reaction_container(image)

    if reaction_corners is None:
        return {
            "test_id": test_id,
            "result": "RETAKE",
            "reason": "Reaction container not detected"
        }

    # 3. Normalize reaction container
    normalized = normalize_reaction(
        image,
        reaction_corners
    )

    # 4. Find inner reaction region
    inner_corners = extract_inner_reaction(normalized)

    if inner_corners is None:
        return {
            "test_id": test_id,
            "result": "RETAKE",
            "reason": "Reaction region not detected"
        }

    # 5. Extract reaction pixels
    reaction_pixels = extract_reaction_region(
    normalized,
    inner_corners
)

    # Calibrate reaction colour using the reference card
    calibrated_pixels, gains = calibrate_reaction(
        canonical,
        reaction_pixels
    )

    mean_bgr, median_bgr = calculate_reaction_color(
        calibrated_pixels
    )

    # OpenCV uses BGR; database uses RGB
    observed_rgb = [
        float(mean_bgr[2]),
        float(mean_bgr[1]),
        float(mean_bgr[0])
    ]

    # 7. Fetch expected colour from Supabase
    target = get_colour_target(test_id)

    expected_rgb = [
        target["expected_r"],
        target["expected_g"],
        target["expected_b"]
    ]

    tolerance = target["rgb_distance_tolerance"]

    colour_name = target["final_colour_name"]

    # 8. Calculate Euclidean RGB distance
    observed = np.array(observed_rgb, dtype=np.float32)
    expected = np.array(expected_rgb, dtype=np.float32)

    distance = float(np.linalg.norm(observed - expected))

    # 9. Prototype classification
    if distance <= tolerance:
        result = "PRESUMPTIVE_POSITIVE"
    else:
        result = "INCONCLUSIVE"

        return {
        "test_id": test_id,
        "observed_rgb": observed_rgb,
        "expected_rgb": expected_rgb,
        "distance": distance,
        "tolerance": tolerance,
        "colour_name": colour_name,
        "result": result,
        "validation_status": target["validation_status"],
        "calibration_applied": True
    }
    


if __name__ == "__main__":
    storage_path = "TEST_OFFICER_001/OPI-001/test_capture.png"
    image_path = download_image(storage_path)
    test_id = "OPI-001"

    result = analyze_test(image_path, test_id)

    print("\nAnalysis result:")
    print(result)

    if result.get("result") not in ["RETAKE", "ERROR"]:
        save_analysis_result(
            result,
            f"{officer_id}/{test_id}/test_capture.png",
            str(temp_path),
            officer_id=officer_id,
            latitude=latitude,
            longitude=longitude,
            captured_at=captured_at
        )