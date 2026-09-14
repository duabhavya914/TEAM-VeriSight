import cv2
import numpy as np


# ==============================================================
# REFERENCE COLOURS
# ==============================================================

# OpenCV uses BGR order.
#
# These are prototype reference values obtained from repeated
# captures of the physical printed reference card.
#
# Final production values should eventually be established using
# controlled measurements of the final physical reference card.

REFERENCE_COLORS = np.array(
    [
        [223, 46, 14],      # Blue
        [26, 181, 37],      # Green
        [47, 52, 213],      # Red
        [24, 202, 201],     # Yellow
        [228, 73, 207]      # Magenta
    ],
    dtype=np.float32
)


# ==============================================================
# REGULARIZATION SETTINGS
# ==============================================================

# Higher value:
#     correction stays closer to original captured colours
#
# Lower value:
#     correction is allowed to become more aggressive
#
# For only five patches, we deliberately use regularization
# because an unrestricted affine transform can overfit badly.

COLOR_TRANSFORM_REGULARIZATION = 2.0


# Maximum allowed absolute coefficient for cross-channel terms.
#
# This prevents one captured channel from having an extreme
# influence on another output channel.

MAX_CROSS_CHANNEL_COEFFICIENT = 0.60


# Maximum allowed offset in normalized 0-1 colour space.
#
# 0.20 corresponds to roughly 51 RGB/BGR intensity units.

MAX_NORMALIZED_BIAS = 0.20


# ==============================================================
# ESTIMATE STABLE COLOUR CORRECTION
# ==============================================================

def estimate_color_transform(
    observed_colors
):
    """
    Estimate a REGULARIZED 3x4 affine colour transformation.

    Input:
        observed_colors:
            5 x 3 array containing observed BGR values.

    Output:
        3 x 4 transformation matrix.

    WHY REGULARIZATION?

    The previous version fitted a completely free affine
    transform from only five patches.

    That gave the model too much freedom and caused small
    differences between captures to create very different
    correction matrices.

    This version keeps the transform closer to the identity
    transform unless the reference patches strongly support
    a larger correction.
    """

    observed_colors = np.asarray(
        observed_colors,
        dtype=np.float32
    )


    if (
        observed_colors.shape
        !=
        REFERENCE_COLORS.shape
    ):

        raise ValueError(
            "Observed colours must contain exactly "
            "five BGR reference colours."
        )


    # ==========================================================
    # NORMALIZE TO 0-1
    # ==========================================================

    observed_normalized = (
        observed_colors
        /
        255.0
    )


    reference_normalized = (
        REFERENCE_COLORS
        /
        255.0
    )


    # ==========================================================
    # BUILD DESIGN MATRIX
    #
    # [B G R 1]
    # ==========================================================

    design_matrix = np.hstack(
        [
            observed_normalized,

            np.ones(
                (
                    len(
                        observed_normalized
                    ),
                    1
                ),
                dtype=np.float32
            )
        ]
    )


    # ==========================================================
    # IDENTITY TRANSFORM
    #
    # We want calibration to BEGIN from:
    #
    # corrected B = B
    # corrected G = G
    # corrected R = R
    #
    # rather than beginning from an unconstrained matrix.
    # ==========================================================

    identity_transform = np.array(
        [
            [1.0, 0.0, 0.0, 0.0],
            [0.0, 1.0, 0.0, 0.0],
            [0.0, 0.0, 1.0, 0.0]
        ],
        dtype=np.float32
    )


    # Matrix orientation used during solving:
    #
    # 4 x 3 instead of 3 x 4

    identity_weights = (
        identity_transform.T
    )


    # ==========================================================
    # RIDGE REGULARIZATION
    # ==========================================================

    regularization = (
        COLOR_TRANSFORM_REGULARIZATION
    )


    regularization_matrix = (
        np.eye(
            4,
            dtype=np.float32
        )
        *
        regularization
    )


    # ----------------------------------------------------------
    # Bias should be allowed slightly more freedom than the
    # colour-channel coefficients.
    # ----------------------------------------------------------

    regularization_matrix[
        3,
        3
    ] = (
        regularization
        *
        0.25
    )


    # ==========================================================
    # SOLVE:
    #
    # (XᵀX + λI)W
    # =
    # XᵀY + λW₀
    #
    # W₀ = identity transform
    # ==========================================================

    left_side = (
        design_matrix.T
        @
        design_matrix
        +
        regularization_matrix
    )


    right_side = (
        design_matrix.T
        @
        reference_normalized
        +
        regularization_matrix
        @
        identity_weights
    )


    weights = np.linalg.solve(
        left_side,
        right_side
    )


    transform = (
        weights.T
    )


    # ==========================================================
    # SAFETY LIMITS
    # ==============================================================

    # ----------------------------------------------------------
    # Restrict cross-channel coefficients.
    #
    # Example:
    #
    # output Blue depending massively on input Red
    # should not be allowed.
    # ----------------------------------------------------------

    for output_channel in range(
        3
    ):

        for input_channel in range(
            3
        ):

            if (
                output_channel
                !=
                input_channel
            ):

                transform[
                    output_channel,
                    input_channel
                ] = np.clip(
                    transform[
                        output_channel,
                        input_channel
                    ],
                    -MAX_CROSS_CHANNEL_COEFFICIENT,
                    MAX_CROSS_CHANNEL_COEFFICIENT
                )


    # ----------------------------------------------------------
    # Prevent unreasonable bias offsets.
    # ----------------------------------------------------------

    transform[
        :,
        3
    ] = np.clip(
        transform[
            :,
            3
        ],
        -MAX_NORMALIZED_BIAS,
        MAX_NORMALIZED_BIAS
    )


    print(
        "\n========== REGULARIZED COLOUR TRANSFORM =========="
    )


    print(
        transform
    )


    return transform


