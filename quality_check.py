import cv2
import numpy as np


# ============================================================
# 1. LOAD IMAGE
# ============================================================

image = cv2.imread("bad_image.png")

if image is None:
    print("Could not load image.")
    exit()


# ============================================================
# 2. DEFINE REFERENCE PATCHES
# ============================================================

patches = {
    "Blue": (300, 100, 420, 220),
    "Green": (500, 100, 620, 220),
    "Red": (700, 100, 820, 220),
    "Yellow": (300, 350, 420, 470),
    "Magenta": (500, 350, 620, 470)
}


# ============================================================
# 3. KNOWN REFERENCE COLOURS
# ============================================================

known_colours = np.array([
    [255, 0, 0],      # Blue
    [0, 255, 0],      # Green
    [0, 0, 255],      # Red
    [0, 255, 255],    # Yellow
    [255, 0, 255]     # Magenta
], dtype=np.float32)


# ============================================================
# 4. SAMPLE OBSERVED COLOURS
# ============================================================

observed_colours = []

for name, (x1, y1, x2, y2) in patches.items():

    patch = image[y1:y2, x1:x2].astype(np.float32)

    mean_colour = np.mean(patch, axis=(0, 1))

    observed_colours.append(mean_colour)


observed_colours = np.array(observed_colours)

print("\nObserved colours:")
for i, name in enumerate(patches):
    print(name, "→", observed_colours[i])


# ============================================================
# 5. SATURATION CHECK
# ============================================================

print("\nPatch saturation:")

threshold = 250

saturation_values = {}

for name, (x1, y1, x2, y2) in patches.items():

    patch = image[y1:y2, x1:x2]

    saturated = np.all(
        patch >= threshold,
        axis=2
    )

    percentage = (
        np.sum(saturated) /
        saturated.size
    ) * 100

    saturation_values[name] = percentage

    print(
        name,
        "→",
        round(percentage, 2),
        "%"
    )


# ============================================================
# 6. PATCH UNIFORMITY
# ============================================================

print("\nPatch variation:")

variation_values = {}

for name, (x1, y1, x2, y2) in patches.items():

    patch = image[y1:y2, x1:x2].astype(np.float32)

    std = np.std(
        patch,
        axis=(0, 1)
    )

    variation = np.mean(std)

    variation_values[name] = variation

    print(
        name,
        "→",
        round(variation, 2)
    )


# ============================================================
# 7. CALCULATE CHANNEL CORRECTION
# ============================================================

known_float = known_colours

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

    correction.append(
        np.mean(ratios)
    )

correction = np.array(correction)

print("\nCalibration correction:")
print(correction)


# ============================================================
# 8. APPLY CORRECTION
# ============================================================

corrected_colours = (
    observed_colours * correction
)

corrected_colours = np.clip(
    corrected_colours,
    0,
    255
)

print("\nCorrected colours:")

for i, name in enumerate(patches):

    print(
        name,
        "→",
        corrected_colours[i]
    )


# ============================================================
# 9. CONVERT BGR → CIE LAB
# ============================================================

def opencv_lab_to_cielab(lab):

    lab = lab.astype(np.float32)

    L = lab[0] * 100 / 255
    a = lab[1] - 128
    b = lab[2] - 128

    return np.array([
        L,
        a,
        b
    ])


def bgr_to_cielab(bgr):

    colour = np.array(
        [[bgr]],
        dtype=np.uint8
    )

    lab = cv2.cvtColor(
        colour,
        cv2.COLOR_BGR2LAB
    )[0, 0]

    return opencv_lab_to_cielab(lab)


# ============================================================
# 10. CALCULATE ΔE
# ============================================================

def delta_e(colour1, colour2):

    return np.sqrt(
        np.sum(
            (colour1 - colour2) ** 2
        )
    )


print("\nCalibration residual:")

delta_e_values = {}

for i, name in enumerate(patches):

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

    delta_e_values[name] = error

    print(
        name,
        "→ ΔE =",
        round(error, 2)
    )


# ============================================================
# 11. OVERALL METRICS
# ============================================================

average_delta_e = np.mean(
    list(delta_e_values.values())
)

maximum_delta_e = np.max(
    list(delta_e_values.values())
)

worst_variation_patch = max(
    variation_values,
    key=variation_values.get
)

worst_delta_e_patch = max(
    delta_e_values,
    key=delta_e_values.get
)


print("\n================================")
print("QUALITY REPORT")
print("================================")

print(
    "Average ΔE:",
    round(average_delta_e, 2)
)

print(
    "Maximum ΔE:",
    round(maximum_delta_e, 2)
)

print(
    "Worst variation:",
    worst_variation_patch,
    "→",
    round(
        variation_values[worst_variation_patch],
        2
    )
)

print(
    "Worst colour error:",
    worst_delta_e_patch,
    "→",
    round(
        delta_e_values[worst_delta_e_patch],
        2
    )
)

# ============================================================
# 12. QUALITY GATE
# ============================================================

print("\n================================")
print("QUALITY GATE")
print("================================")

# Development-only thresholds
SATURATION_LIMIT = 5.0
VARIATION_LIMIT = 20.0
DELTA_E_LIMIT = 15.0

patch_status = {}

for name in patches:

    saturation = saturation_values[name]
    variation = variation_values[name]
    error = delta_e_values[name]

    problems = []

    if saturation > SATURATION_LIMIT:
        problems.append("saturation")

    if variation > VARIATION_LIMIT:
        problems.append("non-uniform colour")

    if error > DELTA_E_LIMIT:
        problems.append("high colour error")

    if len(problems) == 0:
        status = "PASS"
    else:
        status = "FAIL"

    patch_status[name] = status

    print(
        name,
        "→",
        status,
        problems
    )


# ============================================================
# 13. OVERALL DECISION
# ============================================================

failed_patches = [
    name
    for name, status in patch_status.items()
    if status == "FAIL"
]

if len(failed_patches) == 0:

    overall_status = "PASS"

else:

    overall_status = "FAIL"

print("\nOverall image quality:", overall_status)

if failed_patches:

    print(
        "Problematic patches:",
        failed_patches
    )

    print(
        "Recommendation: RETAKE IMAGE"
    )

else:

    print(
        "Recommendation: PROCEED TO REACTION ANALYSIS"
    )