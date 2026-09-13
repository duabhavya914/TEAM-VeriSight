import cv2
import numpy as np

from reaction_roi import process_reaction_frame


image = cv2.imread(
    "test_capture.png"
)

if image is None:
    raise FileNotFoundError(
        "Could not find test_capture.png"
    )


reaction_points = process_reaction_frame(
    image
)


if reaction_points is None:

    print("❌ Reference card not detected.")

else:

    print("✅ Reference card detected.")

    print("\nReaction coordinates:")

    print(
        np.round(
            reaction_points,
            2
        )
    )

    output = image.copy()

    polygon = np.int32(
        reaction_points
    )

    # Draw reaction ROI
    cv2.polylines(
        output,
        [polygon],
        True,
        (0, 0, 255),
        4
    )

    cv2.putText(
        output,
        "REACTION ROI",
        tuple(
            polygon[0]
        ),
        cv2.FONT_HERSHEY_SIMPLEX,
        0.8,
        (0, 0, 255),
        2
    )

    cv2.imwrite(
        "reaction_roi_card_based.png",
        output
    )

    print(
        "\nSaved: "
        "reaction_roi_card_based.png"
    )