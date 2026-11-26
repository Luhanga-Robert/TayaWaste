package com.example.tayawaste

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tayawaste.firebase.FirebaseManager
import com.example.tayawaste.navigation.NavGraph
import com.example.tayawaste.ui.theme.TayaWasteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initialize real-time synchronization with Firebase Firestore
        FirebaseManager.startRealtimeSync()

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        setContent {
            TayaWasteTheme {
                NavGraph()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        FirebaseManager.stopRealtimeSync()
    }
}
