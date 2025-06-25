package com.example.mynotes.pages

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.mynotes.R
import com.example.mynotes.model.FolderModel
import com.example.mynotes.ui.theme.NRegular
import com.example.mynotes.data.viewmodel.FolderViewModel
import com.example.mynotes.data.viewmodel.LockViewModel
import com.example.mynotes.data.viewmodel.NoteBlockViewModel
import com.example.mynotes.data.viewmodel.NoteViewModel
import com.example.mynotes.model.NoteModel
import com.example.mynotes.widgets.AppBarWidget
import com.example.mynotes.widgets.NoteWidget
import com.example.mynotes.widgets.FolderWidget
import com.example.mynotes.widgets.PrivateWidget
import compose.icons.TablerIcons
import compose.icons.tablericons.Lock
import compose.icons.tablericons.LockOpen
import compose.icons.tablericons.Plus
import compose.icons.tablericons.Trash
import kotlinx.coroutines.launch


@Composable
fun HomePage(navController: NavController,isPrivateUnlocked: MutableState<Boolean>, headSelect: MutableState<String>,
             lockViewModel: LockViewModel, noteViewModel: NoteViewModel, folderViewModel: FolderViewModel,
             noteBlockViewModel: NoteBlockViewModel){
    val searchSelect: MutableState<Boolean> = remember{mutableStateOf(false)}
    fun searchToggle(){
        searchSelect.value = !searchSelect.value
    }

    val folderNotes=noteViewModel.folderNotes.collectAsState(initial = emptyList())



    //for adding new folder
    val folderTitle = remember{mutableStateOf("")}
    val showDialogForNewFolder = remember { mutableStateOf(false) }
    val focusRequester = remember{ FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    if (showDialogForNewFolder.value) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
        Dialog(onDismissRequest = {
            showDialogForNewFolder.value = false
            folderTitle.value=""
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
                    Text(text = "ADD A FOLDER", style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 18.sp,
                        color = Color(0xFFDEDEDE)
                    ))
                    Spacer(modifier = Modifier.height(30.dp))
                    BasicTextField(
                        modifier = Modifier.fillMaxWidth().padding(10.dp).focusRequester(focusRequester),
                        value = folderTitle.value,
                        onValueChange = { folderTitle.value = it },
                        textStyle = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 20.sp,
                            color = Color.White,
                        ),
                        decorationBox = { innerTextField ->
                            if(folderTitle.value.isEmpty()){
                                Text(
                                    text = "Folder Name",
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
                            val newFolder = FolderModel(title = folderTitle.value, createdAt = System.currentTimeMillis())
                            folderViewModel.insertFolder(newFolder)
                            showDialogForNewFolder.value=false
                            folderTitle.value=""
                        }) {
                        Text(modifier = Modifier.padding(vertical = 5.dp), text = "ADD FOLDER", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = {
                        showDialogForNewFolder.value = false
                        folderTitle.value=""
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



    val showDialogForChangePassword = remember { mutableStateOf(false) }
    if (showDialogForChangePassword.value) {
        Dialog(onDismissRequest = {
            showDialogForChangePassword.value = false
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
                    Text(text = "Are You Sure To Change Password", style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 18.sp,
                        color = Color(0xFFDEDEDE),
                        textAlign = TextAlign.Center
                    ))
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        modifier = Modifier.padding(10.dp).fillMaxWidth()
                            .clip(RoundedCornerShape(50.dp)),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFCB070D)
                        ),
                        onClick = {
                            navController.navigate("private_lock_setup_page")
                            showDialogForChangePassword.value=false
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
                            showDialogForChangePassword.value = false
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


    val selectedNoteList = remember{mutableStateListOf<NoteModel>()}
    val isSelectedFiles = remember { mutableStateOf(false) }
    val selectedFolderList = remember{mutableStateListOf<FolderModel>()}
    val isSelectedFolders = remember { mutableStateOf(false) }
    val selectedNoteListInPrivate = remember{mutableStateListOf<NoteModel>()}
    val isSelectedFilesInPrivate = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current


    //=========================================Select operations for note widget==============================================

    //for adding a note to a folder
    val showDialogForAddFolderWithSelected = remember{mutableStateOf(false)}
    val showDialogForNewFolderWithSelected = remember { mutableStateOf(false) } //for creating new folder

    fun updateFolderForSelectedFiles(folderId: Long){
        coroutineScope.launch {
            selectedNoteList.forEach { note->
                val updatedNote = note.copy(
                    folderId = folderId
                )
                noteViewModel.updateNote(updatedNote) // suspend call awaits
            }
            Toast.makeText(context, "Moved To Folder", Toast.LENGTH_SHORT).show()
            noteViewModel.setIsUpdateDone(bool=false)
            showDialogForAddFolderWithSelected.value = false
            isSelectedFiles.value=false
        }
    }

    if(showDialogForAddFolderWithSelected.value){ //for adding a note to a folder
        Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = {
                showDialogForAddFolderWithSelected.value = false
            }) {
            Box(
                modifier = Modifier
                    .width(360.dp)// You can control width here!
                    .height(600.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFA131313))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    item{
                        Text(modifier = Modifier.fillMaxWidth(),
                            text = "CHOOSE A FOLDER", style = TextStyle(
                                fontFamily = NRegular,
                                fontSize = 18.sp,
                                color = Color(0xFFDEDEDE),
                                textAlign = TextAlign.Center,
                            ))
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                    item {
                        FolderWidget(
                            folderViewModel = folderViewModel,
                            navController = navController,
                            isNotePageDialog = true,
                            onFolderSelectedWithSelectedFolders = ::updateFolderForSelectedFiles,
                            isSelectedFiles = isSelectedFiles
                        )
                    }
                }
                FloatingActionButton(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    onClick = {
                        showDialogForNewFolderWithSelected.value=true
                    },
                    containerColor = Color(0xFFCB070D),
                    shape = RoundedCornerShape(30.dp),
                ) {
                    Icon(
                        tint = Color(0xFFDADADA),
                        imageVector = TablerIcons.Plus,
                        contentDescription = "Add",
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }
    }


    //for creating a folder
    val focusRequesterForNewFolder = remember{ FocusRequester() }
    val tempTitle= remember { mutableStateOf("") }

    if (showDialogForNewFolderWithSelected.value) {
        LaunchedEffect(Unit) {
            focusRequesterForNewFolder.requestFocus()
            keyboardController?.show()
        }
        Dialog(onDismissRequest = {
            showDialogForNewFolderWithSelected.value = false
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
                    Text(text = "ADD A FOLDER", style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 18.sp,
                        color = Color(0xFFDEDEDE)
                    ))
                    Spacer(modifier = Modifier.height(30.dp))
                    BasicTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .focusRequester(focusRequesterForNewFolder),
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
                                    text = "Folder Name",
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
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50.dp)),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFCB070D)
                        ),
                        onClick = {
                            val newFolder = FolderModel(title = tempTitle.value, createdAt = System.currentTimeMillis())
                            folderViewModel.insertFolder(newFolder)
                            showDialogForNewFolderWithSelected.value=false
                            tempTitle.value=""
                        }) {
                        Text(modifier = Modifier.padding(vertical = 5.dp), text = "ADD FOLDER", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = {
                            showDialogForNewFolderWithSelected.value = false
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



    val showDialogForPrivateFilesWithSelected = remember{mutableStateOf(false)}

    val lock=lockViewModel.lock

    val showDialogForPrivateSetup=remember{mutableStateOf(false)}
    if (showDialogForPrivateSetup.value) {
        Dialog(onDismissRequest = {
            showDialogForPrivateSetup.value = false
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
                    Text(text = "Private Files", style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 18.sp,
                        color = Color(0xFFDEDEDE)
                    ))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(textAlign = TextAlign.Center,
                        text = "Private Files Hasn't Set Up", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color(0xFFB6B6B6)
                        ))
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50.dp)),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFCB070D)
                        ),
                        onClick = {
                            showDialogForPrivateSetup.value=false
                            navController.navigate("private_lock_setup_page")
                        }) {
                        Text(modifier = Modifier.padding(vertical = 5.dp), text = "SET PRIVATE FILES", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = {
                            showDialogForPrivateSetup.value = false
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

    if(showDialogForPrivateFilesWithSelected.value){
        Dialog(onDismissRequest = {
            showDialogForPrivateFilesWithSelected.value = false
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
                    Text(text = "LOCK NOTES", style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 18.sp,
                        color = Color(0xFFDEDEDE)
                    ))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(textAlign = TextAlign.Center,
                        text = "Are You Sure To Move Notes To Private Files", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color(0xFFB6B6B6)
                        ))
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50.dp)),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFCB070D)
                        ),
                        onClick = {
                            if(lock.value==null){
                                showDialogForPrivateFilesWithSelected.value=false
                                showDialogForPrivateSetup.value=true
                            }
                            else{
                                coroutineScope.launch {
                                    selectedNoteList.forEach { note->
                                        val updatedNote = note.copy(
                                            isPrivate = true,
                                        )
                                        noteViewModel.updateNote(updatedNote) //awaits
                                    }
                                    Toast.makeText(context,"Notes Moved to Private Files", Toast.LENGTH_SHORT).show()
                                    showDialogForPrivateFilesWithSelected.value=false
                                }
                            }
                        }) {
                        Text(modifier = Modifier.padding(vertical = 5.dp), text = "LOCK", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = {
                            showDialogForPrivateFilesWithSelected.value = false
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

    val showDialogForDeleteWithSelected = remember{mutableStateOf(false)}
    if(showDialogForDeleteWithSelected.value){
        Dialog(onDismissRequest = {
            showDialogForDeleteWithSelected.value = false
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
                    Text(text = "DELETE NOTE", style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 18.sp,
                        color = Color(0xFFDEDEDE)
                    ))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(textAlign = TextAlign.Center,
                        text = "Are You Sure To Delete Selected Notes", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color(0xFFB6B6B6)
                        ))
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50.dp)),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFCB070D)
                        ),
                        onClick = {
                            coroutineScope.launch{
                                selectedNoteList.forEach { note->
                                    noteBlockViewModel.deleteBlocksByNoteId(note.id)//awaits
                                    noteViewModel.deleteNote(note)//awaits
                                }
                                Toast.makeText(context,"Notes Deleted", Toast.LENGTH_SHORT).show()
                                showDialogForDeleteWithSelected.value=false
                            }
                        }) {
                        Text(modifier = Modifier.padding(vertical = 5.dp), text = "DELETE", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = {
                            showDialogForDeleteWithSelected.value = false
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

    //=========================================Select operations for note widget==============================================

    //=========================================Select operations for Folder widget==============================================

    val deleteFoldersWithFilesRequestWithSelected = remember{mutableStateOf(false)}
    val showDialogForFoldersDeleteWithSelected = remember{mutableStateOf(false)}

    LaunchedEffect(deleteFoldersWithFilesRequestWithSelected.value) {
        if(deleteFoldersWithFilesRequestWithSelected.value){
            selectedFolderList.forEach { folder->
                noteViewModel.setFolderId(folder.id)
                folderNotes.value.forEach {
                    noteViewModel.deleteNote(it)//awaits
                }
                folderViewModel.deleteFolder(folder)//awaits
            }
            Toast.makeText(context, "Folder Deleted with Files", Toast.LENGTH_SHORT).show()
            showDialogForFoldersDeleteWithSelected.value=false
            deleteFoldersWithFilesRequestWithSelected.value=false
        }
    }
    if(showDialogForFoldersDeleteWithSelected.value){
        Dialog(onDismissRequest = {
            showDialogForFoldersDeleteWithSelected.value = false
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
                    Text(text = "DELETE FOLDERS", style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 18.sp,
                        color = Color(0xFFDEDEDE),
                        textAlign = TextAlign.Center
                    ))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(textAlign = TextAlign.Center,
                        text = "Delete All Files Within The Selected Folders?", style = TextStyle(
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
                            deleteFoldersWithFilesRequestWithSelected.value=true
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
                                selectedFolderList.forEach { folder->
                                    folderViewModel.deleteFolder(folder)//awaits
                                }
                            }
                            Toast.makeText(context, "Folders Deleted", Toast.LENGTH_SHORT).show()
                            showDialogForFoldersDeleteWithSelected.value=false
                        }) {
                        Text(modifier = Modifier.padding(vertical = 5.dp), text = "DELETE ONLY FOLDERS", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = {
                            showDialogForFoldersDeleteWithSelected.value = false
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

    //=========================================Select operations for Folder widget==============================================

    //=========================================Select operations for Private widget==============================================

    val showDialogToUnlockPrivateFilesWithSelected = remember{mutableStateOf(false)}

    if(showDialogToUnlockPrivateFilesWithSelected.value){
        Dialog(onDismissRequest = {
            showDialogToUnlockPrivateFilesWithSelected.value = false
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
                    Text(text = "UNLOCK NOTES", style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 18.sp,
                        color = Color(0xFFDEDEDE)
                    ))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(textAlign = TextAlign.Center,
                        text = "Are You Sure To Unlock Selected Files", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color(0xFFB6B6B6)
                        ))
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50.dp)),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFCB070D)
                        ),
                        onClick = {
                            coroutineScope.launch {
                                selectedNoteListInPrivate.forEach { note->
                                    val updatedNote = note.copy(
                                        isPrivate = false,
                                    )
                                    noteViewModel.updateNote(updatedNote) //awaits
                                }
                                Toast.makeText(context,"Notes Moved to Public Files", Toast.LENGTH_SHORT).show()
                                showDialogToUnlockPrivateFilesWithSelected.value=false
                            }
                        }) {
                        Text(modifier = Modifier.padding(vertical = 5.dp), text = "UNLOCK", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = {
                            showDialogToUnlockPrivateFilesWithSelected.value = false
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


    val showDialogForPrivateFilesDeleteWithSelected = remember{mutableStateOf(false)}
    if(showDialogForPrivateFilesDeleteWithSelected.value){
        Dialog(onDismissRequest = {
            showDialogForPrivateFilesDeleteWithSelected.value = false
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
                    Text(text = "DELETE NOTE", style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 18.sp,
                        color = Color(0xFFDEDEDE)
                    ))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(textAlign = TextAlign.Center,
                        text = "Are You Sure To Delete Selected Notes", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color(0xFFB6B6B6)
                        ))
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50.dp)),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFCB070D)
                        ),
                        onClick = {
                            coroutineScope.launch{
                                selectedNoteListInPrivate.forEach { note->
                                    noteBlockViewModel.deleteBlocksByNoteId(note.id)//awaits
                                    noteViewModel.deleteNote(note)//awaits
                                }
                                Toast.makeText(context,"Notes Deleted", Toast.LENGTH_SHORT).show()
                                showDialogForPrivateFilesDeleteWithSelected.value=false
                            }
                        }) {
                        Text(modifier = Modifier.padding(vertical = 5.dp), text = "DELETE", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = {
                            showDialogForPrivateFilesDeleteWithSelected.value = false
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
    val activity = LocalActivity.current
    BackHandler(enabled = true) {
        when {
            isSelectedFiles.value -> {
                isSelectedFiles.value = false
                selectedNoteList.clear()
            }
            isSelectedFolders.value -> {
                isSelectedFolders.value = false
                selectedFolderList.clear()
            }
            isSelectedFilesInPrivate.value -> {
                isSelectedFilesInPrivate.value = false
                selectedNoteListInPrivate.clear()
            }
            else -> {
                activity?.finish()
            }
        }
    }





    Scaffold(containerColor = Color.Black,
        contentWindowInsets = WindowInsets.safeContent,
        floatingActionButton = {
            Box(modifier = Modifier.padding(20.dp)) {
                var enableFloatingButton = headSelect.value=="ALL"||headSelect.value=="FOLDERS"||(headSelect.value=="PRIVATE"&&isPrivateUnlocked.value)
                if(enableFloatingButton){
                    if(isSelectedFiles.value){
                        FloatingActionButton(
                            onClick = {},
                            containerColor = Color(0xDA19181E),
                            shape = RoundedCornerShape(30.dp),
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Image(
                                    painter = painterResource(id= R.drawable.folder_add),
                                    contentDescription = "Folder add",
                                    modifier = Modifier
                                        .requiredSize(30.dp)
                                        .padding(start = 1.dp).clickable(
                                            onClick = {
                                                showDialogForAddFolderWithSelected.value=true
                                            }
                                        ),
                                    colorFilter = ColorFilter.tint(Color.White),
                                    contentScale = ContentScale.Fit
                                )
                                Icon(
                                    tint = Color.White,
                                    modifier = Modifier
                                        .requiredSize(30.dp)
                                        .padding(start = 1.dp).clickable(
                                            onClick = {
                                                showDialogForPrivateFilesWithSelected.value=true
                                            }
                                        ),
                                    imageVector = TablerIcons.Lock,
                                    contentDescription = "Lock note"
                                )
                                Icon(
                                    tint = Color.Red,
                                    modifier = Modifier
                                        .requiredSize(30.dp)
                                        .padding(start = 1.dp).clickable(
                                            onClick = {
                                                showDialogForDeleteWithSelected.value=true
                                            }
                                        ),
                                    imageVector = TablerIcons.Trash,
                                    contentDescription = "Delete"
                                )
                            }
                        }
                    }
                    else if(isSelectedFolders.value){
                        FloatingActionButton(
                            onClick = {},
                            containerColor = Color(0xDA19181E),
                            shape = RoundedCornerShape(30.dp),
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Icon(
                                    tint = Color.Red,
                                    modifier = Modifier
                                        .requiredSize(30.dp)
                                        .padding(start = 1.dp).clickable(
                                            onClick = {
                                                showDialogForFoldersDeleteWithSelected.value=true
                                            }
                                        ),
                                    imageVector = TablerIcons.Trash,
                                    contentDescription = "Delete"
                                )
                            }
                        }
                    }
                    else if(isSelectedFilesInPrivate.value){
                        FloatingActionButton(
                            onClick = {},
                            containerColor = Color(0xDA19181E),
                            shape = RoundedCornerShape(30.dp),
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Icon(
                                    tint = Color.White,
                                    modifier = Modifier
                                        .requiredSize(30.dp)
                                        .padding(start = 1.dp).clickable(
                                            onClick = {
                                                showDialogToUnlockPrivateFilesWithSelected.value=true
                                            }
                                        ),
                                    imageVector = TablerIcons.LockOpen,
                                    contentDescription = "UNLock note"
                                )
                                Icon(
                                    tint = Color.Red,
                                    modifier = Modifier
                                        .requiredSize(30.dp)
                                        .padding(start = 1.dp).clickable(
                                            onClick = {
                                                showDialogForPrivateFilesDeleteWithSelected.value=true
                                            }
                                        ),
                                    imageVector = TablerIcons.Trash,
                                    contentDescription = "Delete"
                                )
                            }
                        }
                    }
                    else{
                        FloatingActionButton(
                            onClick = {
                                if(headSelect.value=="ALL"){
                                    navController.navigate("card_page/-1/-1/false")
                                }
                                else if(headSelect.value=="FOLDERS"){
                                    showDialogForNewFolder.value=true
                                }
                                else if(headSelect.value=="PRIVATE"){
                                    navController.navigate("card_page/-1/-1/true")
                                }
                            },
                            containerColor = Color(0xFFCB070D),
                            contentColor = Color.White,
                            shape = RoundedCornerShape(30.dp),
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                                text = if(headSelect.value=="ALL"){
                                    "ADD NOTE"
                                }
                                else if(headSelect.value=="FOLDERS"){
                                    "ADD FOLDER"
                                }
                                else{
                                    "ADD PRIVATE FILE"
                                },
                                style = TextStyle(
                                    fontFamily = NRegular,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        },
        floatingActionButtonPosition = if(isSelectedFiles.value) FabPosition.End else FabPosition.EndOverlay
        ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding().pointerInput(Unit){
            detectTapGestures(onTap = {
                searchSelect.value=false
            })
        }){
            LazyColumn() {
                item {
                    AppBarWidget(searchSelect,::searchToggle, headSelect = headSelect, isUnlocked = isPrivateUnlocked,
                        showDialogForChangePassword = showDialogForChangePassword, navController = navController, isSelectedFiles= isSelectedFiles,
                        isSelectedFolders = isSelectedFolders, selectedFolderList = selectedFolderList, selectedNoteList = selectedNoteList,
                        isSelectedFilesInPrivate = isSelectedFilesInPrivate, selectedFilesInPrivate = selectedNoteListInPrivate)
                }
                item{
                    if(!isSelectedFiles.value&&!isSelectedFolders.value&&!isSelectedFilesInPrivate.value){
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically){
                            //Spacer(modifier = Modifier.width( ( (screenWidth / 2)-50.dp))) // Adjustable

                            Box(modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp).clickable(onClick = {
                                headSelect.value="ALL"
                            })) {
                                Text( text = "ALL", style = TextStyle(fontFamily = NRegular, fontSize = 15.sp,
                                    color = if(headSelect.value=="ALL")Color(0xFFE00A0A)else Color(0xFFC4C4C4),
                                    fontWeight = FontWeight.Bold))
                            }
                            Box(modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp).clickable(onClick = {
                                headSelect.value="FOLDERS"
                            })) {
                                Text( text = "FOLDERS", style = TextStyle(fontFamily = NRegular, fontSize = 15.sp,
                                    color = if(headSelect.value=="FOLDERS")Color(0xFFE00A0A)else Color(0xFFC4C4C4),
                                    fontWeight = FontWeight.Bold))
                            }
                            Box(modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp).clickable(onClick = {
                                headSelect.value="PRIVATE"
                            })) {
                                Text( text = "PRIVATE", style = TextStyle(fontFamily = NRegular, fontSize = 15.sp,
                                    color = if(headSelect.value=="PRIVATE")Color(0xFFE00A0A)else Color(0xFFC4C4C4),
                                    fontWeight = FontWeight.Bold))
                            }

                            //Spacer(modifier = Modifier.width( ( (screenWidth / 2)-50.dp))) // Adjustable
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                item {
                    if(headSelect.value=="ALL"){
                        NoteWidget(noteViewModel = noteViewModel,navController = navController, isSelectedFiles = isSelectedFiles,
                            selectedNoteList = selectedNoteList)
                    }
                    else if(headSelect.value=="FOLDERS"){
                        FolderWidget(folderViewModel = folderViewModel, navController = navController, isSelectedFolders = isSelectedFolders,
                            selectedFolderList = selectedFolderList)
                    }
                    else if(headSelect.value=="PRIVATE"){
                        PrivateWidget(noteViewModel = noteViewModel, lockViewModel = lockViewModel, isUnlocked = isPrivateUnlocked, navController = navController,
                            isSelectedFilesInPrivate = isSelectedFilesInPrivate, selectedNoteListInPrivate = selectedNoteListInPrivate)
                    }
                }
            }
        }
 }
    //RecordingPage()
}