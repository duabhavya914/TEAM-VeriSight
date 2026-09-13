import numpy as np


# --------------------------------------------------
# DEVELOPMENT-ONLY THRESHOLD
# --------------------------------------------------
# This value is NOT a validated forensic threshold.
# It is only for our prototype experiments.

MAX_DELTA_E_THRESHOLD = 15.0


def summarize_quality(results):
    """
    Calculate overall reference-card quality metrics.
    """

    delta_values = np.array([
        metrics["delta_e"]
        for metrics in results.values()
    ])

    average_delta_e = float(
        np.mean(delta_values)
    )

    maximum_delta_e = float(
        np.max(delta_values)
    )

    return average_delta_e, maximum_delta_e


def decide_quality(results):
    """
    Prototype quality decision.

    Returns:
        PASS  -> image can proceed to analysis
        RETAKE -> image should be captured again
    """

    average_delta_e, maximum_delta_e = (
        summarize_quality(results)
    )

    if maximum_delta_e > MAX_DELTA_E_THRESHOLD:
        return "RETAKE"

    return "PASS"


def print_quality_summary(results):

    average_delta_e, maximum_delta_e = (
        summarize_quality(results)
    )

    decision = decide_quality(results)

    print("\n--- QUALITY SUMMARY ---")

    print(
        f"Average ΔE: "
        f"{average_delta_e:.2f}"
    )

    print(
        f"Maximum ΔE: "
        f"{maximum_delta_e:.2f}"
    )

    print(
        f"Prototype threshold: "
        f"{MAX_DELTA_E_THRESHOLD:.2f}"
    )

    print(
        f"QUALITY DECISION: "
        f"{decision}"
    )