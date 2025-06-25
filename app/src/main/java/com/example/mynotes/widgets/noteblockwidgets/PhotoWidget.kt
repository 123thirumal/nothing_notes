package com.example.mynotes.widgets.noteblockwidgets

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import coil3.gif.GifDecoder
import coil3.request.ImageRequest
import com.example.mynotes.R
import com.example.mynotes.data.viewmodel.ImageDemoViewModel
import com.example.mynotes.model.ImageDemoModel
import com.example.mynotes.model.NoteBlockEntityModel
import com.example.mynotes.ui.theme.NRegular
import compose.icons.TablerIcons
import compose.icons.tablericons.Trash
import compose.icons.tablericons.ZoomCheck
import kotlinx.coroutines.launch

@Composable
fun PhotoWidget(imageBlock: NoteBlockEntityModel.ImageBlock, noteBlockList: MutableList<NoteBlockEntityModel>,
                isChangeMade: MutableState<Boolean>,imageDemoViewModel: ImageDemoViewModel) {

    val density = LocalDensity.current
    val tempWidth = remember { mutableStateOf(imageBlock.imgWidth.value) }
    val tempHeight = remember { mutableStateOf(imageBlock.imgHeight.value) }

    val coroutineScope = rememberCoroutineScope()


    //demo for image resize
    val showDemoForImgResize = remember{mutableStateOf(false)}
    val demoGifList = listOf(
        R.drawable.demo1,
        R.drawable.demo2
    )
    val demoMsg = listOf(
        "Drag to resize image",
        "Double tap to set image size"
    )
    val msgPtr = remember{mutableStateOf(0)}

    val doNot = remember{mutableStateOf(false)}

    if (showDemoForImgResize.value) {
        Dialog(onDismissRequest = {
            msgPtr.value=0
            showDemoForImgResize.value = false
        }) {
            Box(
                modifier = Modifier
                    .width(600.dp) // You can control width here!
                    .height(600.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFA131313))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround) {
                    Text(text = "IMAGE RESIZE", style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 18.sp,
                        color = Color(0xFFDEDEDE)
                    ))
                    Spacer(modifier = Modifier.height(16.dp))

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(demoGifList[msgPtr.value])
                            .decoderFactory(GifDecoder.Factory())
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp) // Adjust as needed
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(textAlign = TextAlign.Center,
                        text = demoMsg[msgPtr.value], style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color(0xFFB6B6B6)
                        ))
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = {
                            doNot.value=!doNot.value
                        },
                        border = BorderStroke(0.dp, Color.Transparent),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if(doNot.value) Color(0xFFB6B6B6) else Color.Transparent
                        )) {
                        Text(modifier = Modifier.padding(5.dp), text = "DO NOT SHOW AGAIN", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = if(doNot.value) Color.Black else Color(0xFFB6B6B6),
                        ))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        TextButton(
                            onClick = {
                                if (msgPtr.value > 0) msgPtr.value -= 1
                            },
                            border = BorderStroke(0.dp, Color.Transparent),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF27272C)
                            )) {
                            Text(modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),text = "PREV", style = TextStyle(
                                fontFamily = NRegular,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB6B6B6),
                            ))
                        }
                        TextButton(
                            onClick = {
                                if (msgPtr.value ==0 ) msgPtr.value += 1
                                else if( msgPtr.value==1) {
                                    msgPtr.value=0
                                    showDemoForImgResize.value=false

                                    if(doNot.value){
                                        coroutineScope.launch {
                                            imageDemoViewModel.insertOrUpdateImageDemo(ImageDemoModel(showImageDemo = false))
                                        }
                                    }
                                }
                            },
                            border = BorderStroke(0.dp, Color.Transparent),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF27272C)
                            )) {
                            Text(modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),text = if(msgPtr.value==0)"NEXT" else "DONE", style = TextStyle(
                                fontFamily = NRegular,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB6B6B6),
                            ))
                        }
                    }
                }
            }
        }
    }


    if(imageBlock.uri.value!=null){
        val modifier= if(imageBlock.isImgResize.value){
            Modifier
                .padding(10.dp)
                .width(tempWidth.value)
                .height(tempHeight.value)
                .clip(RoundedCornerShape(15.dp))
                .border(width = 5.dp, color = Color(0xFFEEEEEE), shape = RoundedCornerShape(15.dp))
                .padding(15.dp)
                .clip(RoundedCornerShape(15.dp))
                .combinedClickable(
                    onDoubleClick = {
                        imageBlock.isImgResize.value = false
                        imageBlock.isImgDropdown.value = false
                    }
                ){}
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            imageBlock.imgWidth.value = tempWidth.value
                            imageBlock.imgHeight.value = tempHeight.value
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val dxDp = with(density) { dragAmount.x.toDp() }
                            val dyDp = with(density) { dragAmount.y.toDp() }

                            tempWidth.value = (tempWidth.value + dxDp).coerceAtLeast(150.dp)
                            tempHeight.value = (tempHeight.value + dyDp).coerceAtLeast(150.dp)
                        },
                    )
                }
        } else{
            Modifier
                .padding(10.dp)
                .width(imageBlock.imgWidth.value)
                .height(imageBlock.imgHeight.value)
                .clip(RoundedCornerShape(15.dp))
                .pointerInput(imageBlock.isImgDropdown.value) {
                    detectTapGestures(
                        onLongPress = {
                            imageBlock.isImgDropdown.value = true
                        }
                    )
                }
        }
        Box(modifier = modifier) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize(),
                model = imageBlock.uri.value,
                contentDescription = "Selected Image",
                contentScale = ContentScale.FillBounds,
            )
            DropdownMenu(
                expanded = imageBlock.isImgDropdown.value&&!imageBlock.isImgResize.value,
                onDismissRequest = {
                    imageBlock.isImgDropdown.value = false
                },
                offset = DpOffset(x=(+10).dp,y= (10).dp),
                modifier = Modifier
                    .background(Color(0xFA131313))
            ) {
                DropdownMenuItem(
                    onClick = {
                        imageDemoViewModel.imgDemo.value?.let { demo ->
                            Log.d("photo", demo.showImageDemo.toString())
                            if (demo.showImageDemo) {
                                showDemoForImgResize.value = true
                            }
                        }
                        isChangeMade.value=true
                        imageBlock.isImgResize.value=true
                        imageBlock.isImgDropdown.value=false
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically){
                            Icon(tint = Color.White, imageVector = TablerIcons.ZoomCheck, contentDescription = "Edit",)
                            Text(text = "Resize", modifier = Modifier.padding(start = 15.dp))
                        }
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                DropdownMenuItem(
                    onClick = {
                        isChangeMade.value=true
                        noteBlockList.remove(imageBlock)
                        imageBlock.isImgDropdown.value=false
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically){
                            Icon(tint = Color.Red, imageVector = TablerIcons.Trash, contentDescription = "Delete")
                            Text(text = "Delete Image", color = Color.Red, modifier = Modifier.padding(start = 15.dp))
                        }
                    }
                )
            }
        }
    }
}
