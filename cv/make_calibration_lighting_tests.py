import cv2
import numpy as np


image = cv2.imread(
    "test_capture.png"
)

if image is None:
    raise FileNotFoundError(
        "Could not find test_capture.png"
    )


def apply_lighting(
    image,
    blue_gain,
    green_gain,
    red_gain,
    filename
):

    result = image.astype(
        np.float32
    )

    result[:, :, 0] *= blue_gain
    result[:, :, 1] *= green_gain
    result[:, :, 2] *= red_gain

    result = np.clip(
        result,
        0,
        255
    ).astype(
        np.uint8
    )

    cv2.imwrite(
        filename,
        result
    )

    print(
        f"Created: {filename}"
    )


# 1. Warm lighting
apply_lighting(
    image,
    0.80,
    0.95,
    0.90,
    "lighting_warm.png"
)


# 2. Cool lighting
apply_lighting(
    image,
    0.90,
    0.95,
    0.80,
    "lighting_cool.png"
)


# 3. Darker exposure
apply_lighting(
    image,
    0.65,
    0.65,
    0.65,
    "lighting_dark.png"
)


# 4. Green-biased lighting
apply_lighting(
    image,
    0.90,
    0.80,
    0.90,
    "lighting_green.png"
)


print("\nAll lighting test images created.")