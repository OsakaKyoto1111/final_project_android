package com.sdu.threads

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import dagger.hilt.android.AndroidEntryPoint
import com.sdu.threads.presentation.navigation.ThreadsNavHost
import com.sdu.threads.presentation.theme.SduThreadsTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val darkTheme = isSystemInDarkTheme()
            SduThreadsTheme(useDarkTheme = darkTheme) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    ThreadsNavHost()
                }
            }
        }
    }
}
