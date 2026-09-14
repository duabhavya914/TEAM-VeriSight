package com.sih.drugtest.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sih.drugtest.model.TestProtocol
import com.sih.drugtest.ui.components.AppBottomNavigation
import com.sih.drugtest.ui.theme.CyanAccent
import com.sih.drugtest.ui.theme.Ivory
import com.sih.drugtest.ui.theme.NavyBackground
import com.sih.drugtest.ui.theme.SecondaryText


@Composable
fun SelectTestScreen(
    protocols: List<TestProtocol>,
    onTestSelected: (TestProtocol) -> Unit,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onTestsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onProfileClick: () -> Unit
) {

    var searchQuery by remember {
        mutableStateOf("")
    }


    val filteredProtocols =
        protocols.filter { protocol ->

            protocol.name.contains(
                searchQuery,
                ignoreCase = true
            ) ||

                    protocol.category.contains(
                        searchQuery,
                        ignoreCase = true
                    ) ||

                    protocol.version.contains(
                        searchQuery,
                        ignoreCase = true
                    )
        }


    Scaffold(

        containerColor =
            NavyBackground,

        bottomBar = {

            AppBottomNavigation(

                selectedTab =
                    "tests",

                onHomeClick =
                    onHomeClick,

                onTestsClick =
                    onTestsClick,

                onHistoryClick =
                    onHistoryClick,

                onProfileClick =
                    onProfileClick
            )
        }

    ) { innerPadding ->


        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        NavyBackground
                    )
                    .padding(
                        innerPadding
                    )
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        horizontal = 20.dp
                    )
                    .padding(
                        top = 20.dp,
                        bottom = 20.dp
                    )
        ) {


            // =================================================
            // HEADER
            // =================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                Surface(
                    modifier =
                        Modifier
                            .size(42.dp)
                            .clickable(
                                onClick =
                                    onBackClick
                            ),
                    shape =
                        CircleShape,
                    color =
                        Color(0xFF0B3455),
                    border =
                        BorderStroke(
                            width = 1.dp,
                            color = CyanAccent
                        )
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Outlined.ArrowBack,
                            contentDescription =
                                "Back",
                            tint =
                                CyanAccent,
                            modifier =
                                Modifier.size(22.dp)
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.width(14.dp)
                )


                Column {

                    Text(
                        text =
                            "Select Test",
                        color =
                            Ivory,
                        fontSize =
                            22.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(26.dp)
            )


            Text(
                text =
                    "Choose the field-test protocol you are using.",
                color =
                    SecondaryText,
                fontSize =
                    17.sp,
                lineHeight =
                    26.sp
            )


            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )


            // =================================================
            // SEARCH
            // =================================================

            OutlinedTextField(

                value =
                    searchQuery,

                onValueChange = {

                    searchQuery =
                        it
                },

                modifier =
                    Modifier.fillMaxWidth(),

                placeholder = {

                    Text(
                        text =
                            "Search test protocols",
                        color =
                            SecondaryText,
                        fontSize =
                            14.sp
                    )
                },

                leadingIcon = {

                    Icon(
                        imageVector =
                            Icons.Outlined.Search,
                        contentDescription =
                            "Search",
                        tint =
                            Ivory,
                        modifier =
                            Modifier.size(23.dp)
                    )
                },

                singleLine =
                    true,

                shape =
                    RoundedCornerShape(18.dp),

                colors =
                    OutlinedTextFieldDefaults.colors(

                        focusedTextColor =
                            Ivory,

                        unfocusedTextColor =
                            Ivory,

                        cursorColor =
                            CyanAccent,

                        focusedBorderColor =
                            CyanAccent,

                        unfocusedBorderColor =
                            CyanAccent.copy(
                                alpha = 0.75f
                            ),

                        focusedContainerColor =
                            Color(0xFF0B3455),

                        unfocusedContainerColor =
                            Color(0xFF0B3455)
                    )
            )


            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )


            ProtocolInfoBanner()


            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )


            // =================================================
            // AVAILABLE PROTOCOLS
            // =================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                Surface(
                    modifier =
                        Modifier
                            .width(4.dp)
                            .height(25.dp),
                    shape =
                        RoundedCornerShape(4.dp),
                    color =
                        CyanAccent
                ) {}


                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )


                Text(
                    text =
                        "Available Protocols",
                    color =
                        Ivory,
                    fontSize =
                        22.sp,
                    fontWeight =
                        FontWeight.Bold,
                    modifier =
                        Modifier.weight(1f)
                )


                if (protocols.isNotEmpty()) {

                    Text(
                        text =
                            "${filteredProtocols.size} protocols",
                        color =
                            SecondaryText,
                        fontSize =
                            13.sp
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )


            // =================================================
            // PROTOCOL LIST
            // =================================================

            when {

                protocols.isEmpty() -> {

                    NoProtocolsAvailable()
                }


                filteredProtocols.isEmpty() -> {

                    NoProtocolsFound()
                }


                else -> {

                    filteredProtocols.forEach { protocol ->

                        ProtocolCard(

                            protocol =
                                protocol,

                            onClick = {

                                onTestSelected(
                                    protocol
                                )
                            }
                        )


                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )


            // =================================================
            // SECURITY STRIP
            // =================================================

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 14.dp,
                            vertical = 12.dp
                        ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                Icon(
                    imageVector =
                        Icons.Outlined.VerifiedUser,
                    contentDescription =
                        "Version controlled protocols",
                    tint =
                        Ivory,
                    modifier =
                        Modifier.size(30.dp)
                )


                Spacer(
                    modifier =
                        Modifier.width(14.dp)
                )


                Text(
                    text =
                        "Protocols are version-controlled for consistent results.",
                    color =
                        SecondaryText,
                    fontSize =
                        13.sp,
                    lineHeight =
                        19.sp
                )
            }
        }
    }
}


