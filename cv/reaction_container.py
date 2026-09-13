import cv2
import numpy as np

from reference_card import detect_marker


def detect_reaction_container(image):

    # --------------------------------------------------
    # 1. Detect the ArUco marker
    # --------------------------------------------------

    marker_corners, marker_ids = detect_marker(image)

    marker_center = None

    if marker_ids is not None:

        marker_points = marker_corners[0][0]

        marker_center = np.mean(
            marker_points,
            axis=0
        )


    # --------------------------------------------------
    # 2. Convert image to binary
    # --------------------------------------------------

    gray = cv2.cvtColor(
        image,
        cv2.COLOR_BGR2GRAY
    )

    _, binary = cv2.threshold(
        gray,
        80,
        255,
        cv2.THRESH_BINARY_INV
    )


    # --------------------------------------------------
    # 3. Find contours + hierarchy
    # --------------------------------------------------

    contours, hierarchy = cv2.findContours(
        binary,
        cv2.RETR_TREE,
        cv2.CHAIN_APPROX_SIMPLE
    )

    if hierarchy is None:
        return None

    hierarchy = hierarchy[0]


    # --------------------------------------------------
    # 4. Print hierarchy for debugging
    # --------------------------------------------------

    print("\n--- CONTOUR HIERARCHY ---")

    for i, h in enumerate(hierarchy):

        print(
            f"Contour {i}: "
            f"next={h[0]}, "
            f"previous={h[1]}, "
            f"child={h[2]}, "
            f"parent={h[3]}"
        )


    # --------------------------------------------------
    # 5. Find quadrilateral candidates
    # --------------------------------------------------

    candidates = []

    image_height, image_width = image.shape[:2]

    image_area = image_height * image_width


    for index, contour in enumerate(contours):

        # ----------------------------------------------
        # Area
        # ----------------------------------------------

        area = cv2.contourArea(contour)

        if area < 20000:
            continue

        # Reject contours that are almost the
        # entire camera image.

        if area > image_area * 0.8:
            continue


        # ----------------------------------------------
        # Polygon approximation
        # ----------------------------------------------

        perimeter = cv2.arcLength(
            contour,
            True
        )

        polygon = cv2.approxPolyDP(
            contour,
            0.02 * perimeter,
            True
        )

        # We want a quadrilateral.

        if len(polygon) != 4:
            continue


        polygon = polygon.reshape(
            4,
            2
        )


        # ----------------------------------------------
        # Bounding box
        # ----------------------------------------------

        x, y, w, h = cv2.boundingRect(
            polygon
        )

        if w < 150 or h < 100:
            continue


        # ----------------------------------------------
        # Reject candidates touching image borders
        # ----------------------------------------------

        if x <= 2 or y <= 2:
            continue

        if x + w >= image_width - 2:
            continue

        if y + h >= image_height - 2:
            continue


        # ----------------------------------------------
        # Reject reference-card candidates
        #
        # If the candidate contains the ArUco marker,
        # it belongs to the reference-card region.
        # ----------------------------------------------

        if marker_center is not None:

            inside = cv2.pointPolygonTest(
                polygon.astype(np.float32),
                (
                    float(marker_center[0]),
                    float(marker_center[1])
                ),
                False
            )

            if inside >= 0:
                continue


        # ----------------------------------------------
        # Store candidate
        # ----------------------------------------------

        candidates.append(
            (
                area,
                polygon,
                index
            )
        )


    # --------------------------------------------------
    # 6. No candidates
    # --------------------------------------------------

    if not candidates:

        print("\n❌ No reaction candidates found.")

        return None


    # --------------------------------------------------
    # 7. Print candidates
    # --------------------------------------------------

    print("\n--- CANDIDATES ---")

    for i, candidate in enumerate(candidates):

        area, polygon, contour_index = candidate

        x, y, w, h = cv2.boundingRect(
            polygon
        )

        print(
            f"Candidate {i}: "
            f"contour={contour_index}, "
            f"area={area:.0f}, "
            f"bbox=({x},{y},{w},{h}), "
            f"corners={polygon.tolist()}"
        )


    # --------------------------------------------------
    # 8. Candidate scoring
    # --------------------------------------------------

    def candidate_score(candidate):

        area, polygon, contour_index = candidate

        x, y, w, h = cv2.boundingRect(
            polygon
        )

        # ----------------------------------------------
        # Size score
        # ----------------------------------------------

        area_ratio = area / image_area

        size_score = min(
            area_ratio / 0.15,
            1.0
        )


        # ----------------------------------------------
        # Edge-distance score
        # ----------------------------------------------

        edge_margin = min(
            x,
            y,
            image_width - (x + w),
            image_height - (y + h)
        )

        edge_score = min(
            edge_margin / 50.0,
            1.0
        )


        # ----------------------------------------------
        # Final score
        # ----------------------------------------------

        score = (
            size_score * 0.7
            +
            edge_score * 0.3
        )

        return score


    # --------------------------------------------------
    # 9. Select best candidate
    # --------------------------------------------------

    best = max(
        candidates,
        key=candidate_score
    )

    _, best_polygon, _ = best


    # --------------------------------------------------
    # 10. Return reaction container corners
    # --------------------------------------------------

    return best_polygon.astype(
        np.int32
    )