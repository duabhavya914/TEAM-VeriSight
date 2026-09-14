import cv2
import numpy as np

image = cv2.imread("realistic_lighting.png")

if image is None:
    raise FileNotFoundError(
        "Could not find realistic_lighting.png"
    )

gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

adaptive = cv2.adaptiveThreshold(
    gray,
    255,
    cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
    cv2.THRESH_BINARY_INV,
    51,
    10
)

contours, hierarchy = cv2.findContours(
    adaptive,
    cv2.RETR_TREE,
    cv2.CHAIN_APPROX_SIMPLE
)

print("\n--- ALL ADAPTIVE CONTOURS ---")

for index, contour in enumerate(contours):

    area = cv2.contourArea(contour)

    perimeter = cv2.arcLength(
        contour,
        True
    )

    polygon = cv2.approxPolyDP(
        contour,
        0.02 * perimeter,
        True
    )

    x, y, w, h = cv2.boundingRect(
        polygon
    )

    print(f"\nContour {index}")
    print("  Area:", area)
    print("  Polygon corners:", len(polygon))
    print("  Bounding box:", (x, y, w, h))