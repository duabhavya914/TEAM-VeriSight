import cv2
import numpy as np


image = cv2.imread("test_capture.png")

if image is None:
    raise FileNotFoundError("Could not find test_capture.png")


height, width = image.shape[:2]


def perspective_distort(image, destination_points, filename):
    source_points = np.float32([
        [0, 0],
        [width - 1, 0],
        [width - 1, height - 1],
        [0, height - 1]
    ])

    destination_points = np.float32(destination_points)

    matrix = cv2.getPerspectiveTransform(
        source_points,
        destination_points
    )

    distorted = cv2.warpPerspective(
        image,
        matrix,
        (width, height)
    )

    cv2.imwrite(filename, distorted)

    print(f"Created: {filename}")


# --------------------------------------------------
# TEST 1 — Mild perspective
# --------------------------------------------------

perspective_distort(
    image,
    [
        [40, 20],
        [width - 30, 0],
        [width - 10, height - 20],
        [20, height - 5]
    ],
    "distorted_1_mild.png"
)


# --------------------------------------------------
# TEST 2 — Tilted perspective
# --------------------------------------------------

perspective_distort(
    image,
    [
        [120, 30],
        [width - 80, 100],
        [width - 30, height - 50],
        [60, height - 10]
    ],
    "distorted_2_tilted.png"
)


# --------------------------------------------------
# TEST 3 — Strong perspective
# --------------------------------------------------

perspective_distort(
    image,
    [
        [180, 80],
        [width - 180, 20],
        [width - 50, height - 80],
        [80, height - 20]
    ],
    "distorted_3_strong.png"
)


print("\nAll distorted test images created.")