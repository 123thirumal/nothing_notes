package com.example.mynotes.pages

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import com.example.mynotes.data.viewmodel.FolderViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mynotes.data.viewmodel.NoteViewModel
import com.example.mynotes.model.FolderModel
import com.example.mynotes.model.NoteModel
import com.example.mynotes.ui.theme.NDot
import com.example.mynotes.ui.theme.NRegular
import com.example.mynotes.widgets.NoteWidget
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft
import compose.icons.tablericons.CursorText
import compose.icons.tablericons.Settings
import compose.icons.tablericons.Trash
import kotlinx.coroutines.launch

@Composable
fun FolderPage(folderId:Long,navController: NavController, folderViewModel: FolderViewModel, noteViewModel: NoteViewModel){


    val context = LocalContext.current


    val folderTitle = remember{mutableStateOf("")}
    val currentFolder = remember{mutableStateOf<FolderModel?>(null)}

    val coroutineScope = rememberCoroutineScope()



    LaunchedEffect(folderId) {//initialize folder
        folderViewModel.getFolderById(folderId){folder->
            folder.let {
                currentFolder.value=it
                folderTitle.value=it.title
            }
        }
        noteViewModel.setFolderId(folderId)
    }

    val folderNotes=noteViewModel.folderNotes.collectAsState(initial = emptyList())

    val tempTitle = remember{mutableStateOf("")}
    val showDialogForFolderUpdate = remember{mutableStateOf(false)}
    val focusRequester = remember{FocusRequester()}
    val keyboardController = LocalSoftwareKeyboardController.current
    if (showDialogForFolderUpdate.value) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
        Dialog(onDismissRequest = {
            showDialogForFolderUpdate.value = false
            tempTitle.value=""
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
                    Text(text = "CHANGE FOLDER NAME", style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 18.sp,
                        color = Color(0xFFDEDEDE)
                    ))
                    Spacer(modifier = Modifier.height(30.dp))
                    BasicTextField(
                        modifier = Modifier.fillMaxWidth().padding(10.dp).focusRequester(focusRequester),
                        value = tempTitle.value,
                        onValueChange = { tempTitle.value = it },
                        textStyle = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 20.sp,
                            color = Color.White,
                        ),
                        decorationBox = { innerTextField ->
                            if(tempTitle.value.isEmpty()){
                                Text(
                                    text = folderTitle.value,
                                    style = TextStyle(
                                        fontFamily = NRegular,
                                        fontSize = 20.sp,
                                        color = Color(0xFF8C8C8C)
                                    )
                                )
                            }
                            innerTextField()
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        singleLine = true,
                        cursorBrush = SolidColor(Color.White),
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        modifier = Modifier.padding(10.dp).fillMaxWidth()
                            .clip(RoundedCornerShape(50.dp)),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFCB070D)
                        ),
                        onClick = {
                            val updatedFolder = currentFolder.value!!.copy(title = tempTitle.value)
                            folderViewModel.updateFolder(updatedFolder)
                            folderTitle.value=tempTitle.value
                            showDialogForFolderUpdate.value=false
                            tempTitle.value=""
                        }) {
                        Text(modifier = Modifier.padding(vertical = 5.dp), text = "CHANGE", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = {
                            showDialogForFolderUpdate.value = false
                            tempTitle.value=""
                        },
                        border = BorderStroke(0.dp, Color.Transparent),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent
                        )) {
                        Text(text = "CANCEL", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color(0xFFB6B6B6),
                        ))
                    }
                }
            }
        }
    }


    val deleteWithFilesRequest = remember{mutableStateOf(false)}
    val showDialogForDelete = remember{mutableStateOf(false)}

    LaunchedEffect(deleteWithFilesRequest.value) {
        if(deleteWithFilesRequest.value){
            folderNotes.value.forEach{
                noteViewModel.deleteNote(it)//awaits
            }
            folderViewModel.deleteFolder(currentFolder.value!!)//awaits
            Toast.makeText(context, "Folder Deleted with Files", Toast.LENGTH_SHORT).show()
            showDialogForDelete.value=false
            navController.popBackStack()
            deleteWithFilesRequest.value=false
        }
    }

    if(showDialogForDelete.value){
        Dialog(onDismissRequest = {
            showDialogForDelete.value = false
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
                    Text(text = "DELETE FOLDER", style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 18.sp,
                        color = Color(0xFFDEDEDE)
                    ))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(textAlign = TextAlign.Center,
                        text = "Delete All Files Within The Folder?", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color(0xFFB6B6B6)
                        ))
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        modifier = Modifier.padding(10.dp).fillMaxWidth()
                            .clip(RoundedCornerShape(50.dp)),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFCB070D)
                        ),
                        onClick = {
                            deleteWithFilesRequest.value=true
                        }) {
                        Text(modifier = Modifier.padding(vertical = 5.dp), text = "DELETE WITH FILES", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        modifier = Modifier.padding(10.dp).fillMaxWidth()
                            .clip(RoundedCornerShape(50.dp)),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFCB070D)
                        ),
                        onClick = {
                            coroutineScope.launch {
                                folderViewModel.deleteFolder(currentFolder.value!!)
                                Toast.makeText(context, "Folder Deleted", Toast.LENGTH_SHORT).show()
                                showDialogForDelete.value=false
                                navController.popBackStack()
                            }
                        }) {
                        Text(modifier = Modifier.padding(vertical = 5.dp), text = "DELETE ONLY FOLDER", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = {
                            showDialogForDelete.value = false
                        },
                        border = BorderStroke(0.dp, Color.Transparent),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent
                        )) {
                        Text(text = "CANCEL", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color(0xFFB6B6B6),
                        ))
                    }
                }
            }
        }
    }

    BackHandler(enabled = !deleteWithFilesRequest.value) {
        //disable when deleting
        navController.popBackStack()
    }



    val expandedSettings = remember{mutableStateOf(false)}


    val isSelectedFilesInFolder = remember{mutableStateOf(false)}
    val selectedFilesListInFolder = remember{mutableStateListOf<NoteModel>()}

    Scaffold(
        containerColor = Color.Black,
        contentWindowInsets = WindowInsets.safeContent,
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp, horizontal = 0.dp)
                    .windowInsetsPadding(WindowInsets.statusBars),
                backgroundColor = Color.Black,
                title = {
                    if(!deleteWithFilesRequest.value){
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                        ) {
                            Icon(
                                imageVector = TablerIcons.ArrowLeft,
                                contentDescription = "back",
                                tint = Color.White,
                                modifier = Modifier.size(25.dp).clickable(onClick = {navController.popBackStack()})
                            )
                            Spacer(modifier = Modifier.width(25.dp))
                            Text(
                                text = if(folderTitle.value.isEmpty())"Untitled" else folderTitle.value,
                                style = TextStyle(fontFamily = NDot, fontSize = 33.sp, color = Color.White, fontWeight = FontWeight.Normal,),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                actions = {
                    if(!deleteWithFilesRequest.value){
                        Box {
                            IconButton(
                                onClick = { expandedSettings.value = true },
                            ) {
                                Icon(
                                    imageVector = TablerIcons.Settings,
                                    contentDescription = "Settings",
                                    tint = Color.White,
                                    modifier = Modifier.size(23.dp)
                                )
                            }
                            DropdownMenu(
                                expanded = expandedSettings.value,
                                onDismissRequest = { expandedSettings.value = false },
                                offset = DpOffset(x = (-5).dp, y = (0).dp),
                                modifier = Modifier
                                    .background(Color(0xFA131313))
                            ) {
                                DropdownMenuItem(
                                    onClick = {
                                        expandedSettings.value = false
                                        showDialogForFolderUpdate.value=true
                                    },
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                tint = Color.White,
                                                imageVector = TablerIcons.CursorText,
                                                contentDescription = "Change Name",
                                            )
                                            Text(
                                                text = "Change Folder Name",
                                                modifier = Modifier.padding(start = 15.dp)
                                            )
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                DropdownMenuItem(
                                    onClick = {
                                        expandedSettings.value = false
                                        showDialogForDelete.value=true
                                    },
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                tint = Color.Red,
                                                imageVector = TablerIcons.Trash,
                                                contentDescription = "Delete"
                                            )
                                            Text(
                                                text = "Delete Folder",
                                                modifier = Modifier.padding(start = 15.dp),
                                                color = Color.Red,
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if(!deleteWithFilesRequest.value){
                Box(modifier = Modifier.padding(20.dp)) {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate("card_page/-1/${folderId}/false")
                        },
                        containerColor = Color(0xFFCB070D),
                        contentColor = Color.White,
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                            text = "ADD NOTE",
                            style = TextStyle(
                                fontFamily = NRegular,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.EndOverlay,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding() - 5.dp,
                    start = 5.dp,
                    end = 5.dp,
                    bottom = innerPadding.calculateBottomPadding() - 30.dp
                )
                .clip(RoundedCornerShape(20.dp))
        ) {
            if(deleteWithFilesRequest.value){
                Box(modifier = Modifier.fillMaxSize()){
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "DELETING...",
                            style = TextStyle(
                                fontFamily = NRegular,
                                fontSize = 15.sp,
                                color = Color(0xFFB9B9B9)
                            )
                        )
                    }
                }
            }
            else{
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        NoteWidget(noteViewModel = noteViewModel, navController = navController, folderId = folderId, isFolder = true,
                            isSelectedFiles = isSelectedFilesInFolder, selectedNoteList = selectedFilesListInFolder)
                    }
                }
            }
        }
    }
}