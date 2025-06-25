package com.example.mynotes.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mynotes.data.viewmodel.LockViewModel
import com.example.mynotes.data.viewmodel.NoteViewModel
import com.example.mynotes.model.FolderModel
import com.example.mynotes.model.NoteModel
import com.example.mynotes.ui.theme.NRegular
import compose.icons.TablerIcons
import compose.icons.tablericons.FileExport
import compose.icons.tablericons.FolderPlus
import compose.icons.tablericons.Lock
import compose.icons.tablericons.LockOpen
import compose.icons.tablericons.Trash


@Composable
fun PrivateWidget(noteViewModel: NoteViewModel,lockViewModel: LockViewModel,isUnlocked: MutableState<Boolean>,navController: NavController,
                  isSelectedFilesInPrivate: MutableState<Boolean>, selectedNoteListInPrivate: SnapshotStateList<NoteModel>) {

    val isLockSet=lockViewModel.isPasscodeSet.collectAsState()

    val dummyState = remember { mutableStateOf(0) }

    LaunchedEffect(isLockSet.value) {
        dummyState.value++ // triggers recomposition
    }



    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    if(!isUnlocked.value){
        Box(
            modifier = Modifier
                .height(screenHeight - 250.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Icon(
                    imageVector = TablerIcons.Lock,
                    contentDescription = "Lock",
                    tint = Color(0xFFDEDEDE),
                    modifier = Modifier.size(50.dp)
                )
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = if(isLockSet.value) "Private Files Are Locked" else "Secure your files with Private Files",
                    style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 18.sp,
                        color = Color(0xFFDEDEDE),
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(150.dp))
                Button(
                    modifier = Modifier
                        .padding(10.dp)
                        .clip(RoundedCornerShape(50.dp)),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFCB070D)
                    ),
                    onClick = {
                        if(isLockSet.value){
                            navController.navigate("private_lock_page")
                        }
                        else{
                            navController.navigate("private_lock_setup_page")
                        }
                    }) {
                    Text(
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                        text = if(isLockSet.value) "UNLOCK" else "SET PASSWORD",
                        style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 15.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    } else{ //private files unlocked
        NoteWidget(forPrivateFiles = true, navController = navController, noteViewModel = noteViewModel,
            isSelectedFiles = isSelectedFilesInPrivate, selectedNoteList = selectedNoteListInPrivate)
    }
}