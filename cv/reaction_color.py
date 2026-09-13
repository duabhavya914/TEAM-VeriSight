import cv2
import numpy as np


def extract_reaction_region(
    image,
    reaction_corners
):

    mask = np.zeros(
        image.shape[:2],
        dtype=np.uint8
    )

    cv2.fillPoly(
        mask,
        [reaction_corners.astype(np.int32)],
        255
    )

    reaction_pixels = image[
        mask == 255
    ]

    if reaction_pixels.size == 0:
        raise ValueError(
            "Reaction region is empty."
        )

    return reaction_pixels


def calculate_reaction_color(
    reaction_pixels
):

    mean_bgr = np.mean(
        reaction_pixels.astype(np.float32),
        axis=0
    )

    median_bgr = np.median(
        reaction_pixels.astype(np.float32),
        axis=0
    )

    return mean_bgr, median_bgr