import cv2
import numpy as np

image = cv2.imread("reference_card.png")

# Convert to float so we can do calculations safely
image = image.astype(np.float32)

height, width = image.shape[:2]

# Create an illumination gradient:
# left side = darker
# right side = brighter

gradient = np.linspace(0.6, 1.2, width)

# Turn it into a 2D image
gradient = np.tile(gradient, (height, 1))

# Apply the gradient to every colour channel
affected = image * gradient[:, :, np.newaxis]

# Add a slight colour cast
# Remember: OpenCV uses BGR
colour_cast = np.array([0.85, 1.0, 1.0])

affected = affected * colour_cast

# Keep values inside valid image range
affected = np.clip(affected, 0, 255)

affected = affected.astype(np.uint8)

cv2.imwrite("realistic_lighting.png", affected)

cv2.imshow("Original", cv2.imread("reference_card.png"))
cv2.imshow("Realistic Lighting", affected)

cv2.waitKey(0)
cv2.destroyAllWindows()