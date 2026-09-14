import cv2
import numpy as np

from reference_card import process_reference_card
from color_patches import extract_patches
from calibration import (
    estimate_color_transform,
    apply_color_transform
)

from reaction_container import detect_reaction_container
from reaction_normalize import normalize_reaction
from reaction_region import extract_inner_reaction
from reaction_color import (
    extract_reaction_region,
    calculate_reaction_color
)


# --------------------------------------------------
# 1. Load the original capture
# --------------------------------------------------

image = cv2.imread(
    "test_capture.png"
)

if image is None:
    raise FileNotFoundError(
        "Could not find test_capture.png"
    )


# --------------------------------------------------
# 2. Simulate a colour cast
# --------------------------------------------------

image_float = image.astype(np.float32)

image_float[:, :, 0] *= 0.85
image_float[:, :, 1] *= 0.90
image_float[:, :, 2] *= 0.80

image_float = np.clip(
    image_float,
    0,
    255
)

lit_image = image_float.astype(
    np.uint8
)

cv2.imwrite(
    "reaction_calibration_lighting.png",
    lit_image
)

print("✅ Artificial colour cast applied.")


# --------------------------------------------------
# 3. Process the reference card
# --------------------------------------------------

canonical = process_reference_card(
    lit_image
)

if canonical is None:
    raise RuntimeError(
        "Reference card was not detected."
    )

print(
    "✅ Reference card detected and normalized."
)


# --------------------------------------------------
# 4. Extract reference patches
# --------------------------------------------------

patches = extract_patches(
    canonical
)

observed_colors = np.array([
    patch.mean(axis=(0, 1))
    for patch in patches.values()
], dtype=np.float32)

print(
    "\nObserved reference colours:"
)

print(
    observed_colors
)


# --------------------------------------------------
# 5. Estimate colour correction
# --------------------------------------------------

transform = estimate_color_transform(
    observed_colors
)

print(
    "\n✅ Colour transform estimated."
)


# --------------------------------------------------
# 6. Detect reaction container
# --------------------------------------------------

container_corners = detect_reaction_container(
    lit_image
)

if container_corners is None:
    raise RuntimeError(
        "Reaction container was not detected."
    )

print(
    "✅ Reaction container detected."
)


# --------------------------------------------------
# 7. Normalize reaction
# --------------------------------------------------

normalized = normalize_reaction(
    lit_image,
    container_corners
)

print(
    "✅ Reaction normalized."
)


# --------------------------------------------------
# 8. Detect inner reaction
# --------------------------------------------------

reaction_corners = extract_inner_reaction(
    normalized
)

if reaction_corners is None:
    raise RuntimeError(
        "Inner reaction was not detected."
    )

print(
    "✅ Inner reaction detected."
)


# --------------------------------------------------
# 9. Extract reaction colour
# --------------------------------------------------

reaction_pixels = extract_reaction_region(
    normalized,
    reaction_corners
)

mean_bgr, median_bgr = (
    calculate_reaction_color(
        reaction_pixels
    )
)


# --------------------------------------------------
# 10. Apply reference-card correction
# --------------------------------------------------

corrected = apply_color_transform(
    median_bgr.reshape(1, 3),
    transform
)[0]


print(
    "\n--- REACTION COLOUR ---"
)

print(
    "Before calibration:",
    median_bgr
)

print(
    "After calibration:",
    corrected
)