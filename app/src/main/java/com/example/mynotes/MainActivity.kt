package com.example.mynotes

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import com.example.mynotes.ui.theme.MyNotesTheme

class MainActivity : FragmentActivity() {
    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            MyNotesTheme{
                Navigation()
            }
        }
    }
}

