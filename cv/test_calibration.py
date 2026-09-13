import cv2
import numpy as np

from color_patches import extract_patches
from calibration import (
    REFERENCE_COLORS,
    estimate_color_transform,
    apply_color_transform,
    calculate_delta_e
)


# --------------------------------------------------
# Load canonical card
# --------------------------------------------------

image = cv2.imread("test_canonical_card.png")

if image is None:
    raise FileNotFoundError(
        "Could not find test_canonical_card.png"
    )


# --------------------------------------------------
# Extract reference patches
# --------------------------------------------------

patches = extract_patches(image)


# Calculate average colour of each patch
observed_colors = np.array([
    patch.mean(axis=(0, 1))
    for patch in patches.values()
], dtype=np.float32)


# --------------------------------------------------
# Delta E BEFORE calibration
# --------------------------------------------------

delta_before = calculate_delta_e(
    observed_colors
)


print("\n--- BEFORE CALIBRATION ---")

for name, value in zip(
    patches.keys(),
    delta_before
):
    print(
        f"{name}: ΔE = {value:.2f}"
    )

print(
    f"Average ΔE: {delta_before.mean():.2f}"
)


# --------------------------------------------------
# Estimate colour transformation
# --------------------------------------------------

transform = estimate_color_transform(
    observed_colors
)


print("\n--- TRANSFORMATION MATRIX ---")
print(transform)


# --------------------------------------------------
# Apply calibration
# --------------------------------------------------

corrected_colors = apply_color_transform(
    observed_colors,
    transform
)


# --------------------------------------------------
# Delta E AFTER calibration
# --------------------------------------------------

delta_after = calculate_delta_e(
    corrected_colors
)


print("\n--- AFTER CALIBRATION ---")

for name, value in zip(
    patches.keys(),
    delta_after
):
    print(
        f"{name}: ΔE = {value:.2f}"
    )

print(
    f"Average ΔE: {delta_after.mean():.2f}"
)