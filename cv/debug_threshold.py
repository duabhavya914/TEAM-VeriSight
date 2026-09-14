import cv2

image = cv2.imread("realistic_lighting.png")

if image is None:
    raise FileNotFoundError(
        "Could not find realistic_lighting.png"
    )

gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

_, binary = cv2.threshold(
    gray,
    80,
    255,
    cv2.THRESH_BINARY_INV
)

cv2.imwrite(
    "debug_threshold.png",
    binary
)

print("Saved: debug_threshold.png")