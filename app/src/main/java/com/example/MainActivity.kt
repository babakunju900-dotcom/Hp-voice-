package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.HelloTalkViewModel
import com.example.ui.navigation.HelloTalkMainContainer
import com.example.ui.theme.HelloTalkTheme

class MainActivity : ComponentActivity() {
    private val viewModel: HelloTalkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HelloTalkTheme {
                HelloTalkMainContainer(viewModel = viewModel)
            }
        }
    }
}
