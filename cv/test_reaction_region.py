import cv2

from reaction_region import extract_inner_reaction
from reaction_color import (
    extract_reaction_region,
    calculate_reaction_color
)


image = cv2.imread(
    "normalized_reaction.png"
)

if image is None:
    raise FileNotFoundError(
        "Could not find normalized_reaction.png"
    )


corners = extract_inner_reaction(
    image
)


if corners is None:
    raise RuntimeError(
        "Inner reaction region was not detected."
    )


print("\nInner reaction region detected.")

print("Corners:")
print(corners)

reaction_pixels = extract_reaction_region(
    image,
    corners
)

mean_bgr, median_bgr = calculate_reaction_color(
    reaction_pixels
)

print("\n--- REACTION COLOUR ---")

print(
    "Mean BGR:",
    mean_bgr
)

print(
    "Median BGR:",
    median_bgr
)

output = image.copy()

cv2.polylines(
    output,
    [corners.astype(int)],
    True,
    (0, 0, 255),
    3
)


cv2.imwrite(
    "reaction_region_detected.png",
    output
)


print(
    "Saved: reaction_region_detected.png"
)