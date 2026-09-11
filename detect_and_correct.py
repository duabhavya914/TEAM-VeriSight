import cv2
import numpy as np


# ============================================================
# 1. LOAD IMAGE
# ============================================================

image = cv2.imread("perspective_card.png")

if image is None:
    print("Could not load image.")
    exit()


# ============================================================
# 2. ARUCO DETECTION
# ============================================================

dictionary = cv2.aruco.getPredefinedDictionary(
    cv2.aruco.DICT_4X4_50
)

detector = cv2.aruco.ArucoDetector(
    dictionary
)

corners, ids, rejected = detector.detectMarkers(
    image
)


if ids is None:

    print("No ArUco marker detected.")
    exit()


print("Detected IDs:", ids)


# ============================================================
# 3. GET DETECTED MARKER CORNERS
# ============================================================

detected_corners = corners[0][0]

print("\nDetected marker corners:")
print(detected_corners)


# ============================================================
# 4. DEFINE CANONICAL MARKER CORNERS
# ============================================================

canonical_marker_corners = np.float32([
    [50, 50],
    [230, 50],
    [230, 230],
    [50, 230]
])


# ============================================================
# 5. CALCULATE PERSPECTIVE TRANSFORMATION
# ============================================================

matrix = cv2.getPerspectiveTransform(
    detected_corners.astype(np.float32),
    canonical_marker_corners
)


# ============================================================
# 6. WARP IMAGE INTO CANONICAL CARD SPACE
# ============================================================

canonical = cv2.warpPerspective(
    image,
    matrix,
    (900, 600)
)


# ============================================================
# 7. SAVE RESULT
# ============================================================

cv2.imwrite(
    "canonical_card.png",
    canonical
)


# ============================================================
# 8. DISPLAY RESULT
# ============================================================

cv2.imshow(
    "Canonical Card",
    canonical
)

cv2.waitKey(0)
cv2.destroyAllWindows()


print("\nCanonical card saved as canonical_card.png")