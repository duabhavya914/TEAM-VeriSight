import cv2
import numpy as np

image = cv2.imread("reference_card.png")

# Make the whole image warmer/brighter
lighting = np.array([0.7, 1.0, 1.0])

affected = image.astype(np.float32) * lighting

# Keep values within valid pixel range
affected = np.clip(affected, 0, 255).astype(np.uint8)

cv2.imwrite("bad_lighting.png", affected)

cv2.imshow("Original", image)
cv2.imshow("Bad Lighting", affected)

cv2.waitKey(0)
cv2.destroyAllWindows()