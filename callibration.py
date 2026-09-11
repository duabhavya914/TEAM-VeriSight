import cv2
import numpy as np


# --------------------------------
# 1. Known colours of our card
# --------------------------------

known_colours = np.array([
    [255, 0, 0],      # Blue
    [0, 255, 0],      # Green
    [0, 0, 255],      # Red
    [0, 255, 255],    # Yellow
    [255, 0, 255]     # Magenta
], dtype=np.uint8)


# --------------------------------
# 2. Observed colours from camera
# --------------------------------

image = cv2.imread("bad_image.png")

patches = {
    "Blue":    (300, 100, 420, 220),
    "Green":   (500, 100, 620, 220),
    "Red":     (700, 100, 820, 220),
    "Yellow":  (300, 350, 420, 470),
    "Magenta": (500, 350, 620, 470)
}

observed_list = []

for name, (x1, y1, x2, y2) in patches.items():

    patch = image[y1:y2, x1:x2]

    average = np.mean(patch, axis=(0, 1))

    observed_list.append(average)

    print(name, "→", average)

observed_colours = np.array(
    observed_list,
    dtype=np.float32
)


# --------------------------------
# 3. Convert BGR → CIELAB
# --------------------------------

def bgr_to_cielab(colours):

    # Convert to float first so subtraction
    # doesn't cause uint8 overflow
    colours = colours.astype(np.uint8)

    image = colours.reshape((-1, 1, 3))

    lab = cv2.cvtColor(image, cv2.COLOR_BGR2LAB)

    lab = lab.reshape((-1, 3)).astype(np.float32)

    # Convert OpenCV's 8-bit Lab representation
    # to conventional CIELAB
    lab[:, 0] = lab[:, 0] * 100 / 255
    lab[:, 1] = lab[:, 1] - 128
    lab[:, 2] = lab[:, 2] - 128

    return lab


known_lab = bgr_to_cielab(known_colours)
observed_lab = bgr_to_cielab(observed_colours)


# --------------------------------
# 4. Calculate ΔE76
# --------------------------------

delta_e = np.linalg.norm(
    known_lab - observed_lab,
    axis=1
)


# --------------------------------
# 5. Print results
# --------------------------------

names = [
    "Blue",
    "Green",
    "Red",
    "Yellow",
    "Magenta"
]

for i in range(len(names)):

    print(names[i])

    print("  Known Lab:   ", known_lab[i])
    print("  Observed Lab:", observed_lab[i])
    print("  Delta E76:   ", delta_e[i])

    print()


# --------------------------------
# 6. Overall calibration error
# --------------------------------

average_error = np.mean(delta_e)
maximum_error = np.max(delta_e)

# --------------------------------
# 6. Calculate BGR correction
# --------------------------------

# --------------------------------
# Calculate BGR correction
# --------------------------------
known_float = known_colours.astype(np.float32)
observed_float = observed_colours.astype(np.float32)

correction = []

for channel in range(3):

    valid = (
        (observed_float[:, channel] > 0) &
        (known_float[:, channel] > 0)
    )

    ratios = (
        known_float[valid, channel] /
        observed_float[valid, channel]
    )

    correction.append(np.mean(ratios))

correction = np.array(correction)

print("--------------------------------")
print("BGR Correction")
print("--------------------------------")
print(correction)
# --------------------------------
# 7. Apply correction
# --------------------------------

corrected_colours = (
    observed_colours.astype(np.float32) * correction
)

corrected_colours = np.clip(
    corrected_colours,
    0,
    255
)

print("\nCorrected colours:")

for i in range(len(names)):
    print(names[i], "→", corrected_colours[i])

# --------------------------------
# 8. Convert corrected colours
#    to CIELAB
# --------------------------------

corrected_colours_uint8 = corrected_colours.astype(np.uint8)

corrected_lab = bgr_to_cielab(
    corrected_colours_uint8
)

# --------------------------------
# 9. Calculate post-calibration ΔE
# --------------------------------

corrected_delta_e = np.linalg.norm(
    known_lab - corrected_lab,
    axis=1
)

print("\n--------------------------------")
print("Post-calibration Delta E76")
print("--------------------------------")

for i in range(len(names)):
    print(
        names[i],
        "→",
        corrected_delta_e[i]
    )

print("\nAverage corrected Delta E76:",
      np.mean(corrected_delta_e))

print("Maximum corrected Delta E76:",
      np.max(corrected_delta_e))



print("--------------------------------")
print("Calibration summary")
print("--------------------------------")

print("Average Delta E76:", average_error)
print("Maximum Delta E76:", maximum_error)