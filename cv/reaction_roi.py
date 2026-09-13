import cv2
import numpy as np

from card_boundary import detect_card_corners


# Canonical reference-card size
CARD_WIDTH = 900
CARD_HEIGHT = 600


# Reaction position in our canonical scene.
#
# The reference card occupies:
# x = 0 → 900
# y = 0 → 600
#
# The reaction is placed to the right of it.
REACTION_ROI = (
    1000, 180,
    1300, 420
)


def create_card_transform(card_corners):
    """
    Create a perspective transform from the
    detected physical card to the canonical card.
    """

    destination = np.array([
        [0, 0],
        [CARD_WIDTH, 0],
        [CARD_WIDTH, CARD_HEIGHT],
        [0, CARD_HEIGHT]
    ], dtype=np.float32)

    transform = cv2.getPerspectiveTransform(
        card_corners.astype(np.float32),
        destination
    )

    return transform


def map_reaction_to_image(
    transform
):
    """
    Map the reaction ROI from canonical
    coordinates back to the original image.
    """

    x1, y1, x2, y2 = REACTION_ROI

    canonical_points = np.array([
        [x1, y1],
        [x2, y1],
        [x2, y2],
        [x1, y2]
    ], dtype=np.float32)

    inverse_transform = np.linalg.inv(
        transform
    )

    image_points = cv2.perspectiveTransform(
        canonical_points.reshape(
            -1, 1, 2
        ),
        inverse_transform
    )

    return image_points.reshape(4, 2)


def process_reaction_frame(image):

    # Detect the actual reference-card boundary
    card_corners = detect_card_corners(
        image
    )

    if card_corners is None:
        return None

    # Build card-based coordinate system
    transform = create_card_transform(
        card_corners
    )

    # Map expected reaction position
    # back into the original camera image
    reaction_points = map_reaction_to_image(
        transform
    )

    return reaction_points