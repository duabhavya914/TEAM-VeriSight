import cv2
import numpy as np

# Load the image taken under bad lighting
image = cv2.imread("bad_lighting.png")

# Correction factors we calculated
correction = np.array([1.43, 1.0, 1.0])

# Convert to float so multiplication doesn't overflow
corrected = image.astype(np.float32) * correction

# Keep pixel values between 0 and 255
corrected = np.clip(corrected, 0, 255)

# Convert back to normal image format
corrected = corrected.astype(np.uint8)

cv2.imshow("Bad Lighting", image)
cv2.imshow("Corrected", corrected)

cv2.imwrite("corrected.png", corrected)

cv2.waitKey(0)
cv2.destroyAllWindows()