# ==============================================================
# APPLY COLOUR CORRECTION
# ==============================================================

def apply_color_transform(
    colors,
    transform
):
    """
    Apply the regularized colour transformation.

    Input colours are BGR values in the 0-255 range.

    The transform itself operates in normalized 0-1 colour space.

    Returns corrected BGR colours in the 0-255 range.
    """

    colors = np.asarray(
        colors,
        dtype=np.float32
    )


    if (
        colors.ndim != 2
        or
        colors.shape[1] != 3
    ):

        raise ValueError(
            "Colours must have shape N x 3."
        )


    transform = np.asarray(
        transform,
        dtype=np.float32
    )


    if (
        transform.shape
        !=
        (3, 4)
    ):

        raise ValueError(
            "Colour transform must have shape 3 x 4."
        )


    # ==========================================================
    # NORMALIZE INPUT
    # ==========================================================

    normalized_colors = (
        colors
        /
        255.0
    )


    # ==========================================================
    # ADD CONSTANT BIAS COLUMN
    #
    # [B G R 1]
    # ==========================================================

    design_matrix = np.hstack(
        [
            normalized_colors,

            np.ones(
                (
                    len(
                        normalized_colors
                    ),
                    1
                ),
                dtype=np.float32
            )
        ]
    )


    # ==========================================================
    # APPLY TRANSFORM
    # ==========================================================

    corrected_normalized = (
        design_matrix
        @
        transform.T
    )


    corrected_normalized = np.clip(
        corrected_normalized,
        0.0,
        1.0
    )


    # ==========================================================
    # RETURN TO 0-255
    # ==========================================================

    corrected = (
        corrected_normalized
        *
        255.0
    )


    corrected = np.clip(
        corrected,
        0,
        255
    )


    return corrected.astype(
        np.float32
    )


# ==============================================================
# BGR → CIELAB
# ==============================================================

def bgr_to_lab(
    colors
):
    """
    Convert N x 3 BGR colours to OpenCV CIELAB values.
    """

    colors = np.asarray(
        colors,
        dtype=np.float32
    )


    image = colors.reshape(
        (
            -1,
            1,
            3
        )
    )


    image = np.clip(
        image,
        0,
        255
    ).astype(
        np.uint8
    )


    lab = cv2.cvtColor(
        image,
        cv2.COLOR_BGR2LAB
    )


    return lab.reshape(
        (
            -1,
            3
        )
    ).astype(
        np.float32
    )


# ==============================================================
# DELTA E 76
# ==============================================================

def calculate_delta_e(
    observed_colors
):
    """
    Calculate CIE76 Delta E between observed colours and the
    known reference-card colours.
    """

    observed_colors = np.asarray(
        observed_colors,
        dtype=np.float32
    )


    if (
        observed_colors.shape
        !=
        REFERENCE_COLORS.shape
    ):

        raise ValueError(
            "Delta E requires exactly five BGR reference colours."
        )


    observed_lab = bgr_to_lab(
        observed_colors
    )


    reference_lab = bgr_to_lab(
        REFERENCE_COLORS
    )


    delta_e = np.linalg.norm(
        reference_lab
        -
        observed_lab,
        axis=1
    )


    return delta_e


# ==============================================================
# LEGACY CHANNEL-GAIN FUNCTIONS
# ==============================================================

# These are kept because other experimental scripts may still
# import them.
#
# analyze_test.py currently uses the regularized affine transform,
# not these functions.


def estimate_channel_gains(
    observed_colors
):

    observed_colors = np.asarray(
        observed_colors,
        dtype=np.float32
    )


    if (
        observed_colors.shape
        !=
        REFERENCE_COLORS.shape
    ):

        raise ValueError(
            "Observed colours must contain exactly "
            "five BGR reference colours."
        )


    gains = []


    for channel in range(
        3
    ):


        reference_values = (
            REFERENCE_COLORS[
                :,
                channel
            ]
        )


        observed_values = (
            observed_colors[
                :,
                channel
            ]
        )


        valid = (
            (
                reference_values
                >
                20
            )
            &
            (
                observed_values
                >
                1
            )
        )


        if not np.any(
            valid
        ):

            raise ValueError(
                (
                    "No valid reference values "
                    f"for channel {channel}."
                )
            )


        ratios = (
            reference_values[
                valid
            ]
            /
            observed_values[
                valid
            ]
        )


        gain = np.median(
            ratios
        )


        gains.append(
            gain
        )


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


    if (
        gains.shape
        !=
        (3,)
    ):

        raise ValueError(
            "Gains must contain exactly three BGR values."
        )


    corrected = (
        colors
        *
        gains
    )


    corrected = np.clip(
        corrected,
        0,
        255
    )


    return corrected.astype(
        np.float32
    )