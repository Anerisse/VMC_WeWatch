package com.example.wmc_wewatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Здесь просто показываем текст, чтобы проверить что работает
            Text("Hello World!")

            // ПОТОМ заменим на:
            // MainScreen()
        }
    }


}