import cv2
import numpy as np

image = cv2.imread("bad_lighting.png")

# What each patch is supposed to be
known_colours = np.array([
    [255, 0, 0],      # blue
    [0, 255, 0],      # green
    [0, 0, 255],      # red
    [0, 255, 255],    # yellow
    [255, 0, 255]     # magenta
], dtype=np.float32)

patches = [
    (300, 100, 420, 220),
    (500, 100, 620, 220),
    (700, 100, 820, 220),
    (300, 350, 420, 470),
    (500, 350, 620, 470)
]

observed_colours = []

# Measure each patch
for x1, y1, x2, y2 in patches:

    patch = image[y1:y2, x1:x2]

    average = np.mean(patch, axis=(0, 1))

    observed_colours.append(average)

observed_colours = np.array(
    observed_colours,
    dtype=np.float32
)

# Our previously calculated correction
correction = np.array([1.43, 1.0, 1.0])

# Apply correction to observed colours
corrected_colours = observed_colours * correction

corrected_colours = np.clip(
    corrected_colours,
    0,
    255
)

print("Patch comparison:\n")

for i in range(len(known_colours)):

    known = known_colours[i]
    observed = observed_colours[i]
    corrected = corrected_colours[i]

    error = np.linalg.norm(known - corrected)

    print(f"Patch {i + 1}")
    print("  Known:     ", known)
    print("  Observed:  ", observed)
    print("  Corrected: ", corrected)
    print("  Error:     ", error)
    print()