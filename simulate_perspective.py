import cv2
import numpy as np

image = cv2.imread("reference_card.png")

height, width = image.shape[:2]

# Original four corners
source_points = np.float32([
    [0, 0],
    [width - 1, 0],
    [width - 1, height - 1],
    [0, height - 1]
])

# Simulate how the card might appear in a phone image
destination_points = np.float32([
    [120, 80],
    [850, 40],
    [900, 620],
    [60, 570]
])

matrix = cv2.getPerspectiveTransform(
    source_points,
    destination_points
)

warped = cv2.warpPerspective(
    image,
    matrix,
    (1000, 700)
)

cv2.imwrite(
    "perspective_card.png",
    warped
)

cv2.imshow(
    "Perspective Card",
    warped
)

cv2.waitKey(0)
cv2.destroyAllWindows()