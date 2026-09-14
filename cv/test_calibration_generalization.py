import numpy as np

from calibration import (
    REFERENCE_COLORS,
    estimate_color_transform,
    apply_color_transform
)


# --------------------------------------------------
# Reference colours
# --------------------------------------------------

reference_colors = REFERENCE_COLORS.copy()


# --------------------------------------------------
# Simulated camera observation of reference card
# --------------------------------------------------

observed_reference = reference_colors.copy()

observed_reference[:, 0] *= 0.75
observed_reference[:, 1] *= 1.00
observed_reference[:, 2] *= 0.90

observed_reference = np.clip(
    observed_reference,
    0,
    255
)


# --------------------------------------------------
# Learn calibration transform
# --------------------------------------------------

transform = estimate_color_transform(
    observed_reference
)


# --------------------------------------------------
# Define an UNKNOWN reaction colour
# --------------------------------------------------

true_reaction = np.array(
    [[0, 140, 220]],
    dtype=np.float32
)


# --------------------------------------------------
# Simulate the same camera colour cast
# --------------------------------------------------

observed_reaction = true_reaction.copy()

observed_reaction[:, 0] *= 0.75
observed_reaction[:, 1] *= 1.00
observed_reaction[:, 2] *= 0.90

observed_reaction = np.clip(
    observed_reaction,
    0,
    255
)


# --------------------------------------------------
# Apply calibration
# --------------------------------------------------

corrected_reaction = apply_color_transform(
    observed_reaction,
    transform
)


# --------------------------------------------------
# Print results
# --------------------------------------------------

print("\n--- CALIBRATION GENERALIZATION TEST ---")

print(
    "True reaction colour:",
    true_reaction[0]
)

print(
    "Observed reaction colour:",
    observed_reaction[0]
)

print(
    "Corrected reaction colour:",
    corrected_reaction[0]
)