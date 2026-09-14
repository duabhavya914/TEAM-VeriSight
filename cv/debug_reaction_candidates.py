import cv2
import numpy as np


image = cv2.imread("realistic_lighting.png")

if image is None:
    raise FileNotFoundError("Could not find realistic_lighting.png")

gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

_, binary = cv2.threshold(
    gray,
    80,
    255,
    cv2.THRESH_BINARY_INV
)

contours, hierarchy = cv2.findContours(
    binary,
    cv2.RETR_TREE,
    cv2.CHAIN_APPROX_SIMPLE
)

image_height, image_width = image.shape[:2]
image_area = image_height * image_width

print("\n--- ALL CONTOURS ---")

for index, contour in enumerate(contours):

    area = cv2.contourArea(contour)

    perimeter = cv2.arcLength(contour, True)

    polygon = cv2.approxPolyDP(
        contour,
        0.02 * perimeter,
        True
    )

    x, y, w, h = cv2.boundingRect(polygon)

    print(f"\nContour {index}")
    print("  Area:", area)
    print("  Polygon corners:", len(polygon))
    print("  Bounding box:", (x, y, w, h))
    print("  Area ratio:", area / image_area)

    if len(polygon) == 4:
        print("  ✅ Four-sided candidate")

        if area < 20000:
            print("  ❌ Rejected: area < 20000")

        elif area > image_area * 0.8:
            print("  ❌ Rejected: area > 80% of image")

        elif w < 150 or h < 100:
            print("  ❌ Rejected: too small")

        elif x <= 2 or y <= 2:
            print("  ❌ Rejected: touches top/left edge")

        elif x + w >= image_width - 2:
            print("  ❌ Rejected: touches right edge")

        elif y + h >= image_height - 2:
            print("  ❌ Rejected: touches bottom edge")

        else:
            print("  ✅ PASSES GEOMETRIC FILTERS")
    else:
        print("  ❌ Rejected: not four-sided")