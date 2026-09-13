import cv2

image = cv2.imread("realistic_lighting.png")

if image is None:
    raise FileNotFoundError(
        "Could not find realistic_lighting.png"
    )

gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

adaptive = cv2.adaptiveThreshold(
    gray,
    255,
    cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
    cv2.THRESH_BINARY_INV,
    51,
    10
)

cv2.imwrite(
    "debug_adaptive_threshold.png",
    adaptive
)

print("Saved: debug_adaptive_threshold.png")