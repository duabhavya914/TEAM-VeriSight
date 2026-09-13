import cv2

from color_patches import extract_patches
from quality_gate import (
    calculate_patch_metrics,
    print_quality_report
)
from quality_decision import print_quality_summary

# --------------------------------------------------
# Load image
# --------------------------------------------------

image = cv2.imread("test_canonical_card.png")

if image is None:
    raise FileNotFoundError(
        "Could not find bad_lighting.png"
    )


# --------------------------------------------------
# Extract reference patches
# --------------------------------------------------

patches = extract_patches(image)


# --------------------------------------------------
# Calculate quality metrics
# --------------------------------------------------

results = calculate_patch_metrics(
    patches
)


# --------------------------------------------------
# Print report
# --------------------------------------------------

print_quality_report(
    results
)
print_quality_summary(
    results
)