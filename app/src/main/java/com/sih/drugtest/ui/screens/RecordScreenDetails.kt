package com.sih.drugtest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/**
 * DATA FOR RECORD DETAILS SCREEN
 *
 * All record-specific information comes from this class.
 *
 * Nothing such as:
 * "Marquis Test"
 * "87%"
 * "IN-TN-1047"
 * "12.9716° N, 77.5946° E"
 *
 * is hardcoded inside the UI.
 */
data class RecordDetailsData(
    val recordId: String,
    val testName: String,
    val result: String,
    val confidence: String,
    val dateTime: String,
    val operatorId: String,
    val location: String,
    val imageSha256: String,
    val protocolVersion: String,
    val verificationStatus: String = "Verified"
)


/**
 * RECORD DETAILS SCREEN
 */
@Composable
fun RecordDetailsScreen(
    data: RecordDetailsData,

    onBackClick: () -> Unit,

    onViewEvidenceClick: () -> Unit,

    onShareReportClick: () -> Unit,

    onExportPdfClick: () -> Unit,

    onCopyLocationClick: () -> Unit = {},

    onCopyHashClick: () -> Unit = {},

    onLocationClick: () -> Unit = {}
) {

    val backgroundColor = Color(0xFFF8FAFC)

    val navy = Color(0xFF102B52)

    val secondaryText = Color(0xFF65738A)

    val borderColor = Color(0xFFE4E8EE)

    val buttonBackground = Color(0xFFF0F4FA)

    val verifiedBackground = Color(0xFFDDF4E5)

    val verifiedText = Color(0xFF249653)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        /*
         * HEADER
         */

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBackClick
            ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = navy,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "Record Details",
                color = navy,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            /*
             * VERIFICATION STATUS
             *
             * This comes from data.verificationStatus.
             */

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(verifiedBackground)
                    .padding(
                        horizontal = 13.dp,
                        vertical = 7.dp
                    )
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = verifiedText,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(4.dp)
                    )

                    Text(
                        text = data.verificationStatus,
                        color = verifiedText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }


        /*
         * MAIN CONTENT
         */

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),

            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 4.dp,
                bottom = 24.dp
            ),

            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {


            /*
             * RECORD INFORMATION CARD
             */

            item {

                RecordInformationCard(
                    data = data,
                    navy = navy,
                    secondaryText = secondaryText,
                    borderColor = borderColor,
                    onCopyLocationClick = if (onLocationClick != {}) onLocationClick else onCopyLocationClick,
                    onCopyHashClick = onCopyHashClick
                )
            }


            /*
             * VIEW EVIDENCE
             */

            item {

                RecordActionButton(
                    icon = Icons.Default.Image,

                    text = "View Evidence",

                    backgroundColor = buttonBackground,

                    navy = navy,

                    onClick = onViewEvidenceClick
                )
            }


            /*
             * SHARE REPORT
             */

            item {

                RecordActionButton(
                    icon = Icons.Default.Share,

                    text = "Share Report",

                    backgroundColor = buttonBackground,

                    navy = navy,

                    onClick = onShareReportClick
                )
            }


            /*
             * EXPORT PDF
             */

            item {

                RecordActionButton(
                    icon = Icons.Default.FileDownload,

                    text = "Export PDF",

                    backgroundColor = buttonBackground,

                    navy = navy,

                    onClick = onExportPdfClick
                )
            }
        }
    }
}


/**
 * RECORD INFORMATION CARD
 */
