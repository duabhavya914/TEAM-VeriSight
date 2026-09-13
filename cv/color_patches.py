import cv2
import numpy as np


# Reference patch coordinates
# Format: x1, y1, x2, y2

PATCHES = {
    "Blue": (300, 100, 420, 220),
    "Green": (500, 100, 620, 220),
    "Red": (700, 100, 820, 220),
    "Yellow": (300, 350, 420, 470),
    "Magenta": (500, 350, 620, 470),
}


def extract_patches(image):
    """
    Extract the five reference colour patches
    from a canonical 900 x 600 card.
    """

    extracted = {}

    for name, (x1, y1, x2, y2) in PATCHES.items():
        patch = image[y1:y2, x1:x2]

        if patch.size == 0:
            raise ValueError(
                f"Could not extract patch: {name}"
            )

        extracted[name] = patch

    return extracted