@Composable
fun ProtocolCard(
    protocol: TestProtocol,
    onClick: () -> Unit
) {

    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick =
                        onClick
                ),
        shape =
            RoundedCornerShape(20.dp),
        color =
            Ivory
    ) {

        Row(
            modifier =
                Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 16.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {


            Surface(
                modifier =
                    Modifier.size(52.dp),
                shape =
                    RoundedCornerShape(15.dp),
                color =
                    CyanAccent.copy(
                        alpha = 0.15f
                    )
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Outlined.Science,
                        contentDescription =
                            protocol.name,
                        tint =
                            NavyBackground,
                        modifier =
                            Modifier.size(28.dp)
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.width(14.dp)
            )


            Column(
                modifier =
                    Modifier.weight(1f)
            ) {


                Text(
                    text =
                        protocol.name,
                    color =
                        NavyBackground,
                    fontSize =
                        16.sp,
                    fontWeight =
                        FontWeight.Bold
                )


                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )


                Text(
                    text =
                        protocol.category,
                    color =
                        Color(0xFF607080),
                    fontSize =
                        12.sp
                )


                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )


                Text(
                    text =
                        "Protocol ${protocol.version}",
                    color =
                        NavyBackground,
                    fontSize =
                        10.sp,
                    fontWeight =
                        FontWeight.Medium
                )
            }


            Icon(
                imageVector =
                    Icons.Outlined.ChevronRight,
                contentDescription =
                    "Open protocol",
                tint =
                    NavyBackground,
                modifier =
                    Modifier.size(26.dp)
            )
        }
    }
}


@Composable
fun NoProtocolsAvailable() {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(20.dp),
        color =
            Color(0xFFF8F3E8),
        border =
            BorderStroke(
                width = 1.dp,
                color = Color(0xFFE9DFC9)
            )
    ) {

        Row(
            modifier =
                Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 18.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {


            Surface(
                modifier =
                    Modifier.size(64.dp),
                shape =
                    RoundedCornerShape(16.dp),
                color =
                    Color(0xFFE3F1EC)
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Outlined.Science,
                        contentDescription =
                            null,
                        tint =
                            NavyBackground,
                        modifier =
                            Modifier.size(30.dp)
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.width(16.dp)
            )


            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        "No protocols available",
                    color =
                        NavyBackground,
                    fontSize =
                        18.sp,
                    fontWeight =
                        FontWeight.Bold
                )


                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )


                Text(
                    text =
                        "Protocols loaded from the database will appear here.",
                    color =
                        Color(0xFF607080),
                    fontSize =
                        14.sp,
                    lineHeight =
                        19.sp
                )
            }
        }
    }
}


@Composable
fun NoProtocolsFound() {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(18.dp),
        color =
            Color(0xFF0B3455),
        border =
            BorderStroke(
                width = 1.dp,
                color =
                    CyanAccent.copy(
                        alpha = 0.30f
                    )
            )
    ) {

        Row(
            modifier =
                Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 15.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {


            Icon(
                imageVector =
                    Icons.Outlined.Search,
                contentDescription =
                    null,
                tint =
                    CyanAccent,
                modifier =
                    Modifier.size(22.dp)
            )


            Spacer(
                modifier =
                    Modifier.width(12.dp)
            )


            Column {

                Text(
                    text =
                        "No matching protocols",
                    color =
                        Ivory,
                    fontSize =
                        14.sp,
                    fontWeight =
                        FontWeight.Bold
                )


                Text(
                    text =
                        "Try another protocol name or category.",
                    color =
                        SecondaryText,
                    fontSize =
                        11.sp
                )
            }
        }
    }
}


@Composable
fun ProtocolInfoBanner() {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(18.dp),
        color =
            Color(0xFFF0E3CB)
    ) {

        Row(
            modifier =
                Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 15.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {


            Surface(
                modifier =
                    Modifier.size(36.dp),
                shape =
                    CircleShape,
                color =
                    NavyBackground
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Outlined.Info,
                        contentDescription =
                            "Information",
                        tint =
                            Ivory,
                        modifier =
                            Modifier.size(18.dp)
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.width(14.dp)
            )


            Text(
                text =
                    "Select the protocol that matches your physical test kit.",
                color =
                    NavyBackground,
                fontSize =
                    13.sp,
                lineHeight =
                    19.sp,
                fontWeight =
                    FontWeight.Medium
            )
        }
    }
}