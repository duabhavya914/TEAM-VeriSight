import cv2
import numpy as np

from reaction_container import (
    detect_reaction_container
)

image = cv2.imread("distorted_3_strong.png")



if image is None:
    raise FileNotFoundError(
        "Could not find test_capture.png"
    )


corners = detect_reaction_container(
    image
)


if corners is None:

    print(
        "❌ Reaction container not detected."
    )

else:

    print(
        "✅ Reaction container detected."
    )

    print("\nCorners:")
    print(corners)

    output = image.copy()

    points = corners.astype(
        np.int32
    )

    cv2.polylines(
        output,
        [points],
        True,
        (0, 0, 255),
        4
    )

    cv2.putText(
        output,
        "REACTION CONTAINER",
        tuple(points[0]),
        cv2.FONT_HERSHEY_SIMPLEX,
        0.7,
        (0, 0, 255),
        2
    )

    cv2.imwrite(
        "reaction_container_detected.png",
        output
    )

    print(
        "\nSaved: "
        "reaction_container_detected.png"
    )