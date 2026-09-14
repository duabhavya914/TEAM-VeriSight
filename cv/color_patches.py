# Reference patch coordinates
# Format: x1, y1, x2, y2
#
# These regions intentionally sample only the
# CENTER of each colour patch.
#
# This avoids:
# - white card background
# - patch borders
# - perspective edges
# - printing/camera blur near boundaries

PATCHES = {

    "Blue": (
        325,
        125,
        395,
        195
    ),

    "Green": (
        525,
        125,
        595,
        195
    ),

    "Red": (
        725,
        125,
        795,
        195
    ),

    "Yellow": (
        325,
        375,
        395,
        445
    ),

    "Magenta": (
        525,
        375,
        595,
        445
    ),
}


def extract_patches(image):
    """
    Extract the five centre reference colour regions
    from the canonical 900 x 600 card.
    """

    extracted = {}

    for name, (
        x1,
        y1,
        x2,
        y2
    ) in PATCHES.items():

        patch = image[
            y1:y2,
            x1:x2
        ]

        if patch.size == 0:

            raise ValueError(
                f"Could not extract patch: {name}"
            )

        extracted[name] = patch

    return extracted