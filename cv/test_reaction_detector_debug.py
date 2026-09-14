import cv2

from reaction_container import detect_reaction_container


image = cv2.imread(
    "realistic_lighting.png"
)

if image is None:
    raise FileNotFoundError(
        "Could not find realistic_lighting.png"
    )


print("Image shape:", image.shape)

corners = detect_reaction_container(
    image
)

if corners is None:
    print("\n❌ Reaction container NOT detected.")
else:
    print("\n✅ Reaction container detected.")
    print(corners)