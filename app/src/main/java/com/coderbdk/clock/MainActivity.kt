package com.coderbdk.clock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.coderbdk.clock.ui.theme.ClockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClockTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Clock(
                        state = rememberClockState(),
                        modifier = Modifier.padding(24.dp)

                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClockPreview() {
    ClockTheme {
        Box(
            Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Clock(
                state = rememberClockState(),
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}