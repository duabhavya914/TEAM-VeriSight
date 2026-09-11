import cv2
import numpy as np

aruco = cv2.aruco

# Choose the ArUco dictionary
dictionary = aruco.getPredefinedDictionary(aruco.DICT_4X4_50)

# Generate marker ID 0
marker = aruco.generateImageMarker(
    dictionary,
    0,
    400
)

# Create a white background
canvas = np.ones((600, 600), dtype=np.uint8) * 255

# Put marker in the center
canvas[100:500, 100:500] = marker

cv2.imwrite("marker.png", canvas)

print("Marker generated!")