import cv2
import numpy as np

image = cv2.imread("bad_lighting.png")

patches = {
    "blue":    (300, 100, 420, 220),
    "green":   (500, 100, 620, 220),
    "red":     (700, 100, 820, 220),
    "yellow":  (300, 350, 420, 470),
    "magenta": (500, 350, 620, 470)
}

for name, (x1, y1, x2, y2) in patches.items():

    patch = image[y1:y2, x1:x2]

    average = np.mean(patch, axis=(0, 1))

    print(name, "→", average)