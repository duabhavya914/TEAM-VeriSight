import cv2
import numpy as np

from reaction_container import (
    detect_reaction_container
)

from reaction_color import (
    extract_reaction_region,
    calculate_reaction_color
)


image = cv2.imread(
    "test_capture.png"
)

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

    reaction = extract_reaction_region(
        image,
        corners
    )

    mean_bgr, median_bgr = (
        calculate_reaction_color(
            reaction
        )
    )

    print(
        "\nReaction region size:",
        reaction.shape
    )

    print(
        "\nMean BGR:",
        np.round(mean_bgr, 2)
    )

    print(
        "Median BGR:",
        np.round(median_bgr, 2)
    )

    cv2.imwrite(
        "reaction_color_region.png",
        reaction
    )

    print(
        "\nSaved: "
        "reaction_color_region.png"
    )