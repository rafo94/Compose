package com.rafo.composeexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.rafo.composeexample.ui.theme.AppTheme
import com.rafo.composeexample.ui.view.MainScreenView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                MainScreenView()
            }
        }
    }
}

@Preview
@Composable
fun ComposablePreview() {
    MainScreenView()
}