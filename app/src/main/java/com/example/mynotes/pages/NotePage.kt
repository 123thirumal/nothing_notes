package com.example.mynotes.pages

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Button
import androidx.compose.material.TextButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.gif.GifDecoder
import coil3.request.ImageRequest
import com.example.mynotes.R
import com.example.mynotes.data.viewmodel.FolderViewModel
import com.example.mynotes.data.viewmodel.ImageDemoViewModel
import com.example.mynotes.data.viewmodel.LockViewModel
import com.example.mynotes.data.viewmodel.NoteBlockViewModel
import com.example.mynotes.model.NoteModel
import com.example.mynotes.ui.theme.NDot
import com.example.mynotes.ui.theme.NRegular
import com.example.mynotes.data.viewmodel.NoteViewModel
import com.example.mynotes.model.CheckListModel
import com.example.mynotes.model.FolderModel
import com.example.mynotes.model.ListModel
import com.example.mynotes.model.NoteBlockEntityModel
import com.example.mynotes.model.NoteBlockModel
import com.example.mynotes.utils.convertToNoteBlockEntityModel
import com.example.mynotes.utils.convertToNoteBlockModel
import com.example.mynotes.widgets.FolderWidget
import com.example.mynotes.widgets.noteblockwidgets.PhotoWidget
import com.example.mynotes.widgets.noteblockwidgets.TextWidget
import com.example.mynotes.widgets.noteblockwidgets.list.BulletedListWidget
import com.example.mynotes.widgets.noteblockwidgets.list.CheckListWidget
import com.example.mynotes.widgets.noteblockwidgets.list.NumberedListWidget
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowBackUp
import compose.icons.tablericons.ArrowForwardUp
import compose.icons.tablericons.ArrowLeft
import compose.icons.tablericons.Camera
import compose.icons.tablericons.FolderOff
import compose.icons.tablericons.List
import compose.icons.tablericons.ListCheck
import compose.icons.tablericons.Lock
import compose.icons.tablericons.LockOpen
import compose.icons.tablericons.Photo
import compose.icons.tablericons.Plus
import compose.icons.tablericons.Settings
import compose.icons.tablericons.Trash
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun NotePage(folderViewModel: FolderViewModel, lockViewModel: LockViewModel, noteViewModel: NoteViewModel, newNote: Boolean, noteId: Long=-1L, navController: NavController, folderId:Long=-1L,
             isPrivate: Boolean=false, noteBlockViewModel: NoteBlockViewModel,
             imageDemoViewModel: ImageDemoViewModel) { //isPrivate for creating new note in private files


    val isChangeMade = remember{mutableStateOf(false)}

    val noteBlockEntityList = remember{mutableStateListOf<NoteBlockEntityModel>()}

    val coroutineScope = rememberCoroutineScope()

    val currentNote = remember{mutableStateOf<NoteModel?>(null)}

    val lock=lockViewModel.lock


    val cardTitle = remember{ mutableStateOf("") }
    val isCardLocked = remember{mutableStateOf(false)}
    val focusRequester = remember{FocusRequester()}
    val keyboardController = LocalSoftwareKeyboardController.current


    val isInitialized = remember{mutableStateOf(false)}
    LaunchedEffect(newNote, noteId) {
        if (!newNote && noteId != -1L) {
            // Load existing note

            val loadedNote = noteViewModel.getNoteById(noteId) //awaits

            isInitialized.value=false

            currentNote.value=loadedNote
            cardTitle.value = loadedNote.title?:""
            isCardLocked.value = loadedNote.isPrivate

            noteBlockEntityList.addAll(noteBlockViewModel.getBlocksByNoteId(noteId).map { convertToNoteBlockEntityModel(it) }) //awaits

            isInitialized.value=true
        } else {
            // Create new note

            val newNoteModel = when {
                isPrivate -> NoteModel(createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis(), isPrivate = true)
                folderId == -1L -> NoteModel(createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis())
                else -> NoteModel(createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis(), folderId = folderId)
            }

            isInitialized.value=false

            val createdNote = noteViewModel.insertNote(newNoteModel) //awaits

            currentNote.value=createdNote
            cardTitle.value = createdNote.title?:""
            isCardLocked.value = createdNote.isPrivate

            // Add one empty TextBlock
            noteBlockEntityList.add(NoteBlockEntityModel.TextBlock(description = mutableStateOf("")))

            // Focus keyboard
            focusRequester.requestFocus()
            keyboardController?.show()

            isInitialized.value=true
        }
    }



    val context = LocalContext.current

    //to wait for save
    val isSavePress=remember{mutableStateOf(false)}
    val isLockPress=remember{mutableStateOf(false)}
    val isFolderUpdate=remember{mutableStateOf(false)}
    val isFolderRemove=remember{mutableStateOf(false)}
    val isUpdateDone = noteViewModel.isUpdateDone
    LaunchedEffect(isUpdateDone.value){
        if(isUpdateDone.value&&isSavePress.value){
            Toast.makeText(context, "Note Saved", Toast.LENGTH_SHORT).show()
            isSavePress.value=false
            isChangeMade.value=false
            noteViewModel.setIsUpdateDone(bool = false)

            navController.popBackStack()
        }
        else if(isUpdateDone.value&&isLockPress.value){
            if(isCardLocked.value){
                Toast.makeText(context, "Note Moved To Private Files", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context, "Note Moved To Public Files", Toast.LENGTH_SHORT).show()
            }
            isLockPress.value=false
            noteViewModel.setIsUpdateDone(bool = false)
        }
        else if(isUpdateDone.value&&isFolderUpdate.value){
            Toast.makeText(context, "Moved To Folder", Toast.LENGTH_SHORT).show()
            isFolderUpdate.value=false
            noteViewModel.setIsUpdateDone(bool = false)
        }
        else if(isUpdateDone.value&&isFolderRemove.value){
            Toast.makeText(context, "Removed From Folder", Toast.LENGTH_SHORT).show()
            isFolderRemove.value=false
            noteViewModel.setIsUpdateDone(bool = false)
        }
    }


    var expandedList by remember { mutableStateOf(false) }
    var expandedSettings by remember { mutableStateOf(false) }
//    var selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    var photoUri = remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                val inputStream = context.contentResolver.openInputStream(uri)
                val fileName = "gallery_photo_${System.currentTimeMillis()}.jpg"
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)

                inputStream.use { input ->
                    file.outputStream().use { output ->
                        input?.copyTo(output)
                    }
                }

                val savedUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )

                val last = noteBlockEntityList.lastOrNull()
                if (last is NoteBlockEntityModel.TextBlock && last.description.value.trim().isEmpty()) {
                    noteBlockEntityList.remove(last)
                }

                noteBlockEntityList.add(NoteBlockEntityModel.ImageBlock(uri = mutableStateOf(savedUri)))
                noteBlockEntityList.add(NoteBlockEntityModel.TextBlock(description = mutableStateOf("")))
                isChangeMade.value = true
            }
        }
    )
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success: Boolean ->
            if(success){
                val last = noteBlockEntityList.lastOrNull()
                if (last is NoteBlockEntityModel.TextBlock && last.description.value.trim().isEmpty()) {
                    noteBlockEntityList.remove(last)
                }
                noteBlockEntityList.add(NoteBlockEntityModel.ImageBlock(uri = mutableStateOf(photoUri.value)))
                noteBlockEntityList.add(NoteBlockEntityModel.TextBlock(description = mutableStateOf("")))
                isChangeMade.value=true
            }
        }
    )



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

    val deleteRequest = remember{mutableStateOf(false)}
    val showDialogForDelete = remember{mutableStateOf(false)}
    LaunchedEffect(deleteRequest.value) {
        if(deleteRequest.value){
            noteBlockViewModel.deleteBlocksByNoteId(currentNote.value!!.id)//awaits
            noteViewModel.deleteNote(currentNote.value!!)//awaits
            Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show()
            showDialogForDelete.value=false
            isChangeMade.value=false
            deleteRequest.value=false
            navController.popBackStack()
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
                    Text(text = "DELETE NOTE", style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 18.sp,
                        color = Color(0xFFDEDEDE)
                    ))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(textAlign = TextAlign.Center,
                        text = "Are You Sure To Delete Note", style = TextStyle(
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
                            deleteRequest.value=true
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


    //for adding a note to a folder
    val showDialogForAddFolder = remember{mutableStateOf(false)}

    fun updateFolder(folderId: Long){
        coroutineScope.launch {
            isFolderUpdate.value = true

            val updatedNote = currentNote.value!!.copy(
                folderId = folderId
            )

            noteViewModel.updateNote(updatedNote) // suspend call awaits
            //Toast.makeText(context, "Moved To Folder", Toast.LENGTH_SHORT).show()
            currentNote.value = updatedNote
            showDialogForAddFolder.value = false
        }
    }



    val showDialogForNewFolder = remember { mutableStateOf(false) } //for creating new folder

    if(showDialogForAddFolder.value){ //for adding a note to a folder
        Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = {
            showDialogForAddFolder.value = false
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
                            onFolderSelected = ::updateFolder
                        )
                    }
                }
                FloatingActionButton(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    onClick = {
                        showDialogForNewFolder.value=true
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

    if (showDialogForNewFolder.value) {
        LaunchedEffect(Unit) {
            focusRequesterForNewFolder.requestFocus()
            keyboardController?.show()
        }
        Dialog(onDismissRequest = {
            showDialogForNewFolder.value = false
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
                            showDialogForNewFolder.value=false
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
                            showDialogForNewFolder.value = false
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


    fun saveNote() {
        coroutineScope.launch {
            val note = currentNote.value
            if (note != null) {
                val updatedNote = note.copy(
                    title = cardTitle.value,
                    updatedAt = System.currentTimeMillis()
                )
                noteViewModel.updateNote(updatedNote)

                noteBlockViewModel.deleteBlocksByNoteId(updatedNote.id)

                noteBlockEntityList.forEachIndexed { index, block ->
                    val convertedBlock = convertToNoteBlockModel(
                        block = block,
                        noteId = updatedNote.id,
                        order = index
                    )
                    noteBlockViewModel.insertNoteBlock(convertedBlock)
                }

                isSavePress.value = true
                isChangeMade.value = false
            }
            navController.popBackStack()
        }
    }

    //prevent back while not saved
    val showDialogWhenNotSaved = remember{mutableStateOf(false)}
    BackHandler(enabled = !deleteRequest.value) {
        if(isChangeMade.value){
            showDialogWhenNotSaved.value=true
        }
        else{
            navController.popBackStack()
        }
    }

    if (showDialogWhenNotSaved.value) {
        Dialog(onDismissRequest = {
            showDialogWhenNotSaved.value = false
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
                    Text(text = "SAVE NOTE", style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 18.sp,
                        color = Color(0xFFDEDEDE)
                    ))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(textAlign = TextAlign.Center,
                        text = "Changes made to note haven't saved", style = TextStyle(
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
                            saveNote()
                            isChangeMade.value=false
                            showDialogWhenNotSaved.value=false
                        }) {
                        Text(modifier = Modifier.padding(vertical = 5.dp), text = "SAVE NOTE", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = {
                            isChangeMade.value=false
                            showDialogWhenNotSaved.value = false
                            navController.popBackStack()
                        },
                        border = BorderStroke(0.dp, Color.Transparent),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent
                        )) {
                        Text(text = "DON'T SAVE", style = TextStyle(
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
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp, horizontal = 0.dp)
                    .windowInsetsPadding(WindowInsets.statusBars),
                backgroundColor = Color.Black,
                title = {
                    if(!deleteRequest.value){
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 8.dp),
                        ) {
                            Icon(
                                imageVector = TablerIcons.ArrowLeft,
                                contentDescription = "back",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(25.dp)
                                    .clickable(onClick = {
                                        if(isChangeMade.value){
                                            showDialogWhenNotSaved.value=true
                                        }
                                        else{
                                            navController.popBackStack()
                                        }
                                    })
                            )
                            Spacer(modifier = Modifier.width(15.dp))
                            BasicTextField(
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(focusRequester),
                                value = cardTitle.value,
                                onValueChange = {
                                    cardTitle.value = it
                                    isChangeMade.value=true
                                },
                                textStyle = TextStyle(
                                    fontFamily = NDot,
                                    fontSize = 25.sp,
                                    color = Color.White,
                                ),
                                decorationBox = { innerTextField ->
                                    if(cardTitle.value.isEmpty()){
                                        Text(
                                            text = "Your Title",
                                            style = TextStyle(
                                                fontFamily = NDot,
                                                fontSize = 25.sp,
                                                color = Color(0xFF8C8C8C)
                                            )
                                        )
                                    }
                                    innerTextField()
                                },
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                singleLine = true,
                                cursorBrush = SolidColor(Color.White)
                            )
                        }
                    }
                },
                actions = {
                    if(!deleteRequest.value){
//                        IconButton(onClick = {}) {
//                            Icon(
//                                imageVector = TablerIcons.ArrowBackUp,
//                                contentDescription = "Undo",
//                                tint = Color.White,
//                                modifier = Modifier.size(26.dp)
//                            )
//                        }
//                        IconButton(onClick = {}) {
//                            Icon(
//                                imageVector = TablerIcons.ArrowForwardUp,
//                                contentDescription = "Redo",
//                                tint = Color.White,
//                                modifier = Modifier.size(26.dp)
//                            )
//                        }
                        Box {
                            IconButton(
                                onClick = { expandedSettings = true },
                            ) {
                                Icon(
                                    imageVector = TablerIcons.Settings,
                                    contentDescription = "Settings",
                                    tint = Color.White,
                                    modifier = Modifier.size(23.dp)
                                )
                            }
                            DropdownMenu(
                                expanded = expandedSettings,
                                onDismissRequest = { expandedSettings = false },
                                offset = DpOffset(x = (-5).dp, y = (0).dp),
                                modifier = Modifier
                                    .background(Color(0xFA131313))
                            ) {
                                DropdownMenuItem(
                                    onClick = {
                                        expandedSettings = false
                                        showDialogForAddFolder.value=true
                                    },
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Image(
                                                painter = painterResource(id= if(currentNote.value!!.folderId==null)R.drawable.folder_add else R.drawable.folder_move),
                                                contentDescription = "Folder add",
                                                modifier = Modifier
                                                    .requiredSize(23.dp)
                                                    .padding(start = 1.dp),
                                                colorFilter = ColorFilter.tint(Color.White),
                                                contentScale = ContentScale.Fit
                                            )
                                            Text(
                                                text = if(currentNote.value!!.folderId==null) "Add to Folder" else "Move To Folder",
                                                modifier = Modifier.padding(start = 15.dp)
                                            )
                                        }
                                    }
                                )
                                if(currentNote.value!!.folderId!=null){
                                    Spacer(modifier = Modifier.height(10.dp))
                                    DropdownMenuItem(
                                        onClick = {
                                            coroutineScope.launch {
                                                expandedSettings = false
                                                val updatedNote = currentNote.value!!.copy(
                                                    folderId=null
                                                )
                                                noteViewModel.updateNote(updatedNote)
                                                currentNote.value=updatedNote
                                                isFolderRemove.value=true
                                            }
                                        },
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    tint = Color.White,
                                                    imageVector = TablerIcons.FolderOff,
                                                    contentDescription = "Remove Folder"
                                                )
                                                Text(
                                                    text = "Remove From Folder",
                                                    modifier = Modifier.padding(start = 15.dp)

                                                )
                                            }
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                DropdownMenuItem(
                                    onClick = {
                                        expandedSettings = false
                                        if(lock.value==null){
                                            showDialogForPrivateSetup.value=true
                                        }
                                        else{
                                            coroutineScope.launch {
                                                val updatedNote = currentNote.value!!.copy(
                                                    isPrivate = !currentNote.value!!.isPrivate,
                                                )
                                                isCardLocked.value=updatedNote.isPrivate
                                                isLockPress.value=true
                                                currentNote.value = updatedNote
                                                noteViewModel.updateNote(updatedNote) //awaits
                                            }
                                        }
                                    },
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                tint = Color.White,
                                                imageVector = if(isCardLocked.value) TablerIcons.LockOpen else TablerIcons.Lock,
                                                contentDescription = "Lock note"
                                            )
                                            Text(
                                                text = if(isCardLocked.value) "Unlock Note" else "Lock Note",
                                                modifier = Modifier.padding(start = 15.dp)

                                            )

                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                DropdownMenuItem(
                                    onClick = {
                                        expandedSettings = false
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
                                                text = "Delete",
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
        bottomBar = {
            if(!deleteRequest.value){
                BottomAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .imePadding(),
                    backgroundColor = Color.Black,
                ) {
                    Row(modifier = Modifier.fillMaxWidth()  ,
                        horizontalArrangement = Arrangement.SpaceBetween){
                        Box{
                            Row{
                                IconButton(
                                    onClick = {
                                        val imageFile = File(
                                            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                            "photo_${System.currentTimeMillis()}.jpg"
                                        )
                                        photoUri.value = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.fileprovider",
                                            imageFile
                                        )
                                        //camera is launched only if photouri.value is not null
                                        photoUri.value?.let { uri ->
                                            cameraLauncher.launch(uri)
                                        }
                                    },
                                    modifier = Modifier.padding(end = 5.dp)) {
                                    Icon(imageVector = TablerIcons.Camera,
                                        contentDescription = "Camera",
                                        tint = Color.White,
                                        modifier = Modifier.size(23.dp))
                                }
                                IconButton(onClick = {galleryLauncher.launch("image/*")},modifier = Modifier.padding(horizontal = 10.dp)) {
                                    Icon(imageVector = TablerIcons.Photo,
                                        contentDescription = "Gallery",
                                        tint = Color.White,
                                        modifier = Modifier.size(23.dp))
                                }
                                Box {
                                    IconButton(
                                        onClick = {expandedList=true},
                                        modifier = Modifier.padding(horizontal = 10.dp)
                                    ) {
                                        Icon(
                                            imageVector = TablerIcons.List,
                                            contentDescription = "Checklist",
                                            tint = Color.White,
                                            modifier = Modifier.size(23.dp)
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = expandedList,
                                        onDismissRequest = {expandedList=false},
                                        offset = DpOffset(x=(-20).dp,y= (0).dp),
                                        modifier = Modifier
                                            .background(Color(0xFA131313))
                                    ) {
                                        DropdownMenuItem(
                                            onClick = {
                                                expandedList=false
                                                val bulletList = mutableStateListOf<ListModel>()
                                                bulletList.add(ListModel(description = ""))
                                                val last = noteBlockEntityList.lastOrNull()
                                                if (last is NoteBlockEntityModel.TextBlock && last.description.value.trim().isBlank()) {
                                                    noteBlockEntityList.removeAt(noteBlockEntityList.size - 1)
                                                }
                                                noteBlockEntityList.add(NoteBlockEntityModel.BulletedListBlock(bulletList))
                                                noteBlockEntityList.add(NoteBlockEntityModel.TextBlock(description = mutableStateOf("")))
                                                isChangeMade.value=true
                                            },
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically){
                                                    Icon(tint = Color.White, imageVector = TablerIcons.List, contentDescription = "Bulleted list",)
                                                    Text(text = "Bullet-List", modifier = Modifier.padding(start = 15.dp))
                                                }
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        DropdownMenuItem(
                                            onClick = {
                                                expandedList=false
                                                val checkList = mutableStateListOf<CheckListModel>()
                                                checkList.add(CheckListModel(description = ""))
                                                val last = noteBlockEntityList.lastOrNull()
                                                if (last is NoteBlockEntityModel.TextBlock && last.description.value.trim().isBlank()) {
                                                    noteBlockEntityList.removeAt(noteBlockEntityList.size - 1)
                                                }
                                                noteBlockEntityList.add(NoteBlockEntityModel.CheckListBlock(checkList))
                                                noteBlockEntityList.add(NoteBlockEntityModel.TextBlock(description = mutableStateOf("")))
                                                isChangeMade.value=true
                                            },
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically){
                                                    Icon(tint = Color.White, imageVector = TablerIcons.ListCheck, contentDescription = "Checklist")
                                                    Text(text = "Check-List", modifier = Modifier.padding(start = 15.dp))
                                                }
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        DropdownMenuItem(
                                            onClick = {
                                                expandedList=false
                                                val numberList = mutableStateListOf<ListModel>()
                                                numberList.add(ListModel(description = ""))
                                                val last = noteBlockEntityList.lastOrNull()
                                                if (last is NoteBlockEntityModel.TextBlock && last.description.value.trim().isBlank()) {
                                                    noteBlockEntityList.removeAt(noteBlockEntityList.size - 1)
                                                }
                                                noteBlockEntityList.add(NoteBlockEntityModel.NumberedListBlock(numberList))
                                                noteBlockEntityList.add(NoteBlockEntityModel.TextBlock(description = mutableStateOf("")))
                                                isChangeMade.value=true
                                            },
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically){
                                                    Image(
                                                        painter = painterResource(id= R.drawable.numbered_list),
                                                        contentDescription = "Numbered list",
                                                        modifier = Modifier
                                                            .requiredSize(23.dp)
                                                            .padding(start = 1.dp),
                                                        colorFilter = ColorFilter.tint(Color.White),
                                                        contentScale = ContentScale.Fit
                                                    )
                                                    Text(text = "Number-List", modifier = Modifier.padding(start = 15.dp))
                                                }
                                            }
                                        )
                                    }
                                }
//                            IconButton(onClick = {navController.navigate("recording_page")},modifier = Modifier.padding(horizontal = 10.dp)) {
//                                Icon(imageVector = TablerIcons.Microphone,
//                                    contentDescription = "Microphone",
//                                    tint = Color.White,
//                                    modifier = Modifier.size(23.dp))
//                            }
                            }
                        }
                        Box{
                            Row{
                                Button(
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .size(width = 80.dp, height = 50.dp)
                                        .clip(RoundedCornerShape(30.dp)),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color(0xFFCB070D)
                                    ) ,
                                    onClick = {
                                        saveNote()
                                    }) {
                                    Text(
                                        text = "SAVE",
                                        style = TextStyle(
                                            fontFamily = NRegular,
                                            fontSize = 12.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding() - 5.dp,
                    start = 5.dp,
                    end = 5.dp,
                    bottom = innerPadding.calculateBottomPadding(),
                )
        ) {
            if(deleteRequest.value){
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
            else if(!isInitialized.value){
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "LOADING...",
                        style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 15.sp,
                            color = Color(0xFFB9B9B9)
                        )
                    )
                }
            }
            else{
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp)).background(Color.Black),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(noteBlockEntityList) { index, block ->
                        when (block) {
                            is NoteBlockEntityModel.TextBlock -> {
                                if (index > 0 && noteBlockEntityList[index - 1] is NoteBlockEntityModel.TextBlock) {
                                    isChangeMade.value = true
                                    noteBlockEntityList.removeAt(index)
                                } else {
                                    TextWidget(block, isChangeMade = isChangeMade)
                                }
                            }

                            is NoteBlockEntityModel.ImageBlock -> PhotoWidget(
                                block,
                                noteBlockList = noteBlockEntityList,
                                isChangeMade = isChangeMade,
                                imageDemoViewModel = imageDemoViewModel
                            )

                            is NoteBlockEntityModel.CheckListBlock -> {
                                if (block.items.isEmpty()) {
                                    isChangeMade.value = true
                                    noteBlockEntityList.remove(block)
                                } else {
                                    CheckListWidget(block, isChangeMade = isChangeMade)
                                }
                            }

                            is NoteBlockEntityModel.NumberedListBlock -> {
                                if (block.items.isEmpty()) {
                                    isChangeMade.value = true
                                    noteBlockEntityList.remove(block)
                                } else {
                                    NumberedListWidget(block, isChangeMade = isChangeMade)
                                }
                            }

                            is NoteBlockEntityModel.BulletedListBlock -> {
                                if (block.items.isEmpty()) {
                                    isChangeMade.value = true
                                    noteBlockEntityList.remove(block)
                                } else {
                                    BulletedListWidget(block, isChangeMade = isChangeMade)
                                }
                            }
//                        is NoteBlockModel.VoiceBlock -> VoiceWidget()
                        }
                    }
                }
            }
        }
    }
}

