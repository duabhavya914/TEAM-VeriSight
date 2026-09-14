package com.sih.drugtest.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SplashScreen() {

    val deepNavy = Color(0xFF001A3A)
    val navyBlue = Color(0xFF002B62)
    val brightBlue = Color(0xFF075ABF)

    val cyan = Color(0xFF16E0F7)
    val ivory = Color(0xFFF5F1E8)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        brightBlue,
                        navyBlue,
                        deepNavy
                    ),
                    center = Offset(180f, 140f),
                    radius = 1500f
                )
            )
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            val ringColor =
                Color(0xFF0E63D8).copy(alpha = 0.32f)

            drawArc(
                color = ringColor.copy(alpha = 0.16f),
                startAngle = 0f,
                sweepAngle = 100f,
                useCenter = false,
                topLeft = Offset(
                    -size.width * 0.60f,
                    -size.width * 0.58f
                ),
                size = Size(
                    size.width * 1.18f,
                    size.width * 1.18f
                ),
                style = Stroke(width = 54f)
            )

            drawArc(
                color = ringColor,
                startAngle = 0f,
                sweepAngle = 100f,
                useCenter = false,
                topLeft = Offset(
                    -size.width * 0.48f,
                    -size.width * 0.48f
                ),
                size = Size(
                    size.width * 0.92f,
                    size.width * 0.92f
                ),
                style = Stroke(width = 3f)
            )

            drawArc(
                color = ringColor.copy(alpha = 0.14f),
                startAngle = 180f,
                sweepAngle = 100f,
                useCenter = false,
                topLeft = Offset(
                    size.width * 0.44f,
                    size.height * 0.67f
                ),
                size = Size(
                    size.width * 1.02f,
                    size.width * 1.02f
                ),
                style = Stroke(width = 48f)
            )

            drawArc(
                color = ringColor,
                startAngle = 180f,
                sweepAngle = 100f,
                useCenter = false,
                topLeft = Offset(
                    size.width * 0.56f,
                    size.height * 0.74f
                ),
                size = Size(
                    size.width * 0.78f,
                    size.width * 0.78f
                ),
                style = Stroke(width = 3f)
            )
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            FieldTestSecureLogoMark(
                modifier = Modifier.size(190.dp)
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "FieldTest",
                    color = ivory,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "Secure",
                    color = cyan,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text =
                    "C A P T U R E   •   C A L I B R A T E   •   V E R I F Y",
                color = Color(0xFFE8EDF3),
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp
            )
        }
    }
}


