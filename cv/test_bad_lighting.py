import cv2
import numpy as np

from color_patches import extract_patches
from calibration import (
    estimate_color_transform,
    apply_color_transform,
    calculate_delta_e
)


# --------------------------------------------------
# Load the normal card
# --------------------------------------------------

normal_image = cv2.imread("test_canonical_card.png")

if normal_image is None:
    raise FileNotFoundError(
        "Could not find test_canonical_card.png"
    )


# --------------------------------------------------
# Load the degraded / bad-lighting image
# --------------------------------------------------

bad_image = cv2.imread("bad_lighting.png")

if bad_image is None:
    raise FileNotFoundError(
        "Could not find bad_lighting.png"
    )


# --------------------------------------------------
# Extract reference patches
# --------------------------------------------------

normal_patches = extract_patches(normal_image)
bad_patches = extract_patches(bad_image)


# Average colour of each patch
normal_colors = np.array([
    patch.mean(axis=(0, 1))
    for patch in normal_patches.values()
], dtype=np.float32)


bad_colors = np.array([
    patch.mean(axis=(0, 1))
    for patch in bad_patches.values()
], dtype=np.float32)


# --------------------------------------------------
# Learn calibration from the normal card
# --------------------------------------------------

transform = estimate_color_transform(
    normal_colors
)


# --------------------------------------------------
# Measure bad image BEFORE calibration
# --------------------------------------------------

delta_before = calculate_delta_e(
    bad_colors
)


print("\n--- BAD LIGHTING: BEFORE CALIBRATION ---")

for name, value in zip(
    bad_patches.keys(),
    delta_before
):
    print(
        f"{name}: ΔE = {value:.2f}"
    )

print(
    f"Average ΔE: {delta_before.mean():.2f}"
)


# --------------------------------------------------
# Apply calibration to bad image
# --------------------------------------------------

corrected_colors = apply_color_transform(
    bad_colors,
    transform
)


# --------------------------------------------------
# Measure AFTER calibration
# --------------------------------------------------

delta_after = calculate_delta_e(
    corrected_colors
)


print("\n--- BAD LIGHTING: AFTER CALIBRATION ---")

for name, value in zip(
    bad_patches.keys(),
    delta_after
):
    print(
        f"{name}: ΔE = {value:.2f}"
    )

print(
    f"Average ΔE: {delta_after.mean():.2f}"
)