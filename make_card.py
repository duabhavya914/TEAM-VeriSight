import cv2
import numpy as np

# Create a white card
card = np.ones((600, 900, 3), dtype=np.uint8) * 255

# -------------------------
# Add ArUco marker
# -------------------------

dictionary = cv2.aruco.getPredefinedDictionary(
    cv2.aruco.DICT_4X4_50
)

marker = cv2.aruco.generateImageMarker(
    dictionary,
    0,
    180
)

# Convert grayscale marker to BGR
marker = cv2.cvtColor(marker, cv2.COLOR_GRAY2BGR)

# Put marker on card
card[50:230, 50:230] = marker


# -------------------------
# Add colour patches
# -------------------------

patches = [
    ((300, 100), (255, 0, 0)),      # Blue (BGR)
    ((500, 100), (0, 255, 0)),      # Green
    ((700, 100), (0, 0, 255)),      # Red
    ((300, 350), (0, 255, 255)),    # Yellow
    ((500, 350), (255, 0, 255)),    # Magenta
    ((700, 350), (255, 255, 255)),  # White
]

patch_size = 120

for (x, y), colour in patches:

    cv2.rectangle(
        card,
        (x, y),
        (x + patch_size, y + patch_size),
        colour,
        -1
    )


# -------------------------
# Save
# -------------------------

cv2.imwrite("reference_card.png", card)

cv2.imshow("Reference Card", card)

cv2.waitKey(0)
cv2.destroyAllWindows()