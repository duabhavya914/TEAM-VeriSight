import cv2

from color_patches import extract_patches


# Load the canonical card
image = cv2.imread("test_canonical_card.png")

if image is None:
    raise FileNotFoundError(
        "Could not find test_canonical_card.png"
    )


# Extract reference patches
patches = extract_patches(image)


# Print information about each patch
for name, patch in patches.items():
    print(
        name,
        "→",
        "size:", patch.shape,
        "average BGR:",
        patch.mean(axis=(0, 1))
    )