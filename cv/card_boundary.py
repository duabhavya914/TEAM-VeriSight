import cv2
import numpy as np


def detect_card_corners(image):

    gray = cv2.cvtColor(
        image,
        cv2.COLOR_BGR2GRAY
    )

    # Threshold the bright card
    _, threshold = cv2.threshold(
        gray,
        220,
        255,
        cv2.THRESH_BINARY
    )

    contours, _ = cv2.findContours(
        threshold,
        cv2.RETR_EXTERNAL,
        cv2.CHAIN_APPROX_SIMPLE
    )

    candidates = []

    for contour in contours:

        area = cv2.contourArea(contour)

        if area < 100000:
            continue

        perimeter = cv2.arcLength(
            contour,
            True
        )

        approximation = cv2.approxPolyDP(
            contour,
            0.02 * perimeter,
            True
        )

        if len(approximation) == 4:
            candidates.append(
                approximation.reshape(4, 2)
            )

    if not candidates:
        return None

    # Largest quadrilateral = reference card
    card = max(
        candidates,
        key=cv2.contourArea
    )

    return order_corners(card)


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

    ordered[0] = points[
        np.argmin(sums)
    ]  # top-left

    ordered[2] = points[
        np.argmax(sums)
    ]  # bottom-right

    ordered[1] = points[
        np.argmin(differences)
    ]  # top-right

    ordered[3] = points[
        np.argmax(differences)
    ]  # bottom-left

    return ordered