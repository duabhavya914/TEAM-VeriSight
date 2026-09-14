import cv2
import numpy as np


# --------------------------------------------------
# Configuration
# --------------------------------------------------

ARUCO_DICTIONARY = cv2.aruco.DICT_4X4_50

CANONICAL_WIDTH = 900
CANONICAL_HEIGHT = 600


# --------------------------------------------------
# ArUco marker detection
# --------------------------------------------------

def detect_marker(image):
    """
    Detect the ArUco marker used to locate the reference card.

    Returns:
        corners: detected marker corners
        ids: detected marker IDs
    """

    dictionary = cv2.aruco.getPredefinedDictionary(
        ARUCO_DICTIONARY
    )

    detector = cv2.aruco.ArucoDetector(dictionary)

    corners, ids, _ = detector.detectMarkers(image)

    return corners, ids


# --------------------------------------------------
# Perspective correction
# --------------------------------------------------

def create_canonical_card(image, marker_corners):
    """
    Convert the photographed reference card into a
    fixed-size canonical representation.
    """

    marker_corners = marker_corners.reshape(4, 2).astype(
        np.float32
    )

    # Expected marker position in canonical image
    destination = np.array([
        [50, 50],
        [230, 50],
        [230, 230],
        [50, 230]
    ], dtype=np.float32)

    transform_matrix = cv2.getPerspectiveTransform(
        marker_corners,
        destination
    )

    canonical = cv2.warpPerspective(
        image,
        transform_matrix,
        (CANONICAL_WIDTH, CANONICAL_HEIGHT)
    )

    return canonical


# --------------------------------------------------
# Complete reference-card detection
# --------------------------------------------------

def process_reference_card(image):
    """
    Detect the reference marker and create a canonical card.

    Returns:
        canonical image if successful
        None if marker is not detected
    """

    corners, ids = detect_marker(image)

    if ids is None:
        return None

    marker_corners = corners[0][0]

    canonical = create_canonical_card(
        image,
        marker_corners
    )

    return canonical