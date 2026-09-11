package com.sih.drugtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sih.drugtest.ui.theme.DrugTestAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DrugTestAppTheme {
                DrugTestApp()
            }
        }
    }
}

@Composable
fun DrugTestApp() {
    var currentScreen by remember { mutableStateOf("home") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (currentScreen) {
            "home" -> HomeScreen(
                onStartClick = { currentScreen = "selectTest" }
            )

            "selectTest" -> SelectTestScreen(
                onTestSelected = { currentScreen = "capture" },
                onBackClick = { currentScreen = "home" }
            )

            "capture" -> CaptureScreen(
                onBackClick = { currentScreen = "selectTest" }
            )
        }
    }
}

@Composable
fun HomeScreen(onStartClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Field Drug Test",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Digital colour analysis and record system",
            modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)
        )

        Button(onClick = onStartClick) {
            Text("Start New Test")
        }
    }
}

@Composable
fun SelectTestScreen(
    onTestSelected: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Test",
            style = MaterialTheme.typography.headlineLarge
        )

        Button(
            onClick = onTestSelected,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Test Protocol 1")
        }

        Button(
            onClick = onBackClick,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Back")
        }
    }
}

@Composable
fun CaptureScreen(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Capture Test Image",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Camera will be added here.",
            modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)
        )

        Button(onClick = onBackClick) {
            Text("Back")
        }
    }
}