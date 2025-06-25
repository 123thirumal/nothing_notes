package com.example.mynotes.widgets.noteblockwidgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.Dots
import compose.icons.tablericons.DotsVertical
import compose.icons.tablericons.List
import compose.icons.tablericons.PlayerPause
import compose.icons.tablericons.PlayerPlay
import compose.icons.tablericons.Trash

@Composable
fun VoiceWidget() {
    val isVoiceDropDownExpanded = remember{mutableStateOf(false)}
    val isPlaying = remember{mutableStateOf(false) }
    var currentPosition: Float = 0.3f
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFF1C1C1C), shape = RoundedCornerShape(12.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Play/Pause Button
            IconButton(onClick = {isPlaying.value=!isPlaying.value}) {
                Icon(
                    imageVector = if (isPlaying.value) TablerIcons.PlayerPause else TablerIcons.PlayerPlay,
                    contentDescription = "Play/Pause",
                    tint = if (isPlaying.value) Color.Red else Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Slider (progress bar)
            Slider(
                value = currentPosition,
                onValueChange = {},
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color(0xFFCB070D),
                    inactiveTrackColor = Color(0xFF757575)
                )
            )

            // Settings Button
            Box {
                IconButton(onClick = {isVoiceDropDownExpanded.value=true}) {
                    Icon(
                        imageVector = TablerIcons.DotsVertical,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                DropdownMenu(
                    expanded = isVoiceDropDownExpanded.value,
                    onDismissRequest = {isVoiceDropDownExpanded.value=false},
                    offset = DpOffset(x=(-20).dp,y= (15).dp),
                    modifier = Modifier
                        .background(Color(0xFA131313))
                ){
                    DropdownMenuItem(onClick = {isVoiceDropDownExpanded.value=false},
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically){
                                Icon(
                                    tint = Color.Red,
                                    imageVector = TablerIcons.Trash,
                                    contentDescription = "Delete",
                                )
                                Text(text = "Delete", modifier = Modifier.padding(start = 15.dp))
                            }
                        }
                    )
                }
            }
        }
    }
}