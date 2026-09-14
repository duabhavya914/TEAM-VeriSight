import cv2
import numpy as np


NORMALIZED_WIDTH = 400
NORMALIZED_HEIGHT = 500


def order_corners(points):

    points = np.asarray(
        points,
        dtype=np.float32
    )

    ordered = np.zeros(
        (4, 2),
        dtype=np.float32
    )

    sums = points.sum(axis=1)
    differences = np.diff(
        points,
        axis=1
    ).reshape(-1)

    # Top-left
    ordered[0] = points[np.argmin(sums)]

    # Top-right
    ordered[1] = points[np.argmin(differences)]

    # Bottom-right
    ordered[2] = points[np.argmax(sums)]

    # Bottom-left
    ordered[3] = points[np.argmax(differences)]

    return ordered


def normalize_reaction(
    image,
    reaction_corners
):

    source = order_corners(
        reaction_corners
    )

    destination = np.array(
        [
            [0, 0],
            [NORMALIZED_WIDTH - 1, 0],
            [NORMALIZED_WIDTH - 1, NORMALIZED_HEIGHT - 1],
            [0, NORMALIZED_HEIGHT - 1]
        ],
        dtype=np.float32
    )

    transform = cv2.getPerspectiveTransform(
        source,
        destination
    )

    normalized = cv2.warpPerspective(
        image,
        transform,
        (
            NORMALIZED_WIDTH,
            NORMALIZED_HEIGHT
        )
    )

    return normalized