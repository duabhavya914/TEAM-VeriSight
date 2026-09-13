import cv2
import numpy as np

from calibration import (
    REFERENCE_COLORS,
    calculate_delta_e
)


def calculate_patch_metrics(patches):
    """
    Calculate quality metrics for each reference patch.

    Metrics:
        - mean BGR colour
        - colour variation
        - saturation percentage
        - Delta E from expected reference colour
    """

    names = list(patches.keys())

    observed_colors = np.array([
        patch.mean(axis=(0, 1))
        for patch in patches.values()
    ], dtype=np.float32)

    delta_e = calculate_delta_e(
        observed_colors
    )

    results = {}

    for index, (name, patch) in enumerate(
        patches.items()
    ):

        # ------------------------------------------
        # Colour variation
        # ------------------------------------------

        variation = np.mean(
            np.std(
                patch.astype(np.float32),
                axis=(0, 1)
            )
        )

        # ------------------------------------------
        # Saturation
        # ------------------------------------------

        hsv = cv2.cvtColor(
            patch,
            cv2.COLOR_BGR2HSV
        )

        saturation = hsv[:, :, 1]

        saturation_percentage = (
            np.mean(saturation > 50) * 100
        )

        # ------------------------------------------
        # Store metrics
        # ------------------------------------------

        results[name] = {
            "mean_bgr": observed_colors[index],
            "variation": variation,
            "saturation_percentage":
                saturation_percentage,
            "delta_e": delta_e[index]
        }

    return results


def print_quality_report(results):
    """
    Print a readable quality report.
    """

    print("\n--- REFERENCE CARD QUALITY REPORT ---")

    for name, metrics in results.items():

        print(f"\n{name}")

        print(
            "  Mean BGR:",
            np.round(
                metrics["mean_bgr"],
                2
            )
        )

        print(
            f"  Variation: "
            f"{metrics['variation']:.2f}"
        )

        print(
            f"  Saturation: "
            f"{metrics['saturation_percentage']:.2f}%"
        )

        print(
            f"  Delta E: "
            f"{metrics['delta_e']:.2f}"
        )