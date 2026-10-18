package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.roomvision.ui.MainScreen
import com.example.roomvision.viewmodel.RoomVisionViewModel
import com.example.ui.theme.RoomVisionTheme

class MainActivity : ComponentActivity() {

    private val viewModel: RoomVisionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoomVisionTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
