package com.example.mynotes.widgets

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mynotes.R
import com.example.mynotes.model.FolderModel
import com.example.mynotes.ui.theme.NRegular
import com.example.mynotes.data.viewmodel.FolderViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.input.pointer.pointerInput


@Composable
fun FolderWidget(onFolderSelected: ((String) -> Unit)? = null,isNotePageDialog : Boolean=false,folderViewModel: FolderViewModel,navController: NavController,
                 isSelectedFolders: MutableState<Boolean> = mutableStateOf(false), selectedFolderList: SnapshotStateList<FolderModel> = mutableStateListOf(),
                 onFolderSelectedWithSelectedFolders: ((String)->Unit)? = null,
                 isSelectedFiles: MutableState<Boolean> = mutableStateOf(false)) {
    val allFolders by folderViewModel.allFolders.collectAsState(initial = emptyList())
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier =  Modifier.fillMaxWidth().height(((allFolders.size / 2 + allFolders.size % 2) * if(isNotePageDialog)200 else 260).dp).padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 0.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false
    ) {
        items(count = allFolders.size) {
            FolderItem(onFolderSelected=onFolderSelected,isNotePageDialog = isNotePageDialog, card = allFolders[it],navController=navController,
                selectedFolderList = selectedFolderList, isSelectedFolders = isSelectedFolders,
                onFolderSelectedWithSelectedFolders = onFolderSelectedWithSelectedFolders, isSelectedFiles = isSelectedFiles)
        }
    }
}


@Composable
fun FolderItem(onFolderSelected: ((String) -> Unit)?=null,isNotePageDialog : Boolean, card: FolderModel, navController: NavController,
               isSelectedFolders: MutableState<Boolean>, selectedFolderList: SnapshotStateList<FolderModel>,
               onFolderSelectedWithSelectedFolders: ((String)->Unit)? = null, isSelectedFiles: MutableState<Boolean>) {

    var modifier = if(isSelectedFolders.value&&card in selectedFolderList) {
        Modifier
            .fillMaxWidth()
            .aspectRatio(if(isNotePageDialog)1f else 0.9f).border(width = 5.dp, color = Color(0xFFB9B9B9), shape = RoundedCornerShape(15.dp))
            .padding(5.dp).clickable(
                onClick = {
                    selectedFolderList.remove(card)
                }
            )
    } else{
        Modifier
            .fillMaxWidth()
            .aspectRatio(if(isNotePageDialog)1f else 0.9f).padding(5.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap={
                        if(!isSelectedFolders.value){
                            if(isSelectedFiles.value&&isNotePageDialog){ //for adding selected files to a folder in homepage
                                onFolderSelectedWithSelectedFolders?.invoke(card.id)
                            }
                            else if(isNotePageDialog){ // for adding a note to a folder in note page
                                onFolderSelected?.invoke(card.id)
                            }
                            else{ //navigating to a folder page from folder widget
                                navController.navigate("folder_page/${card.id}")
                            }
                        }
                        else{ //for selecting a folder in homepage
                            selectedFolderList.add(card)
                        }
                    },
                    onLongPress = {
                        if(!isNotePageDialog&&!isSelectedFolders.value){
                            isSelectedFolders.value=true
                            selectedFolderList.add(card)
                        }
                    }
                )
            }
    }
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xDA19181E)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxHeight().fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            Image(
                painter = painterResource(id= R.drawable.folder_img),
                contentDescription = "Folder Image",
                modifier = Modifier.requiredSize(if(isNotePageDialog)60.dp else 100.dp),
                colorFilter = ColorFilter.tint(Color(0x81FFFFFF)),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(if(isNotePageDialog)5.dp else 20.dp))
            Text(textAlign = TextAlign.Center, text = if(card.title.isEmpty()) "Untitled" else card.title, style = TextStyle(color = Color.White,fontFamily = NRegular, fontSize = if(isNotePageDialog)15.sp else 17.sp),
                maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}