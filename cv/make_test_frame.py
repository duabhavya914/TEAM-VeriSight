import cv2
import numpy as np

# Load our existing canonical reference card
card = cv2.imread("test_canonical_card.png")

if card is None:
    raise FileNotFoundError(
        "Could not find test_canonical_card.png"
    )

# Create a larger frame
# 1400 wide × 600 high
frame = np.ones(
    (600, 1400, 3),
    dtype=np.uint8
) * 240

# Put reference card on the left
frame[:, :900] = card

# -----------------------------
# Fake test-reaction area
# -----------------------------

# Reaction container
cv2.rectangle(
    frame,
    (1000, 180),
    (1300, 420),
    (0, 0, 0),
    3
)

# Fake chemical reaction colour
# BGR = orange
cv2.rectangle(
    frame,
    (1020, 200),
    (1280, 400),
    (0, 140, 255),
    -1
)

# Label
cv2.putText(
    frame,
    "TEST REACTION",
    (1000, 460),
    cv2.FONT_HERSHEY_SIMPLEX,
    0.8,
    (0, 0, 0),
    2
)

# Save
cv2.imwrite(
    "test_capture.png",
    frame
)

print("Created test_capture.png")
print("Frame size:", frame.shape)