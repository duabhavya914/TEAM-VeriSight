package com.sih.drugtest.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sih.drugtest.ui.components.AppBottomNavigation


/**
 * Data required by the Save Test Record screen.
 *
 * The screen receives the test information through this data class
 * instead of hardcoding a particular test.
 */
data class SaveTestRecordData(
    val testName: String,
    val result: String,
    val dateTime: String,
    val operatorId: String,
    val location: String,
    val imageUri: String? = null,
    val analysisSummary: String? = null
)


@Composable
fun SaveTestRecordScreen(
    data: SaveTestRecordData,
    onBackClick: () -> Unit,
    onLocationClick: () -> Unit,
    onSaveClick: (
        notes: String,
        storeImage: Boolean,
        storeAnalysisData: Boolean
    ) -> Unit,
    selectedTab: String = "history",
    onHomeClick: () -> Unit = {},
    onTestsClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {

    var notes by remember {
        mutableStateOf("")
    }

    var storeImage by remember {
        mutableStateOf(true)
    }

    var storeAnalysisData by remember {
        mutableStateOf(true)
    }

    val backgroundColor = Color(0xFFF8F7FC)
    val navy = Color(0xFF102B52)
    val secondaryText = Color(0xFF65738A)
    val cyan = Color(0xFF11D5E5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),

            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 16.dp,
                bottom = 24.dp
            ),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            /*
             * HEADER
             */

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                        text = "Save Test Record",
                        color = navy,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Save the test details to your secure history.",
                    color = secondaryText,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }


            /*
             * TEST SUMMARY CARD
             */

            item {

                TestSummaryCard(
                    data = data,
                    navy = navy
                )
            }


            /*
             * DETAILS CARD
             */

            item {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.White)
                        .padding(
                            horizontal = 20.dp,
                            vertical = 8.dp
                        )
                ) {

                    DetailRow(
                        icon = Icons.Default.Person,
                        title = "Operator ID",
                        value = data.operatorId,
                        navy = navy,
                        secondaryText = secondaryText
                    )

                    HorizontalDivider(
                        color = Color(0xFFE7E8ED)
                    )

                    DetailRow(
                        icon = Icons.Default.LocationOn,
                        title = "Location",
                        value = data.location,
                        navy = navy,
                        secondaryText = secondaryText,

                        trailingContent = {

                            IconButton(
                                onClick = onLocationClick,
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFF0F4FB))
                            ) {

                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Update location",
                                    tint = navy
                                )
                            }
                        }
                    )

                    HorizontalDivider(
                        color = Color(0xFFE7E8ED)
                    )


                    /*
                     * NOTES
                     */

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp),

                        verticalAlignment = Alignment.Top
                    ) {

                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = navy,
                            modifier = Modifier
                                .size(30.dp)
                                .padding(top = 4.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(20.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "Notes (optional)",
                                color = secondaryText,
                                fontSize = 16.sp
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            OutlinedTextField(
                                value = notes,

                                onValueChange = {
                                    notes = it
                                },

                                modifier = Modifier.fillMaxWidth(),

                                placeholder = {
                                    Text(
                                        text = "Add any additional notes...",
                                        color = secondaryText
                                    )
                                },

                                shape = RoundedCornerShape(14.dp),

                                maxLines = 4
                            )
                        }
                    }
                }
            }


            /*
             * STORAGE OPTIONS
             */

            item {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.White)
                        .padding(
                            horizontal = 20.dp,
                            vertical = 8.dp
                        )
                ) {

                    StorageOptionRow(
                        icon = Icons.Default.Image,
                        title = "Store image",
                        subtitle = "Save the captured test image",
                        checked = storeImage,

                        onCheckedChange = {
                            storeImage = it
                        },

                        navy = navy,
                        secondaryText = secondaryText
                    )

                    HorizontalDivider(
                        color = Color(0xFFE7E8ED)
                    )

                    StorageOptionRow(
                        icon = Icons.Default.Storage,
                        title = "Store analysis data",
                        subtitle = "Save colour values and results",
                        checked = storeAnalysisData,

                        onCheckedChange = {
                            storeAnalysisData = it
                        },

                        navy = navy,
                        secondaryText = secondaryText
                    )
                }
            }


            /*
             * SAVE BUTTON
             */

            item {

                Button(
                    onClick = {

                        onSaveClick(
                            notes,
                            storeImage,
                            storeAnalysisData
                        )
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),

                    shape = RoundedCornerShape(18.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = cyan,
                        contentColor = navy
                    )
                ) {

                    Text(
                        text = "Save to History",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(navy),

                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }


            /*
             * SECURITY MESSAGE
             */

            item {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),

                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = navy,
                        modifier = Modifier.size(38.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(16.dp)
                    )

                    Text(
                        text = "Records are securely stored and\nversion-controlled for audit integrity.",
                        color = secondaryText,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }


        /*
         * EXISTING APP BOTTOM NAVIGATION
         */

        AppBottomNavigation(
            selectedTab = selectedTab,
            onHomeClick = onHomeClick,
            onTestsClick = onTestsClick,
            onHistoryClick = onHistoryClick,
            onSettingsClick = onSettingsClick
        )
    }
}


/*
 * TEST SUMMARY
 */

@Composable
private fun TestSummaryCard(
    data: SaveTestRecordData,
    navy: Color
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .padding(16.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        if (data.imageUri != null) {

            AsyncImage(
                model = data.imageUri,
                contentDescription = "Test image",

                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(14.dp)),

                contentScale = ContentScale.Crop
            )

        } else {

            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE9ECF1)),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = navy,
                    modifier = Modifier.size(35.dp)
                )
            }
        }

        Spacer(
            modifier = Modifier.width(18.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = data.testName,
                color = navy,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = data.dateTime,
                color = Color(0xFF65738A),
                fontSize = 15.sp
            )
        }

        ResultBadge(
            result = data.result
        )
    }
}


/*
 * DETAIL ROW
 */

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    navy: Color,
    secondaryText: Color,
    trailingContent: (@Composable () -> Unit)? = null
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0F3FA)),

            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = navy,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(
            modifier = Modifier.width(18.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                color = secondaryText,
                fontSize = 16.sp
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = value,
                color = navy,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            )
        }

        trailingContent?.invoke()
    }
}


/*
 * STORAGE OPTION
 */

@Composable
private fun StorageOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    navy: Color,
    secondaryText: Color
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFF0F3FA)),

            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = navy,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(
            modifier = Modifier.width(18.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                color = navy,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = subtitle,
                color = secondaryText,
                fontSize = 13.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}


/*
 * RESULT BADGE
 */

@Composable
private fun ResultBadge(
    result: String
) {

    val normalizedResult = result.lowercase()

    val background: Color
    val textColor: Color

    when {

        normalizedResult.contains("positive") -> {

            background = Color(0xFFFFE5E7)
            textColor = Color(0xFFE52D36)
        }

        normalizedResult.contains("negative") -> {

            background = Color(0xFFE1F6EC)
            textColor = Color(0xFF15965A)
        }

        else -> {

            background = Color(0xFFFFF0D2)
            textColor = Color(0xFFC78300)
        }
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .padding(
                horizontal = 14.dp,
                vertical = 9.dp
            )
    ) {

        Text(
            text = result,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}