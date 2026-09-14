import cv2
import numpy as np


# Known reference colours (BGR)
known_colours = np.array([
    [255, 0, 0],      # Blue
    [0, 255, 0],      # Green
    [0, 0, 255],      # Red
    [0, 255, 255],    # Yellow
    [255, 0, 255]     # Magenta
], dtype=np.uint8)


# Observed colours from bad_image.png
observed_colours = np.array([
    [181.0909, 1.7371, 1.7290],
    [15.9609, 246.8181, 15.9995],
    [1.7562, 1.7617, 252.7638],
    [1.7853, 95.6506, 95.6650],
    [181.2352, 52.1742, 200.1458]
], dtype=np.float32)


# -----------------------------------
# Convert OpenCV Lab → CIE Lab
# -----------------------------------

def opencv_lab_to_cielab(lab):

    lab = lab.astype(np.float32)

    L = lab[0] * 100 / 255
    a = lab[1] - 128
    b = lab[2] - 128

    return np.array([L, a, b])


# -----------------------------------
# Calculate CIE Lab
# -----------------------------------

def bgr_to_cielab(bgr):

    colour = np.array([[bgr]], dtype=np.uint8)

    lab = cv2.cvtColor(colour, cv2.COLOR_BGR2LAB)[0, 0]

    return opencv_lab_to_cielab(lab)


# -----------------------------------
# Calculate ΔE76
# -----------------------------------

def delta_e(colour1, colour2):

    return np.sqrt(np.sum((colour1 - colour2) ** 2))


# -----------------------------------
# Calculate calibration correction
# -----------------------------------

known_float = known_colours.astype(np.float32)

correction = []

for channel in range(3):

    valid = (
        (observed_colours[:, channel] > 0) &
        (known_float[:, channel] > 0)
    )

    ratios = (
        known_float[valid, channel] /
        observed_colours[valid, channel]
    )

    correction.append(np.mean(ratios))

correction = np.array(correction)


print("Calibration correction:", correction)


# -----------------------------------
# Apply correction
# -----------------------------------

corrected_colours = observed_colours * correction

corrected_colours = np.clip(
    corrected_colours,
    0,
    255
)


# -----------------------------------
# Calculate residual ΔE
# -----------------------------------

print("\nCalibration residual:")

errors = []

for i in range(len(known_colours)):

    known_lab = bgr_to_cielab(
        known_colours[i]
    )

    corrected_lab = bgr_to_cielab(
        corrected_colours[i].astype(np.uint8)
    )

    error = delta_e(
        known_lab,
        corrected_lab
    )

    errors.append(error)

    print(
        f"Patch {i+1} → ΔE = {error:.2f}"
    )


# -----------------------------------
# Summary
# -----------------------------------

errors = np.array(errors)

print("\nSummary:")
print("Average ΔE:", np.mean(errors))
print("Maximum ΔE:", np.max(errors))
