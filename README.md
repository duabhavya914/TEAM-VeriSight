# VeriSight — PARAKH

> AI-assisted digital interpretation and recording of presumptive colourimetric field tests.

## Overview

PARAKH is a mobile application designed to support standardized interpretation and digital documentation of existing colourimetric field-test kits.

The application uses the smartphone camera to capture the test reaction together with a reference colour card, performs image and colour analysis, and classifies the observed result into predefined categories such as:

- Presumptive Positive
- Presumptive Negative
- Inconclusive

The system also creates a tamper-evident digital record containing relevant test metadata.

## Problem

Traditional colourimetric field tests are often interpreted visually by officers. This can introduce subjectivity and makes it difficult to maintain a standardized, searchable and verifiable record of the test.

PARAKH aims to reduce subjective colour interpretation and create a structured digital record of the field-test outcome.

## Proposed Solution

The workflow is:

1. Officer selects the applicable test/protocol.
2. Existing field-test kit produces the colour reaction.
3. Officer captures the test reaction using the live smartphone camera.
4. The reference colour card is detected and used for colour/lighting calibration.
5. The test reaction region is identified and analysed.
6. The system classifies the result as presumptive positive, presumptive negative, or inconclusive.
7. A digital test record is generated.
8. The captured image is hashed using SHA-256.
9. The record is stored in a searchable test log.

## Key Features

- Live camera capture
- Reference colour-card detection
- Lighting and colour calibration
- Test-region detection
- Standardized colour analysis
- Presumptive result classification
- Timestamp and GPS metadata
- Operator identification
- SHA-256 image hashing
- Tamper-evident digital records
- Searchable test history

## Important Scope

PARAKH is intended to support the interpretation and documentation of existing presumptive field tests.

It does **not** replace laboratory confirmation and does not independently prove the presence of a controlled substance.

## Project Status

🚧 Prototype under development.

This repository will contain the application source code, image-processing components, test data, documentation, and prototype implementation developed for the Smart India Hackathon.

## Team

**Team:** VeriSight

**Application:** PARAKH
