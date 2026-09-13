import numpy as np


REFERENCE_COLORS = np.array([
    [255, 0, 0],      # Blue
    [0, 255, 0],      # Green
    [0, 0, 255],      # Red
    [0, 255, 255],    # Yellow
    [255, 0, 255]     # Magenta
], dtype=np.float32)


# Simulated camera observation

observed = REFERENCE_COLORS.copy()

observed[:, 0] *= 0.85
observed[:, 1] *= 0.90
observed[:, 2] *= 0.80


observed = np.clip(
    observed,
    0,
    255
)


# Estimate channel gains

reference_mean = np.mean(
    REFERENCE_COLORS,
    axis=0
)

observed_mean = np.mean(
    observed,
    axis=0
)


gains = (
    reference_mean /
    observed_mean
)


# Unknown reaction

true_reaction = np.array(
    [[0, 140, 220]],
    dtype=np.float32
)


observed_reaction = true_reaction.copy()

observed_reaction[:, 0] *= 0.85
observed_reaction[:, 1] *= 0.90
observed_reaction[:, 2] *= 0.80


observed_reaction = np.clip(
    observed_reaction,
    0,
    255
)


# Apply channel correction

corrected_reaction = (
    observed_reaction * gains
)

corrected_reaction = np.clip(
    corrected_reaction,
    0,
    255
)


print("\n--- CHANNEL CALIBRATION TEST ---")

print(
    "True reaction:",
    true_reaction[0]
)

print(
    "Observed reaction:",
    observed_reaction[0]
)

print(
    "Estimated gains:",
    gains
)

print(
    "Corrected reaction:",
    corrected_reaction[0]
)