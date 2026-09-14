import cv2
import numpy as np

image = cv2.imread("test_capture.png")

if image is None:
    raise FileNotFoundError(
        "Could not find test_capture.png"
    )

height, width = image.shape[:2]

# Create a smooth spatial lighting gradient.
x = np.linspace(0.65, 1.0, width)
y = np.linspace(1.0, 0.75, height)

gradient = np.outer(y, x)

result = image.astype(np.float32)

for channel in range(3):
    result[:, :, channel] *= gradient

result = np.clip(result, 0, 255).astype(np.uint8)

cv2.imwrite(
    "spatial_lighting.png",
    result
)

print("Saved: spatial_lighting.png")