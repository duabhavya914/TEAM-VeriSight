import cv2
import numpy as np

# -------------------------
# 1. Load image
# -------------------------

image = cv2.imread("marker.png")


# -------------------------
# 2. Set up ArUco detector
# -------------------------

dictionary = cv2.aruco.getPredefinedDictionary(
    cv2.aruco.DICT_4X4_50
)

detector = cv2.aruco.ArucoDetector(dictionary)


# -------------------------
# 3. Detect marker
# -------------------------

corners, ids, rejected = detector.detectMarkers(image)


if ids is None:
    print("No ArUco marker detected.")
    exit()


print("Marker detected!")
print("ID:", ids[0])


# -------------------------
# 4. Get the four corners
# -------------------------

source_points = corners[0][0].astype(np.float32)

print("Detected corners:")
print(source_points)


# -------------------------
# 5. Define our standard card
# -------------------------

destination_points = np.float32([
    [0, 0],
    [400, 0],
    [400, 400],
    [0, 400]
])


# -------------------------
# 6. Calculate transformation
# -------------------------

matrix = cv2.getPerspectiveTransform(
    source_points,
    destination_points
)


# -------------------------
# 7. Straighten the card
# -------------------------

warped = cv2.warpPerspective(
    image,
    matrix,
    (400, 400)
)


# -------------------------
# 8. Show result
# -------------------------

cv2.imshow("Original", image)
cv2.imshow("Straightened", warped)

cv2.waitKey(0)
cv2.destroyAllWindows()