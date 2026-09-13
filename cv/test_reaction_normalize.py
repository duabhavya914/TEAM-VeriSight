import cv2

from reaction_container import detect_reaction_container
from reaction_normalize import normalize_reaction


image = cv2.imread(
    "distorted_3_strong.png"
)

if image is None:
    raise FileNotFoundError(
        "Could not find distorted_2_tilted.png"
    )


corners = detect_reaction_container(
    image
)

if corners is None:
    raise RuntimeError(
        "Reaction container was not detected."
    )


normalized = normalize_reaction(
    image,
    corners
)


cv2.imwrite(
    "normalized_reaction.png",
    normalized
)


print(
    "Saved: normalized_reaction.png"
)

print(
    "Normalized size:",
    normalized.shape
)