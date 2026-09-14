import cv2
import numpy as np

from reference_card import process_reference_card
from color_patches import extract_patches

from calibration import (
    REFERENCE_COLORS,
    estimate_color_transform,
    apply_color_transform,
    calculate_delta_e
)


# --------------------------------------------------
# 1. Load the colour-cast image
# --------------------------------------------------

image = cv2.imread(
    "reaction_calibration_lighting.png"
)

if image is None:
    raise FileNotFoundError(
        "Could not find reaction_calibration_lighting.png"
    )


# --------------------------------------------------
# 2. Detect and normalize reference card
# --------------------------------------------------

canonical = process_reference_card(
    image
)

if canonical is None:
    raise RuntimeError(
        "Reference card was not detected."
    )


print(
    "✅ Reference card detected."
)


# --------------------------------------------------
# 3. Extract reference patches
# --------------------------------------------------

patches = extract_patches(
    canonical
)


observed_colors = np.array([
    patch.mean(axis=(0, 1))
    for patch in patches.values()
], dtype=np.float32)


print(
    "\n--- OBSERVED REFERENCE COLOURS ---"
)

for name, color in zip(
    patches.keys(),
    observed_colors
):
    print(
        f"{name}: {color}"
    )


# --------------------------------------------------
# 4. Estimate current transform
# --------------------------------------------------

transform = estimate_color_transform(
    observed_colors
)


# --------------------------------------------------
# 5. Correct the SAME reference colours
# --------------------------------------------------

corrected_colors = apply_color_transform(
    observed_colors,
    transform
)


print(
    "\n--- CORRECTED REFERENCE COLOURS ---"
)

for name, color in zip(
    patches.keys(),
    corrected_colors
):
    print(
        f"{name}: {color}"
    )


# --------------------------------------------------
# 6. Calculate error
# --------------------------------------------------

before_delta_e = calculate_delta_e(
    observed_colors
)

after_delta_e = calculate_delta_e(
    corrected_colors
)


print(
    "\n--- CALIBRATION VALIDATION ---"
)


for index, name in enumerate(
    patches.keys()
):

    print(
        f"{name}: "
        f"before ΔE = {before_delta_e[index]:.2f}, "
        f"after ΔE = {after_delta_e[index]:.2f}"
    )


print(
    "\nAverage ΔE before:",
    f"{np.mean(before_delta_e):.2f}"
)

print(
    "Average ΔE after:",
    f"{np.mean(after_delta_e):.2f}"
)

print(
    "Maximum ΔE before:",
    f"{np.max(before_delta_e):.2f}"
)

print(
    "Maximum ΔE after:",
    f"{np.max(after_delta_e):.2f}"
)