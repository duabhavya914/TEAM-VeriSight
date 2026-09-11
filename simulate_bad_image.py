import cv2
import numpy as np

# Load our already unevenly-lit image
image = cv2.imread("realistic_lighting.png")

# Convert to float for safe calculations
image = image.astype(np.float32)

height, width = image.shape[:2]


# --------------------------------
# 1. Add a dark shadow
# --------------------------------

shadow = np.ones((height, width), dtype=np.float32)

# Shadow region
shadow[250:550, 100:550] = 0.45

image = image * shadow[:, :, np.newaxis]


# --------------------------------
# 2. Add glare
# --------------------------------

# Create a bright circular region
glare = np.zeros((height, width), dtype=np.float32)

cv2.circle(
    glare,
    (600, 300),
    100,
    1.0,
    -1
)

# Increase brightness inside glare
image = image + glare[:, :, np.newaxis] * 180


# --------------------------------
# 3. Add camera-like noise
# --------------------------------

noise = np.random.normal(
    0,
    5,
    image.shape
)

image = image + noise


# --------------------------------
# 4. Clip to valid pixel range
# --------------------------------

image = np.clip(image, 0, 255)

image = image.astype(np.uint8)


# --------------------------------
# 5. Save
# --------------------------------

cv2.imwrite(
    "bad_image.png",
    image
)

cv2.imshow("Bad Image", image)

cv2.waitKey(0)
cv2.destroyAllWindows()