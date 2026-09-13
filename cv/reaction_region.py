import cv2


def extract_inner_reaction(normalized_image):

    hsv = cv2.cvtColor(
        normalized_image,
        cv2.COLOR_BGR2HSV
    )

    saturation = hsv[:, :, 1]

    _, binary = cv2.threshold(
        saturation,
        50,
        255,
        cv2.THRESH_BINARY
    )

    contours, _ = cv2.findContours(
        binary,
        cv2.RETR_EXTERNAL,
        cv2.CHAIN_APPROX_SIMPLE
    )

    candidates = []

    for contour in contours:

        area = cv2.contourArea(contour)

        if area < 10000:
            continue

        perimeter = cv2.arcLength(
            contour,
            True
        )

        polygon = cv2.approxPolyDP(
            contour,
            0.02 * perimeter,
            True
        )

        if len(polygon) != 4:
            continue

        x, y, w, h = cv2.boundingRect(
            polygon
        )

        if w < 100 or h < 100:
            continue

        candidates.append(
            (area, polygon)
        )

    if not candidates:
        return None

    _, best = max(
        candidates,
        key=lambda item: item[0]
    )

    return best.reshape(4, 2)