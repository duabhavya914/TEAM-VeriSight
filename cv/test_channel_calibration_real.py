import cv2
import numpy as np

from reference_card import process_reference_card
from color_patches import extract_patches
from calibration import (
    REFERENCE_COLORS,
    estimate_channel_gains,
    apply_channel_gains
)

from reaction_container import detect_reaction_container
from reaction_normalize import normalize_reaction
from reaction_region import extract_inner_reaction
from reaction_color import (
    extract_reaction_region,
    calculate_reaction_color
)


# --------------------------------------------------
# 1. Load colour-cast image
# --------------------------------------------------

image = cv2.imread(
    "spatial_lighting.png"
)

if image is None:
    raise FileNotFoundError(
        "Could not find reaction_calibration_lighting.png"
    )


# --------------------------------------------------
# 2. Detect reference card
# --------------------------------------------------

canonical = process_reference_card(
    image
)

if canonical is None:
    raise RuntimeError(
        "Reference card was not detected."
    )

print("✅ Reference card detected.")


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


print("\nObserved reference colours:")
print(observed_colors)

# --------------------------------------------------
# 4. Estimate channel gains
# --------------------------------------------------

gains = estimate_channel_gains(
    observed_colors
)
print("\nEstimated channel gains:", gains)
# --------------------------------------------------
# 5. Detect reaction container
# --------------------------------------------------

container_corners = (
    detect_reaction_container(image)
)

if container_corners is None:
    raise RuntimeError(
        "Reaction container was not detected."
    )

print("\n✅ Reaction container detected.")


# --------------------------------------------------
# 6. Normalize reaction
# --------------------------------------------------

normalized = normalize_reaction(
    image,
    container_corners
)

print("✅ Reaction normalized.")


# --------------------------------------------------
# 7. Detect inner reaction
# --------------------------------------------------

reaction_corners = (
    extract_inner_reaction(normalized)
)

if reaction_corners is None:
    raise RuntimeError(
        "Inner reaction was not detected."
    )

print("✅ Inner reaction detected.")


# --------------------------------------------------
# 8. Extract reaction colour
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
# 9. Apply channel gains
# --------------------------------------------------
corrected = apply_channel_gains(
    median_bgr,
    gains
)


# --------------------------------------------------
# 10. Print result
# --------------------------------------------------

print("\n--- REAL IMAGE CALIBRATION ---")

print(
    "Reaction before:",
    median_bgr
)

print(
    "Reaction after:",
    corrected
)

print(
    "Expected original:",
    "[0, 140, 255]"
)