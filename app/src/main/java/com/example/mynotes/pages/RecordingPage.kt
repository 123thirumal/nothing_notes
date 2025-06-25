package com.example.mynotes.pages

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.mynotes.ui.theme.NDot
import com.example.mynotes.ui.theme.NDot55
import com.example.mynotes.ui.theme.NRegular
import com.example.mynotes.ui.theme.NType
import com.example.mynotes.utils.AudioRecorderManager
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft
import compose.icons.tablericons.PlayerPause
import compose.icons.tablericons.PlayerPlay
import compose.icons.tablericons.Refresh
import kotlinx.coroutines.delay

@Composable
fun RecordingPage(navController: NavController){
    val context = LocalContext.current
    val audioRecorderManager = remember { AudioRecorderManager() }
    val amplitude = audioRecorderManager.amplitudeFlow.collectAsState()
    val isStart = remember{mutableStateOf(false)}
    val isPlay = remember{mutableStateOf(false)}
    val showDialog = remember { mutableStateOf(false) }
    val isBack = remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        showDialog.value = true
        isBack.value=true
    }


    //For waveform
    val waveformBars = remember { mutableStateListOf<Float>() }
    val tempBarList = remember { mutableStateListOf<Float>() }
    // Update waveform list with the new amplitude
    LaunchedEffect(amplitude.value) {
        waveformBars.add(amplitude.value)
        tempBarList.add(amplitude.value)
        if(tempBarList.size>35){
            tempBarList.removeAt(0)
        }
    }


    //for Timer
    val timerSec = remember { mutableLongStateOf(0) }

    LaunchedEffect(isPlay.value) {
        if (isPlay.value) {
            while (isPlay.value) {
                // Update timer text
                ++timerSec.longValue
                delay(1000L) // wait 1 second
            }
        }
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                // Only start recording if permission granted
                isStart.value = true
                isPlay.value = true
            } else {
                Toast.makeText(context, "Microphone permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(isStart.value, isPlay.value) {
        if (isStart.value && isPlay.value) {
            //check for permission
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ){
                audioRecorderManager.startRecording()
            }
            else{
                // Request permission
                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        } else {
            audioRecorderManager.stopRecording()
        }
    }

    if (showDialog.value) {
        Dialog(onDismissRequest = {
            isBack.value=false
            showDialog.value = false
        }) {
            Box(
                modifier = Modifier
                    .width(600.dp) // You can control width here!
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFA131313))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "DISCARD RECORDING", style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 18.sp,
                        color = Color(0xFFDEDEDE)
                    ))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(textAlign = TextAlign.Center,
                        text = "This recording isn't saved", style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 14.sp,
                        color = Color(0xFFB6B6B6)
                    ))
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        modifier = Modifier.padding(10.dp).fillMaxWidth()
                            .clip(RoundedCornerShape(50.dp)),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFCB070D)
                        ),
                        onClick = {
                            showDialog.value = false
                            isStart.value=false
                            isPlay.value=false
                            timerSec.longValue=0
                            waveformBars.clear()
                            tempBarList.clear()
                            if(isBack.value){
                                navController.popBackStack()
                            }
                        }) {
                        Text(modifier = Modifier.padding(vertical = 5.dp), text = "DISCARD", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(onClick = {
                        isBack.value=false
                        showDialog.value = false },
                        border = BorderStroke(0.dp, Color.Transparent),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent
                        )) {
                        Text(text = "KEEP RECORDING", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color(0xFFB6B6B6),
                        ))
                    }
                }
            }
        }
    }



    Scaffold(
        containerColor = Color.Black,
        contentWindowInsets = WindowInsets.safeContent,
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp, horizontal = 5.dp)
                    .windowInsetsPadding(WindowInsets.statusBars),
                backgroundColor = Color.Black,
                navigationIcon = {
                    IconButton(onClick = {
                        isBack.value=true
                        showDialog.value=true
                    }) {
                        Icon(
                            imageVector = TablerIcons.ArrowLeft,
                            contentDescription = "back",
                            tint = Color.White,
                            modifier = Modifier.size(23.dp)
                        )
                    }
                },
                title = {
                    Text(
                        text = "VoiceNote",
                        style = TextStyle(fontFamily = NDot, fontSize = 25.sp, color = Color.White, fontWeight = FontWeight.Normal)

                    )
                },
            )
        }
    ){ innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding() - 5.dp,
                    start = 5.dp,
                    end = 5.dp,
                    bottom = 5.dp
                )
                .clip(RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                // Timer
                Text(
                    text = buildAnnotatedString {
                        // Hours part
                        withStyle(style = SpanStyle(fontFamily = NDot)) {
                            append((timerSec.longValue / 3600).toString().padStart(2, '0'))
                        }
                        withStyle(style = SpanStyle(fontFamily = NDot55)) {
                            append(" : ")
                        }
                        // Minutes part
                        withStyle(style = SpanStyle(fontFamily = NDot)) {
                            append(((timerSec.longValue % 3600) / 60).toString().padStart(2, '0'))
                        }
                        withStyle(style = SpanStyle(fontFamily = NDot55)) {
                            append(" : ")
                        }
                        // Seconds part
                        withStyle(style = SpanStyle(fontFamily = NDot)) {
                            append((timerSec.longValue % 60).toString().padStart(2, '0'))
                        }
                    },
                    color = Color(0xFFD7D7D7),
                    fontSize = 55.sp
                )

                // Simulated waveform
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(tint = Color.Red, imageVector = Icons.Default.ArrowDropDown, contentDescription = "Pointer",
                            modifier = Modifier.size(40.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Past bars (left of pointer)
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                tempBarList.forEach { heightFactor ->
                                    Box(
                                        modifier = Modifier
                                            .width(5.dp)
                                            .padding(horizontal = 2.dp)
                                            .fillMaxHeight(heightFactor.coerceIn(0.1f, 0.6f))
                                            .background(
                                                if (isPlay.value) Color.Red else Color.White,
                                                RoundedCornerShape(2.dp)
                                            )
                                    )
                                }
                            }

                            // Center pointer
                            Box(
                                modifier = Modifier
                                    .width(2.1.dp)
                                    .fillMaxHeight(0.8f)
                                    .background(Color.Red) // or any highlight color
                            )

                            // Future bars (right of pointer - new ones)
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                // Only show new bars that arrived after centerIndex if needed
                                // Here, we continue the center as a live point, so we don’t draw future
                            }
                        }
                    }
                }

                //Buttons
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly){

                    if(!isStart.value){
                        Button(onClick = {
                            isStart.value=true
                            isPlay.value=true
                            timerSec.longValue=0
                        },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFFCB070D)
                            )  ,
                            modifier = Modifier.clip(RoundedCornerShape(50.dp))
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                                text = "RECORD",
                                style = TextStyle(
                                    fontFamily = NRegular,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                )
                            )
                        }
                    } else if(isStart.value&&isPlay.value){
                        IconButton(
                            onClick = {
                                isPlay.value=false
                            },
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .background(Color(0xFFD7D7D7))) {
                            Icon(
                                imageVector = TablerIcons.PlayerPause,
                                contentDescription = "Pause",
                                tint = Color.Black,
                                modifier = Modifier.size(35.dp)
                            )
                        }
                    } else if(isStart.value&&!isPlay.value){
                        IconButton(
                            onClick = {
                                showDialog.value=true
                            },
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .background(Color(0xFFD7D7D7))){
                            Icon(
                                imageVector = TablerIcons.Refresh,
                                contentDescription = "Re-record",
                                tint = Color.Black,
                                modifier = Modifier.size(35.dp)
                            )
                        }
                        IconButton(onClick = {isPlay.value=true},
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .background(Color(0xFFD7D7D7))){
                            Icon(
                                imageVector = TablerIcons.PlayerPlay,
                                contentDescription = "Play",
                                tint = Color.Black,
                                modifier = Modifier.size(35.dp)
                            )
                        }
                        Button(onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFFCB070D)
                            )  ,
                            modifier = Modifier.clip(RoundedCornerShape(50.dp))
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                                text = "SAVE",
                                style = TextStyle(
                                    fontFamily = NRegular,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

