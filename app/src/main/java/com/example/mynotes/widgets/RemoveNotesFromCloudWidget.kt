package com.example.mynotes.widgets

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.model.NoteModel
import com.example.mynotes.ui.theme.NRegular
import com.example.mynotes.data.viewmodel.NoteViewModel


@Composable
fun RemoveNotesFromCloudWidget(allNotesInCloud: SnapshotStateList<NoteModel>, privateNotesInCloud : SnapshotStateList<NoteModel> ,noteViewModel: NoteViewModel, viewSelect: MutableState<String>, selectedNoteList: SnapshotStateList<NoteModel>,
                               selectedPrivateNoteList: SnapshotStateList<NoteModel>, isSelectAll: MutableState<Boolean>, isSelectPrivateAll: MutableState<Boolean>) {



    LaunchedEffect(key1 = isSelectAll.value) {
        if (isSelectAll.value) {
            selectedNoteList.clear()
            selectedNoteList.addAll(allNotesInCloud)
        }
    }

    LaunchedEffect(key1 = isSelectPrivateAll.value) {
        if(isSelectPrivateAll.value){
            selectedPrivateNoteList.clear()
            selectedPrivateNoteList.addAll(privateNotesInCloud)
        }
    }


    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier =  if(viewSelect.value=="ALL") Modifier.fillMaxWidth().height(((allNotesInCloud.size / 2 + allNotesInCloud.size % 2) * 230).dp).padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 0.dp)
        else  Modifier.fillMaxWidth().height(((privateNotesInCloud.size / 2 + privateNotesInCloud.size % 2) * 230).dp).padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 0.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false
    ) {
        if(viewSelect.value=="ALL"){
            items(count = allNotesInCloud.size) {
                RemoveNoteItem(card = allNotesInCloud[it], selectedNoteList = selectedNoteList, isPrivate = false, selectedPrivateNoteList = selectedPrivateNoteList,
                    isSelectAll = isSelectAll, isSelectPrivateAll = isSelectPrivateAll)
            }
        }
        else if(viewSelect.value=="PRIVATE"){
            items(count = privateNotesInCloud.size) {
                RemoveNoteItem(card = privateNotesInCloud[it], selectedNoteList = selectedNoteList, isPrivate = true, selectedPrivateNoteList = selectedPrivateNoteList,
                    isSelectAll = isSelectAll, isSelectPrivateAll = isSelectPrivateAll)
            }
        }
    }
}


@Composable
fun RemoveNoteItem(
    card: NoteModel,
    isPrivate: Boolean,
    selectedNoteList: SnapshotStateList<NoteModel>,
    selectedPrivateNoteList: SnapshotStateList<NoteModel>,
    isSelectAll: MutableState<Boolean>,
    isSelectPrivateAll: MutableState<Boolean>
) {
    val isSelected = card in selectedNoteList || card in selectedPrivateNoteList

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(5.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.Transparent)
            .let {
                if (isSelected) {
                    it.clickable {
                        if(isPrivate){
                            isSelectPrivateAll.value=false
                            selectedPrivateNoteList.remove(card)
                        }
                        else{
                            isSelectAll.value=false
                            selectedNoteList.remove(card)
                        }
                    }
                } else {
                    it.pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                if(isPrivate){
                                    selectedPrivateNoteList.add(card)
                                }
                                else{
                                    selectedNoteList.add(card)
                                }
                            }
                        )
                    }
                }
            }
    ) {
        // Main card UI
        Card(
            modifier = Modifier.fillMaxSize(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xDA19181E))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = if (card.title.isNullOrEmpty()) "Untitled" else card.title!!,
                    style = TextStyle(
                        color = Color.White,
                        fontFamily = NRegular,
                        fontSize = 17.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "...",
                    style = TextStyle(
                        color = Color.White,
                        fontFamily = NRegular,
                        fontSize = 15.sp
                    ),
                )
            }
        }

        // Overlay and icon if selected
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000)) // Semi-transparent dark overlay
            )

            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp)
                    .size(30.dp)
            )
        }
    }
}