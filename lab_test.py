import cv2
import numpy as np

# BGR colours
colours = np.array([
    [255, 0, 0],      # blue
    [0, 255, 0],      # green
    [0, 0, 255],      # red
    [0, 255, 255],    # yellow
    [255, 0, 255]     # magenta
], dtype=np.uint8)

# OpenCV expects an image, so make each colour a 1x1 pixel image
image = colours.reshape((-1, 1, 3))

# Convert BGR → Lab
lab = cv2.cvtColor(image, cv2.COLOR_BGR2LAB)

print("BGR:")
print(colours)

print("\nLab:")
print(lab.reshape((-1, 3)))


observed_blue = np.array(
    [[[178, 0, 0]]],
    dtype=np.uint8
)

observed_lab = cv2.cvtColor(
    observed_blue,
    cv2.COLOR_BGR2LAB
)

print("\nObserved blue Lab:")
print(observed_lab[0, 0])

import cv2
import numpy as np

known_blue = np.array([[[255, 0, 0]]], dtype=np.uint8)
observed_blue = np.array([[[178, 0, 0]]], dtype=np.uint8)

known_lab = cv2.cvtColor(known_blue, cv2.COLOR_BGR2LAB)[0, 0]
observed_lab = cv2.cvtColor(observed_blue, cv2.COLOR_BGR2LAB)[0, 0]

def opencv_lab_to_cielab(lab):
    lab = lab.astype(np.float32)

    L = lab[0] * 100 / 255
    a = lab[1] - 128
    b = lab[2] - 128

    return np.array([L, a, b])

known_cielab = opencv_lab_to_cielab(known_lab)
observed_cielab = opencv_lab_to_cielab(observed_lab)

delta_e = np.linalg.norm(known_cielab - observed_cielab)

print("Known CIELAB:")
print(known_cielab)

print("\nObserved CIELAB:")
print(observed_cielab)

print("\nDelta E76:")
print(delta_e)