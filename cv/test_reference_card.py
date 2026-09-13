import cv2

from reference_card import process_reference_card


# Load an existing reference-card image
image = cv2.imread("perspective_card.png")

if image is None:
    raise FileNotFoundError(
    "Could not find perspective_card.png"
)


# Process the card
canonical = process_reference_card(image)


if canonical is None:
    print("❌ Reference card / ArUco marker not detected.")
else:
    print("✅ Reference card detected.")
    print("Canonical image size:", canonical.shape)

    cv2.imwrite(
        "test_canonical_card.png",
        canonical
    )

    print("Saved: test_canonical_card.png")