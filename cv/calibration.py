import cv2
import numpy as np


# --------------------------------------------------
# Reference colours
# --------------------------------------------------
# OpenCV uses BGR order.
#
# These values currently represent the known colours
# of our prototype reference card.
#
# IMPORTANT:
# For the final physical card, these should be
# replaced with measured/reference values obtained
# from the actual printed card.

REFERENCE_COLORS = np.array([
    [255, 0, 0],      # Blue
    [0, 255, 0],      # Green
    [0, 0, 255],      # Red
    [0, 255, 255],    # Yellow
    [255, 0, 255]     # Magenta
], dtype=np.float32)


# --------------------------------------------------
# Estimate colour correction
# --------------------------------------------------

def estimate_color_transform(observed_colors):
    """
    Estimate a 3x4 affine colour transformation.

    Input:
        observed_colors:
            Nx3 array of observed BGR colours.

    Output:
        3x4 transformation matrix.
    """

    observed_colors = np.asarray(
        observed_colors,
        dtype=np.float32
    )

    if observed_colors.shape != REFERENCE_COLORS.shape:
        raise ValueError(
            "Observed colours must contain exactly "
            "five BGR reference colours."
        )

    # Add a constant column for brightness/bias correction.
    #
    # [B G R 1]
    #
    design_matrix = np.hstack([
        observed_colors,
        np.ones((len(observed_colors), 1), dtype=np.float32)
    ])

    # Solve:
    #
    # observed × transform ≈ reference
    #
    transform, _, _, _ = np.linalg.lstsq(
        design_matrix,
        REFERENCE_COLORS,
        rcond=None
    )

    return transform.T


# --------------------------------------------------
# Apply colour correction
# --------------------------------------------------

def apply_color_transform(colors, transform):
    """
    Apply the estimated colour transformation.

    Returns corrected BGR colours in range 0–255.
    """

    colors = np.asarray(
        colors,
        dtype=np.float32
    )

    design_matrix = np.hstack([
        colors,
        np.ones((len(colors), 1), dtype=np.float32)
    ])

    corrected = design_matrix @ transform.T

    corrected = np.clip(
        corrected,
        0,
        255
    )

    return corrected


# --------------------------------------------------
# Convert BGR colours to CIELAB
# --------------------------------------------------

def bgr_to_lab(colors):
    """
    Convert Nx3 BGR colours to CIELAB.
    """

    colors = np.asarray(
        colors,
        dtype=np.float32
    )

    image = colors.reshape(
        (-1, 1, 3)
    )

    image = np.clip(
        image,
        0,
        255
    ).astype(np.uint8)

    lab = cv2.cvtColor(
        image,
        cv2.COLOR_BGR2LAB
    )

    return lab.reshape(
        (-1, 3)
    ).astype(np.float32)


# --------------------------------------------------
# Calculate Delta E 76
# --------------------------------------------------

def calculate_delta_e(observed_colors):
    """
    Calculate CIE76 Delta E between the observed
    colours and the known reference colours.
    """

    observed_lab = bgr_to_lab(
        observed_colors
    )

    reference_lab = bgr_to_lab(
        REFERENCE_COLORS
    )

    delta_e = np.linalg.norm(
        reference_lab - observed_lab,
        axis=1
    )

    return delta_e
def estimate_channel_gains(observed_colors):

    observed_colors = np.asarray(
        observed_colors,
        dtype=np.float32
    )

    if observed_colors.shape != REFERENCE_COLORS.shape:
        raise ValueError(
            "Observed colours must contain exactly "
            "five BGR reference colours."
        )

    gains = []

    for channel in range(3):

        reference_values = (
            REFERENCE_COLORS[:, channel]
        )

        observed_values = (
            observed_colors[:, channel]
        )

        valid = (
            (reference_values > 20)
            &
            (observed_values > 1)
        )

        if not np.any(valid):
            raise ValueError(
                f"No valid reference values "
                f"for channel {channel}."
            )

        ratios = (
            reference_values[valid]
            /
            observed_values[valid]
        )

        gain = np.median(ratios)

        gains.append(gain)

    return np.array(
        gains,
        dtype=np.float32
    )


def apply_channel_gains(
    colors,
    gains
):

    colors = np.asarray(
        colors,
        dtype=np.float32
    )

    gains = np.asarray(
        gains,
        dtype=np.float32
    )

    if gains.shape != (3,):
        raise ValueError(
            "Gains must contain exactly "
            "three BGR values."
        )

    corrected = colors * gains

    corrected = np.clip(
        corrected,
        0,
        255
    )

    return corrected