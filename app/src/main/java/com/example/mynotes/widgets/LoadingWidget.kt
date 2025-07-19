package com.example.mynotes.widgets

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.ui.theme.NRegular

@Composable
fun LoadingWidget(loadDone: MutableState<Boolean>, msg: String) {


    // Scale animation for Done icon
    val scale by animateFloatAsState(
        targetValue = if (!loadDone.value) 1f else 1.2f,
        animationSpec = tween(durationMillis = 500),
        label = "iconScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(targetState = loadDone, label = "SyncCrossFade") { loadOver ->
            if (!loadOver.value) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = Color(0xFFC4C4C4),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text( text = "${msg}...", style = TextStyle(fontFamily = NRegular, fontSize = 18.sp,
                        color = Color(0xFFC4C4C4),))
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Done",
                        tint = Color(0xFFC4C4C4),
                        modifier = Modifier
                            .size(48.dp)
                            .graphicsLayer(scaleX = scale, scaleY = scale)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text( text = "DONE", style = TextStyle(fontFamily = NRegular, fontSize = 18.sp,
                        color = Color(0xFFC4C4C4),))
                }
            }
        }
    }
}