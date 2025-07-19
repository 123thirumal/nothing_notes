package com.example.mynotes.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.runtime.getValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mynotes.R
import com.example.mynotes.model.NoteModel
import com.example.mynotes.ui.theme.NRegular
import com.example.mynotes.data.viewmodel.NoteViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.CloudOff


@Composable
fun NoteWidget(noteViewModel: NoteViewModel,forPrivateFiles: Boolean=false,navController: NavController,isFolder:Boolean =false,folderId:String = "-1",
               isSelectedFiles: MutableState<Boolean>, selectedNoteList: SnapshotStateList<NoteModel>) {

    LaunchedEffect(isFolder, folderId) {
        if (isFolder&&folderId!="-1") noteViewModel.setFolderId(folderId)
    }

    val allNotes by
    if(forPrivateFiles) noteViewModel.privateNotes.collectAsState(initial = emptyList())
    else if(isFolder&&folderId!="-1") noteViewModel.folderNotes.collectAsState(initial = emptyList())
    else noteViewModel.nonPrivateNotes.collectAsState(initial = emptyList())


    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier =  Modifier.fillMaxWidth().height(((allNotes.size / 2 + allNotes.size % 2) * 230).dp).padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 0.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false
    ) {
        items(count = allNotes.size) {
            NoteItem(card = allNotes[it],navController, isSelectedFiles = isSelectedFiles, selectedNoteList = selectedNoteList)
        }
    }
}


@Composable
fun NoteItem(card: NoteModel, navController: NavController, isSelectedFiles: MutableState<Boolean>,
             selectedNoteList: SnapshotStateList<NoteModel>) {

    var modifier = if(isSelectedFiles.value&&card in selectedNoteList) {
        Modifier
            .fillMaxWidth()
            .aspectRatio(1f).border(width = 5.dp, color = Color(0xFFB9B9B9), shape = RoundedCornerShape(15.dp))
            .padding(5.dp).clickable(
                onClick = {
                    selectedNoteList.remove(card)
                }
            )
    } else{
        Modifier
            .fillMaxWidth()
            .aspectRatio(1f).padding(5.dp).pointerInput(Unit) {
                detectTapGestures(
                    onTap={
                        if(!isSelectedFiles.value){
                            if(card.folderId==null){ //open a note in all
                                navController.navigate("card_page/${card.id}/-1/false")
                            }
                            else{ //open a note in folder or private files
                                navController.navigate("card_page/${card.id}/${card.folderId}/false")
                            }
                        }
                        else{
                            selectedNoteList.add(card)
                        }
                    },
                    onLongPress = {
                        if(!isSelectedFiles.value){
                            isSelectedFiles.value=true
                            selectedNoteList.add(card)
                        }
                    }
                )
            }
    }
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xDA19181E))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart) // Keep text at top
            ) {
                Text(
                    text = if (card.title.isNullOrEmpty()) "Untitled" else card.title!!,
                    style = TextStyle(color = Color.White, fontFamily = NRegular, fontSize = 17.sp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "...",
                    style = TextStyle(color = Color.White, fontFamily = NRegular, fontSize = 15.sp)
                )
            }
            if(card.SavedInCloud){
                if(card.Synced){
                    Image(
                        painter = painterResource(id= R.drawable.cloud_done),
                        contentDescription = "synced",
                        colorFilter = ColorFilter.tint(Color.White),
                        modifier = Modifier.padding(10.dp)
                            .size(25.dp)
                            .align(Alignment.BottomEnd),
                        contentScale = ContentScale.Fit
                    )
                }
                else{
                    Icon(
                        imageVector = TablerIcons.CloudOff,
                        tint = Color.Red,
                        contentDescription = "not synced",
                        modifier = Modifier.padding(10.dp)
                            .size(25.dp)
                            .align(Alignment.BottomEnd),
                    )
                }
            }
        }
    }

}