@Composable
private fun RecordInformationCard(
    data: RecordDetailsData,

    navy: Color,

    secondaryText: Color,

    borderColor: Color,

    onCopyLocationClick: () -> Unit,

    onCopyHashClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
    ) {

        /*
         * RECORD ID
         */

        RecordDetailRow(
            label = "Record ID",

            value = data.recordId,

            navy = navy,

            secondaryText = secondaryText,

            borderColor = borderColor
        )


        /*
         * TEST
         */

        RecordDetailRow(
            label = "Test",

            value = data.testName,

            navy = navy,

            secondaryText = secondaryText,

            borderColor = borderColor
        )


        /*
         * RESULT
         */

        RecordDetailRow(
            label = "Result",

            value = data.result,

            navy = navy,

            secondaryText = secondaryText,

            borderColor = borderColor,

            valueColor = getResultColor(data.result)
        )


        /*
         * CONFIDENCE
         */

        RecordDetailRow(
            label = "Confidence",

            value = data.confidence,

            navy = navy,

            secondaryText = secondaryText,

            borderColor = borderColor
        )


        /*
         * DATE & TIME
         */

        RecordDetailRow(
            label = "Date & Time",

            value = data.dateTime,

            navy = navy,

            secondaryText = secondaryText,

            borderColor = borderColor
        )


        /*
         * OPERATOR ID
         */

        RecordDetailRow(
            label = "Operator ID",

            value = data.operatorId,

            navy = navy,

            secondaryText = secondaryText,

            borderColor = borderColor
        )


        /*
         * LOCATION
         */

        RecordDetailRow(
            label = "Location",

            value = data.location,

            navy = navy,

            secondaryText = secondaryText,

            borderColor = borderColor,

            trailingIcon = Icons.Default.ContentCopy,

            onTrailingIconClick = onCopyLocationClick
        )


        /*
         * IMAGE SHA-256
         */

        RecordDetailRow(
            label = "Image SHA-256",

            value = data.imageSha256,

            navy = navy,

            secondaryText = secondaryText,

            borderColor = borderColor,

            trailingIcon = Icons.Default.ContentCopy,

            onTrailingIconClick = onCopyHashClick
        )


        /*
         * PROTOCOL VERSION
         */

        RecordDetailRow(
            label = "Protocol version",

            value = data.protocolVersion,

            navy = navy,

            secondaryText = secondaryText,

            borderColor = borderColor,

            isLast = true
        )
    }
}


/**
 * SINGLE RECORD DETAIL ROW
 */
@Composable
private fun RecordDetailRow(
    label: String,

    value: String,

    navy: Color,

    secondaryText: Color,

    borderColor: Color,

    valueColor: Color = navy,

    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,

    onTrailingIconClick: () -> Unit = {},

    isLast: Boolean = false
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 11.dp
                ),

            verticalAlignment = Alignment.CenterVertically
        ) {

            /*
             * LABEL
             */

            Text(
                text = label,

                color = secondaryText,

                fontSize = 13.sp,

                fontWeight = FontWeight.Medium,

                modifier = Modifier.width(122.dp)
            )


            /*
             * VALUE
             */

            Text(
                text = value,

                color = valueColor,

                fontSize = 14.sp,

                fontWeight = FontWeight.Medium,

                maxLines = 2,

                overflow = TextOverflow.Ellipsis,

                modifier = Modifier.weight(1f)
            )


            /*
             * OPTIONAL COPY ICON
             */

            if (trailingIcon != null) {

                IconButton(
                    onClick = onTrailingIconClick,

                    modifier = Modifier.size(32.dp)
                ) {

                    Icon(
                        imageVector = trailingIcon,

                        contentDescription = "Copy $label",

                        tint = navy,

                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }


        /*
         * DIVIDER
         */

        if (!isLast) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(borderColor)
            )
        }
    }
}


/**
 * ACTION BUTTON
 */
@Composable
private fun RecordActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,

    text: String,

    backgroundColor: Color,

    navy: Color,

    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(
                onClick = onClick
            )
            .padding(horizontal = 18.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier.size(36.dp),

            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,

                contentDescription = text,

                tint = navy,

                modifier = Modifier.size(23.dp)
            )
        }


        Spacer(
            modifier = Modifier.width(16.dp)
        )


        Text(
            text = text,

            color = navy,

            fontSize = 15.sp,

            fontWeight = FontWeight.SemiBold
        )
    }
}


/**
 * RESULT COLOR
 *
 * The actual result still comes from data.
 * We only change its visual appearance depending
 * on what result was returned.
 */
private fun getResultColor(
    result: String
): Color {

    return when {

        result.contains(
            "positive",
            ignoreCase = true
        ) -> {
            Color(0xFFE52D36)
        }

        result.contains(
            "negative",
            ignoreCase = true
        ) -> {
            Color(0xFF15965A)
        }

        else -> {
            Color(0xFFC78300)
        }
    }
}