@Composable
private fun FieldTestSecureLogoMark(
    modifier: Modifier = Modifier
) {

    Canvas(
        modifier = modifier
    ) {

        val w = size.width
        val h = size.height

        val cyan =
            Color(0xFF16E0F7)

        val white =
            Color(0xFFF6F7FA)

        val purple =
            Color(0xFF7D3CFF)

        val aqua =
            Color(0xFF1FE3F2)

        val yellow =
            Color(0xFFFFCD1F)

        val light =
            Color(0xFFE9EDF4)


        // =====================================================
        // SOFT GLOW
        // =====================================================

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF0B8CFF)
                        .copy(alpha = 0.25f),

                    Color(0xFF0B8CFF)
                        .copy(alpha = 0.08f),

                    Color.Transparent
                )
            ),
            radius = w * 0.36f,
            center = Offset(
                w / 2f,
                h * 0.45f
            )
        )


        // =====================================================
        // CORNER BRACKETS
        // =====================================================

        val bracketStroke =
            w * 0.045f

        val bracketLength =
            w * 0.14f

        val leftX =
            w * 0.10f

        val rightX =
            w * 0.90f

        val topY =
            h * 0.12f

        val bottomY =
            h * 0.88f


        // TOP LEFT

        drawLine(
            color = white,
            start = Offset(
                leftX,
                topY + bracketLength
            ),
            end = Offset(
                leftX,
                topY
            ),
            strokeWidth = bracketStroke,
            cap = StrokeCap.Round
        )

        drawLine(
            color = white,
            start = Offset(
                leftX,
                topY
            ),
            end = Offset(
                leftX + bracketLength,
                topY
            ),
            strokeWidth = bracketStroke,
            cap = StrokeCap.Round
        )


        // TOP RIGHT

        drawLine(
            color = white,
            start = Offset(
                rightX - bracketLength,
                topY
            ),
            end = Offset(
                rightX,
                topY
            ),
            strokeWidth = bracketStroke,
            cap = StrokeCap.Round
        )

        drawLine(
            color = white,
            start = Offset(
                rightX,
                topY
            ),
            end = Offset(
                rightX,
                topY + bracketLength
            ),
            strokeWidth = bracketStroke,
            cap = StrokeCap.Round
        )


        // BOTTOM LEFT

        drawLine(
            color = white,
            start = Offset(
                leftX,
                bottomY - bracketLength
            ),
            end = Offset(
                leftX,
                bottomY
            ),
            strokeWidth = bracketStroke,
            cap = StrokeCap.Round
        )

        drawLine(
            color = white,
            start = Offset(
                leftX,
                bottomY
            ),
            end = Offset(
                leftX + bracketLength,
                bottomY
            ),
            strokeWidth = bracketStroke,
            cap = StrokeCap.Round
        )


        // BOTTOM RIGHT

        drawLine(
            color = white,
            start = Offset(
                rightX - bracketLength,
                bottomY
            ),
            end = Offset(
                rightX,
                bottomY
            ),
            strokeWidth = bracketStroke,
            cap = StrokeCap.Round
        )

        drawLine(
            color = white,
            start = Offset(
                rightX,
                bottomY - bracketLength
            ),
            end = Offset(
                rightX,
                bottomY
            ),
            strokeWidth = bracketStroke,
            cap = StrokeCap.Round
        )


        // =====================================================
        // CLASSIC SECURITY SHIELD
        // =====================================================

        val shield =
            Path().apply {

                // Top center point
                moveTo(
                    w * 0.50f,
                    h * 0.27f
                )

                // Curved upper-right shoulder
                cubicTo(
                    w * 0.56f,
                    h * 0.31f,

                    w * 0.63f,
                    h * 0.33f,

                    w * 0.69f,
                    h * 0.33f
                )

                // Right upper side
                cubicTo(
                    w * 0.69f,
                    h * 0.43f,

                    w * 0.68f,
                    h * 0.51f,

                    w * 0.65f,
                    h * 0.57f
                )

                // Right lower taper
                cubicTo(
                    w * 0.62f,
                    h * 0.63f,

                    w * 0.56f,
                    h * 0.68f,

                    w * 0.50f,
                    h * 0.71f
                )

                // Left lower taper
                cubicTo(
                    w * 0.44f,
                    h * 0.68f,

                    w * 0.38f,
                    h * 0.63f,

                    w * 0.35f,
                    h * 0.57f
                )

                // Left upper side
                cubicTo(
                    w * 0.32f,
                    h * 0.51f,

                    w * 0.31f,
                    h * 0.43f,

                    w * 0.31f,
                    h * 0.33f
                )

                // Curved upper-left shoulder
                cubicTo(
                    w * 0.37f,
                    h * 0.33f,

                    w * 0.44f,
                    h * 0.31f,

                    w * 0.50f,
                    h * 0.27f
                )

                close()
            }


        drawPath(
            path = shield,
            color = cyan,
            style = Stroke(
                width = w * 0.032f,
                cap = StrokeCap.Round
            )
        )


        // =====================================================
        // CHECKMARK
        // =====================================================

        drawLine(
            color = cyan,
            start = Offset(
                w * 0.40f,
                h * 0.48f
            ),
            end = Offset(
                w * 0.48f,
                h * 0.56f
            ),
            strokeWidth = w * 0.034f,
            cap = StrokeCap.Round
        )

        drawLine(
            color = cyan,
            start = Offset(
                w * 0.48f,
                h * 0.56f
            ),
            end = Offset(
                w * 0.61f,
                h * 0.42f
            ),
            strokeWidth = w * 0.034f,
            cap = StrokeCap.Round
        )


        // =====================================================
        // COLOUR PATCHES
        // =====================================================

        val squareSize =
            w * 0.070f

        val squareGap =
            w * 0.045f

        val totalWidth =
            squareSize * 4 +
                    squareGap * 3

        val startX =
            (w - totalWidth) / 2f

        val squareY =
            h * 0.76f


        val colors =
            listOf(
                purple,
                aqua,
                yellow,
                light
            )


        colors.forEachIndexed { index, color ->

            drawRect(
                color = color,
                topLeft = Offset(
                    startX +
                            index *
                            (
                                    squareSize +
                                            squareGap
                                    ),
                    squareY
                ),
                size = Size(
                    squareSize,
                    squareSize
                )
            )
        }
    }
}