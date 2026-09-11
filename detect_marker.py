import cv2

aruco = cv2.aruco

dictionary = aruco.getPredefinedDictionary(
    aruco.DICT_4X4_50
)

detector = aruco.ArucoDetector(dictionary)

image = cv2.imread("marker.png")

corners, ids, rejected = detector.detectMarkers(image)

print("IDs:")
print(ids)

print("Corners:")
print(corners)

aruco.drawDetectedMarkers(image, corners, ids)

cv2.imshow("Detected Marker", image)
cv2.waitKey(0)
cv2.destroyAllWindows()

corners, ids, rejected = detector.detectMarkers(image)

if ids is not None:
    print("Detected marker ID:", ids[0])
    print("Four corners:")
    print(corners[0][0])