import cv2

from reaction_container import detect_reaction_container
from reaction_normalize import normalize_reaction
from reaction_region import extract_inner_reaction
from reaction_color import (
    extract_reaction_region,
    calculate_reaction_color
)


image = cv2.imread(
    "distorted_3_strong.png"
)

if image is None:
    raise FileNotFoundError(
        "Could not find distorted_3_strong.png"
    )


# Step 1: Detect reaction container

container_corners = detect_reaction_container(
    image
)

if container_corners is None:
    raise RuntimeError(
        "Reaction container was not detected."
    )


print("\n✅ Reaction container detected.")


# Step 2: Normalize reaction container

normalized = normalize_reaction(
    image,
    container_corners
)

cv2.imwrite(
    "full_normalized_reaction.png",
    normalized
)


print("✅ Reaction normalized.")


# Step 3: Detect inner reaction

reaction_corners = extract_inner_reaction(
    normalized
)

if reaction_corners is None:
    raise RuntimeError(
        "Inner reaction region was not detected."
    )


print("✅ Inner reaction detected.")

print("Corners:")
print(reaction_corners)


# Step 4: Extract reaction pixels

reaction_pixels = extract_reaction_region(
    normalized,
    reaction_corners
)


# Step 5: Calculate colour

mean_bgr, median_bgr = calculate_reaction_color(
    reaction_pixels
)


print("\n--- FINAL REACTION COLOUR ---")

print(
    "Mean BGR:",
    mean_bgr
)

print(
    "Median BGR:",
    median_bgr
)