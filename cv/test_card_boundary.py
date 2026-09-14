import cv2

from card_boundary import detect_card_corners


image = cv2.imread(
    "test_capture.png"
)

if image is None:
    raise FileNotFoundError(
        "Could not find test_capture.png"
    )


corners = detect_card_corners(image)


if corners is None:

    print("❌ Card not detected.")

else:

    print("✅ Card detected.")

    print("\nCard corners:")

    print(corners)

    output = image.copy()

    points = corners.astype(int)

    cv2.polylines(
        output,
        [points],
        True,
        (0, 0, 255),
        4
    )

    cv2.imwrite(
        "card_boundary_detected.png",
        output
    )

    print(
        "\nSaved: "
        "card_boundary_detected.png"
    )