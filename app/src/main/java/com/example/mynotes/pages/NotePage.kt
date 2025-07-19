package com.example.mynotes.pages

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Button
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicNone
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.navigation.NavController
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
import com.example.mynotes.ui.theme.NDot55
import com.example.mynotes.utils.AudioRecorderManager
import com.example.mynotes.utils.SpeechToTextManager
import com.example.mynotes.utils.convertToNoteBlockEntityModel
import com.example.mynotes.utils.convertToNoteBlockModel
import com.example.mynotes.utils.isNetworkAvailable
import com.example.mynotes.widgets.FolderWidget
import com.example.mynotes.widgets.noteblockwidgets.PhotoWidget
import com.example.mynotes.widgets.noteblockwidgets.TextWidget
import com.example.mynotes.widgets.noteblockwidgets.VoiceWidget
import com.example.mynotes.widgets.noteblockwidgets.list.BulletedListWidget
import com.example.mynotes.widgets.noteblockwidgets.list.CheckListWidget
import com.example.mynotes.widgets.noteblockwidgets.list.NumberedListWidget
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft
import compose.icons.tablericons.Camera
import compose.icons.tablericons.FolderOff
import compose.icons.tablericons.List
import compose.icons.tablericons.ListCheck
import compose.icons.tablericons.Lock
import compose.icons.tablericons.LockOpen
import compose.icons.tablericons.Microphone
import compose.icons.tablericons.Photo
import compose.icons.tablericons.PlayerPause
import compose.icons.tablericons.PlayerPlay
import compose.icons.tablericons.Plus
import compose.icons.tablericons.Refresh
import compose.icons.tablericons.Settings
import compose.icons.tablericons.Trash
import compose.icons.tablericons.X
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun NotePage(folderViewModel: FolderViewModel, lockViewModel: LockViewModel, noteViewModel: NoteViewModel, newNote: Boolean, noteId: String="-1", navController: NavController, folderId:String = "-1",
             isPrivate: Boolean=false, noteBlockViewModel: NoteBlockViewModel,
             imageDemoViewModel: ImageDemoViewModel,//isPrivate for creating new note in private files
             ) {

    val fireStore = FirebaseFirestore.getInstance()


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
        if (!newNote && noteId != "-1") {
            // Load existing note

            val loadedNote = noteViewModel.getNoteById(noteId) //awaits

            isInitialized.value=false

            currentNote.value=loadedNote
            cardTitle.value = loadedNote.title?:""
            isCardLocked.value = loadedNote.Private

            noteBlockEntityList.addAll(noteBlockViewModel.getBlocksByNoteId(noteId).map { convertToNoteBlockEntityModel(it) }) //awaits
            if(noteBlockEntityList.isEmpty()){
                noteBlockEntityList.add(NoteBlockEntityModel.TextBlock(description = mutableStateOf("")))
            }

            isInitialized.value=true
        } else {
            // Create new note

            val newNoteModel = when {
                isPrivate -> NoteModel(createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis(), Private = true)
                folderId == "-1" -> NoteModel(createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis())
                else -> NoteModel(createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis(), folderId = folderId)
            }

            isInitialized.value=false

            val createdNote = noteViewModel.insertNote(newNoteModel) //awaits

            currentNote.value=createdNote
            cardTitle.value = createdNote.title?:""
            isCardLocked.value = createdNote.Private

            // Add one empty TextBlock
            noteBlockEntityList.add(NoteBlockEntityModel.TextBlock(description = mutableStateOf("")))

            isChangeMade.value=true
            // Focus keyboard
            focusRequester.requestFocus()
            keyboardController?.show()

            isInitialized.value=true
        }
    }



    val context = LocalContext.current


    var expandedListForChecklist by remember { mutableStateOf(false) }
    var expandedListForVoice by remember { mutableStateOf(false) }

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

    fun updateFolder(folderId: String){
        coroutineScope.launch {

            val updatedNote = currentNote.value!!.copy(
                folderId = folderId
            )

            noteViewModel.updateNote(updatedNote) // suspend call awaits
            //Toast.makeText(context, "Moved To Folder", Toast.LENGTH_SHORT).show()
            currentNote.value = updatedNote
            showDialogForAddFolder.value = false
            Toast.makeText(context, "Moved To Folder", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context, "Note Saved", Toast.LENGTH_SHORT).show()




                //===============================syncing notes to cloud============================
                if(updatedNote.SavedInCloud){
                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    if (uid == null) {
                        delay(200L)
                        navController.popBackStack()
                        return@launch
                    }
                    try {
                        if (!isNetworkAvailable(context)) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Note not saved in cloud", Toast.LENGTH_SHORT).show()
                            }

                            //--------update note--------------//
                            val updatedNote = note.copy(
                                updatedAt = System.currentTimeMillis(),
                                SavedInCloud = true,
                                Synced = false
                            )
                            noteViewModel.updateNote(updatedNote)
                            delay(200L)
                            withContext(Dispatchers.Main) {
                                navController.popBackStack()
                            }
                            return@launch
                        }

                        val notesRef = fireStore.collection("users").document(uid).collection("notes")


                        val noteMap = mapOf(
                            "title" to note.title,
                            "createdAt" to note.createdAt,
                            "updatedAt" to note.updatedAt,
                            "folderId" to note.folderId,
                            "Private" to note.Private,
                            "SavedInCloud" to true,
                            "Synced" to true
                        )
                        //store the note
                        val noteDoc = notesRef.document(note.id)
                        noteDoc.set(noteMap).await()

                        //deleting the previous blocks
                        val blocksRef = noteDoc.collection("blocks")
                        val existingBlocks = blocksRef.get().await()
                        for (doc in existingBlocks.documents) {
                            doc.reference.delete().await()
                        }

                        val blocks = noteBlockViewModel.getBlocksByNoteId(note.id) // suspend
                        for (block in blocks) {
                            val blockMap = mapOf(
                                "noteId" to block.noteId,
                                "type" to block.type,
                                "description" to block.description,
                                "checklistItems" to block.checklistItems,
                                "numberedItems" to block.numberedItems,
                                "bulletedItems" to block.bulletedItems,
                                "blockOrder" to block.blockOrder
                            )
                            //store the block
                            blocksRef.document(block.id).set(blockMap).await()
                        }


                        val updatedNote = note.copy(
                            updatedAt = System.currentTimeMillis(),
                            SavedInCloud = true,
                            Synced = true
                        )
                        noteViewModel.updateNote(updatedNote)


                    } catch (e: Exception) {
                        //  Error toast
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error syncing notes: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                        val updatedNote = note.copy(
                            updatedAt = System.currentTimeMillis(),
                            SavedInCloud = true,
                            Synced = false
                        )
                        noteViewModel.updateNote(updatedNote)
                    }
                }

                delay(200L)
                isChangeMade.value = false
                withContext(Dispatchers.Main) {
                    navController.popBackStack()
                }

            }
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
                            showDialogWhenNotSaved.value=false
                            saveNote()
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



//================================================dialog for voice note=================================================//


    val voiceNoteUri = remember{mutableStateOf<Uri?>(null)}
    val isShowDialogForVoiceNote = remember{ mutableStateOf(false) }
    val audioRecorderManager = remember { AudioRecorderManager(context) }
    val amplitude = audioRecorderManager.amplitudeFlow.collectAsState()
    val isStartRecording = remember{mutableStateOf(false)}
    val isPlayRecording = remember{mutableStateOf(false)}
    val showDialogForDiscardRecording = remember { mutableStateOf(false) }
    val isPermissionGrantedForRecording = remember { mutableStateOf(false) }

    val waveformBars = remember { mutableStateListOf<Float>() }
    val tempBarList = remember { mutableStateListOf<Float>() }

    LaunchedEffect(amplitude.value) {
        waveformBars.add(amplitude.value)
        tempBarList.add(amplitude.value)
        if(tempBarList.size>35){
            tempBarList.removeAt(0)
        }
    }

    val timerSec = remember { mutableLongStateOf(0) }

    LaunchedEffect(isPlayRecording.value,isPermissionGrantedForRecording.value) {
        if (isPlayRecording.value&&isPermissionGrantedForRecording.value) {
            while (isPlayRecording.value) {
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
                isPermissionGrantedForRecording.value=true
                isPlayRecording.value = true
            } else {
                Toast.makeText(context, "Microphone permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(isStartRecording.value, isPlayRecording.value) {
        if (isStartRecording.value && isPlayRecording.value) {
            //check for permission
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ){
                isPermissionGrantedForRecording.value=true
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

    if (showDialogForDiscardRecording.value) {
        Dialog(onDismissRequest = {
            showDialogForDiscardRecording.value = false
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
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50.dp)),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFCB070D)
                        ),
                        onClick = {
                            isStartRecording.value=false
                            isPlayRecording.value=false
                            timerSec.longValue=0
                            waveformBars.clear()
                            tempBarList.clear()
                            showDialogForDiscardRecording.value = false
                            isShowDialogForVoiceNote.value=false
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
                        showDialogForDiscardRecording.value = false },
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

    val showDialogForResetRecording = remember { mutableStateOf(false) }

    if (showDialogForResetRecording.value) {
        Dialog(onDismissRequest = {
            showDialogForResetRecording.value = false
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
                    Text(text = "RESET RECORDING", style = TextStyle(
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
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50.dp)),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFCB070D)
                        ),
                        onClick = {
                            isStartRecording.value=false
                            isPlayRecording.value=false
                            timerSec.longValue=0
                            waveformBars.clear()
                            tempBarList.clear()
                            showDialogForResetRecording.value = false
                        }) {
                        Text(modifier = Modifier.padding(vertical = 5.dp), text = "RESET", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(onClick = {
                        showDialogForResetRecording.value = false },
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

    if(isShowDialogForVoiceNote.value){
        Dialog(
            onDismissRequest = {
                if(timerSec.longValue>0){
                    showDialogForDiscardRecording.value=true
                }
                else{
                    isShowDialogForVoiceNote.value=false
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(0.95f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFA131313))
                    .padding(5.dp),
                contentAlignment = Alignment.TopStart
            ){
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.1f),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        IconButton(onClick = {
                            if(timerSec.longValue>0){
                                showDialogForDiscardRecording.value=true
                            }
                            else{
                                isShowDialogForVoiceNote.value=false
                            }
                        }) {
                            Icon(
                                imageVector = TablerIcons.X,
                                contentDescription = "back",
                                tint = Color.White,
                                modifier = Modifier.size(23.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "VoiceNote",
                            style = TextStyle(fontFamily = NDot, fontSize = 30.sp, color = Color.White, fontWeight = FontWeight.Normal)

                        )

                    }
                    Spacer( modifier = Modifier.height(80.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.9f),
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
                                                    .fillMaxHeight(
                                                        heightFactor.coerceIn(
                                                            0.1f,
                                                            0.6f
                                                        )
                                                    )
                                                    .background(
                                                        if (isPlayRecording.value) Color.Red else Color.White,
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

                            if(!isStartRecording.value){
                                Button(onClick = {
                                    isStartRecording.value=true
                                    isPlayRecording.value=true
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
                            } else if(isStartRecording.value&&isPlayRecording.value){
                                IconButton(
                                    onClick = {
                                        isPlayRecording.value=false
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
                            } else if(isStartRecording.value&&!isPlayRecording.value){
                                IconButton(
                                    onClick = {
                                        showDialogForResetRecording.value=true
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
                                IconButton(onClick = {isPlayRecording.value=true},
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
                                Text(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(30.dp)).background(Color(0xFFCB070D))
                                        .clickable(
                                            onClick = {
                                                voiceNoteUri.value= audioRecorderManager.getOutputFilePath().toString().toUri()
                                                val last = noteBlockEntityList.lastOrNull()
                                                if (last is NoteBlockEntityModel.TextBlock && last.description.value.trim().isBlank()) {
                                                    noteBlockEntityList.removeAt(noteBlockEntityList.size - 1)
                                                }
                                                val tempVoiceNote = mutableStateOf(voiceNoteUri.value)
                                                noteBlockEntityList.add(NoteBlockEntityModel.VoiceBlock(uri = tempVoiceNote))
                                                noteBlockEntityList.add(NoteBlockEntityModel.TextBlock(description = mutableStateOf("")))
                                                isChangeMade.value=true

                                                voiceNoteUri.value=null
                                                isStartRecording.value=false
                                                isPlayRecording.value=false
                                                timerSec.longValue=0
                                                waveformBars.clear()
                                                tempBarList.clear()
                                                isShowDialogForVoiceNote.value=false
                                            }
                                        )
                                        .padding(vertical = 18.dp, horizontal = 23.dp),
                                    text = "SAVE",
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
                }
            }
        }
    }

    //=====================================speech to text============================
    val speechToText = remember{mutableStateOf("")}
    val isStartSpeechToText = remember{mutableStateOf(false)}
    val isListeningSpeechToText = remember{mutableStateOf(false)}
    val isShowDialogForSpeechToText = remember{mutableStateOf(false)}

    fun extractTextFromJson(json: String): String {
        val regex = """"text"\s*:\s*"([^"]*)"""".toRegex()
        val match = regex.find(json)
        return match?.groupValues?.get(1) ?: ""
    }


    // instance for the model_en_us
    val speechToTextManager = remember {
        SpeechToTextManager(context) { it
            if (it.isNotBlank()) {
                val cleanText = extractTextFromJson(it)
                speechToText.value += if(speechToText.value.isEmpty()) cleanText else " $cleanText"
            }
        }
    }

    val scrollStateForSpeechToText = rememberScrollState()

    LaunchedEffect(speechToText.value) {
        scrollStateForSpeechToText.animateScrollTo(scrollStateForSpeechToText.maxValue)
    }


    val audioPermissionLauncherForSpeechToText = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                // Only start recording if permission granted
                speechToTextManager.loadModel{isShowDialogForSpeechToText.value=true}

            } else {
                Toast.makeText(context, "Microphone permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    fun transcribe(){
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ){
            speechToTextManager.loadModel{isShowDialogForSpeechToText.value=true}
        }
        else{
            // Request permission
            audioPermissionLauncherForSpeechToText.launch(Manifest.permission.RECORD_AUDIO)
        }
    }



    val micColor = remember { mutableStateOf("WHITE") }

    LaunchedEffect(isListeningSpeechToText.value) {
        while(isListeningSpeechToText.value){
            micColor.value="RED"
            delay(500L)
            micColor.value="WHITE"
            delay(500L)
        }
        micColor.value = "WHITE"
    }

    val showDialogForDiscardSpeechToText = remember { mutableStateOf(false) }
    if (showDialogForDiscardSpeechToText.value) {
        Dialog(onDismissRequest = {
            showDialogForDiscardSpeechToText.value = false
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
                    Text(text = "DISCARD TEXT TO SPEECH", style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 18.sp,
                        color = Color(0xFFDEDEDE),
                        textAlign = TextAlign.Center
                    ))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(textAlign = TextAlign.Center,
                        text = "This text isn't saved", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color(0xFFB6B6B6)
                        ))
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50.dp)),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFCB070D)
                        ),
                        onClick = {
                            speechToTextManager.stopListening()
                            isListeningSpeechToText.value=false
                            speechToText.value=""
                            isStartSpeechToText.value=false
                            isListeningSpeechToText.value=false
                            micColor.value="WHITE"
                            showDialogForDiscardSpeechToText.value = false
                            isShowDialogForSpeechToText.value=false
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
                        showDialogForDiscardSpeechToText.value = false },
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

    //this is shown only if model is initialized
    if(isShowDialogForSpeechToText.value){
        Dialog(
            onDismissRequest = {
                if(isListeningSpeechToText.value||speechToText.value.isNotEmpty()){
                    showDialogForDiscardSpeechToText.value=true
                }
                else{
                    speechToTextManager.stopListening()
                    speechToText.value=""
                    isStartSpeechToText.value=false
                    micColor.value="WHITE"
                    isShowDialogForSpeechToText.value=false
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(0.95f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFA131313))
                    .padding(5.dp),
                contentAlignment = Alignment.TopStart
            ){
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.1f),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        IconButton(onClick = {
                            if(isListeningSpeechToText.value||speechToText.value.isNotEmpty()){
                                showDialogForDiscardSpeechToText.value=true
                            }
                            else{
                                speechToTextManager.stopListening()
                                speechToText.value=""
                                isStartSpeechToText.value=false
                                micColor.value="WHITE"
                                isShowDialogForSpeechToText.value=false
                            }
                        }) {
                            Icon(
                                imageVector = TablerIcons.X,
                                contentDescription = "back",
                                tint = Color.White,
                                modifier = Modifier.size(23.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Speech To Text",
                            style = TextStyle(fontFamily = NDot, fontSize = 28.sp, color = Color.White, fontWeight = FontWeight.Normal)

                        )

                    }
                    Spacer( modifier = Modifier.height(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.9f),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        // Text
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.7f)
                                .shadow(8.dp, RoundedCornerShape(20.dp), clip = false)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF1F1F1F))
                                .verticalScroll(scrollStateForSpeechToText)
                                .padding(20.dp)
                        ) {
                            Text(
                                text = if (speechToText.value.isEmpty()) "..." else speechToText.value,
                                style = TextStyle(
                                    fontFamily = NRegular,
                                    fontSize = 16.sp,
                                    color = Color(0xCBFFFFFF),
                                    fontWeight = FontWeight.W100
                                )
                            )
                        }


                        //Buttons
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.2f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween){

                            //=======================dummy for space filling================
                            Text(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(30.dp)).background(Color.Transparent)
                                    .padding(vertical = 15.dp, horizontal = 20.dp),
                                text = "SAVE",
                                style = TextStyle(
                                    fontFamily = NRegular,
                                    fontSize = 13.sp,
                                    color = Color.Transparent,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            //================================================================

                            IconButton(
                                onClick = {
                                    if(!isStartSpeechToText.value) {
                                        isStartSpeechToText.value = true
                                    }


                                    if(isListeningSpeechToText.value){
                                        speechToTextManager.stopListening()
                                        isListeningSpeechToText.value=false
                                    }
                                    else{
                                        speechToTextManager.startListening()
                                        isListeningSpeechToText.value=true
                                    }
                                },
                            ){
                                Icon(
                                    imageVector = if(micColor.value=="WHITE")Icons.Rounded.MicNone else Icons.Rounded.Mic,
                                    contentDescription = "Mic",
                                    tint = if(micColor.value=="WHITE") Color.White else Color.Red,
                                    modifier = Modifier.size(65.dp)
                                )
                            }

                            Text(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(30.dp)).background(if(isListeningSpeechToText.value||!isStartSpeechToText.value)Color.Transparent else Color(0xFFCB070D) )
                                    .clickable(
                                        onClick = {
                                            if(!isListeningSpeechToText.value&&isStartSpeechToText.value){
                                                //to save to text widget
                                                val last = noteBlockEntityList.lastOrNull()
                                                if (last is NoteBlockEntityModel.TextBlock && last.description.value.trim().isBlank()) {
                                                    noteBlockEntityList.removeAt(noteBlockEntityList.size - 1)
                                                }
                                                val temp = speechToText.value.toString()
                                                noteBlockEntityList.add(NoteBlockEntityModel.TextBlock(description = mutableStateOf(temp)))
                                                isChangeMade.value=true
                                                speechToText.value=""
                                                isStartSpeechToText.value=false
                                                isListeningSpeechToText.value=false
                                                micColor.value="WHITE"
                                                isShowDialogForSpeechToText.value=false
                                            }
                                        }
                                    ).padding(vertical = 15.dp, horizontal = 20.dp),
                                text = "SAVE",
                                style = TextStyle(
                                    fontFamily = NRegular,
                                    fontSize = 15.sp,
                                    color = if(isListeningSpeechToText.value||!isStartSpeechToText.value)Color.Transparent else Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                        }
                        Text(
                            modifier = Modifier.weight(0.1f)
                                .fillMaxWidth(),
                            text = if(isListeningSpeechToText.value)"Listening..." else " ",
                            style = TextStyle(
                                fontFamily = NRegular, // Ensure NRegular is defined
                                fontSize = 16.sp,
                                color = Color(0xCB9B9B9B),
                                fontWeight = FontWeight.W100
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }


    val listState = rememberLazyListState()

    LaunchedEffect(noteBlockEntityList.size) {
        if (noteBlockEntityList.isNotEmpty()) {
            listState.animateScrollToItem(noteBlockEntityList.lastIndex)
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
                                        if (isChangeMade.value) {
                                            showDialogWhenNotSaved.value = true
                                        } else {
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
                                                Toast.makeText(context, "Removed From Folder", Toast.LENGTH_SHORT).show()
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
                                                    Private = !currentNote.value!!.Private,
                                                )
                                                isCardLocked.value=updatedNote.Private
                                                currentNote.value = updatedNote
                                                noteViewModel.updateNote(updatedNote) //awaits
                                                if(isCardLocked.value){
                                                    Toast.makeText(context, "Note Moved To Private Files", Toast.LENGTH_SHORT).show()
                                                }
                                                else{
                                                    Toast.makeText(context, "Note Moved To Public Files", Toast.LENGTH_SHORT).show()
                                                }
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
                                        onClick = {expandedListForChecklist=true},
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
                                        expanded = expandedListForChecklist,
                                        onDismissRequest = {expandedListForChecklist=false},
                                        offset = DpOffset(x=(-20).dp,y= (0).dp),
                                        modifier = Modifier
                                            .background(Color(0xFA131313))
                                    ) {
                                        DropdownMenuItem(
                                            onClick = {
                                                expandedListForChecklist=false
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
                                                expandedListForChecklist=false
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
                                                expandedListForChecklist=false
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
                                Box {
                                    IconButton(
                                        onClick = {expandedListForVoice=true},
                                        modifier = Modifier.padding(horizontal = 10.dp)
                                    ) {
                                        Icon(
                                            imageVector = TablerIcons.Microphone,
                                            contentDescription = "Voice",
                                            tint = Color.White,
                                            modifier = Modifier.size(23.dp)
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = expandedListForVoice,
                                        onDismissRequest = {expandedListForVoice=false},
                                        offset = DpOffset(x=(-20).dp,y= (0).dp),
                                        modifier = Modifier
                                            .background(Color(0xFA131313))
                                    ) {
                                        DropdownMenuItem(
                                            onClick = {
                                                expandedListForVoice=false
                                                isShowDialogForVoiceNote.value=true;
                                            },
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically){
                                                    Icon(imageVector = TablerIcons.Microphone,
                                                        contentDescription = "Microphone",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(23.dp))
                                                    Text(text = "Voice Note", modifier = Modifier.padding(start = 15.dp))
                                                }
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        DropdownMenuItem(
                                            onClick = {
                                                expandedListForVoice=false
                                                transcribe()
                                            },
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically){
                                                    Image(
                                                        painter = painterResource(id= R.drawable.speech_to_text),
                                                        contentDescription = "Transcription",
                                                        modifier = Modifier
                                                            .requiredSize(23.dp)
                                                            .padding(start = 1.dp),
                                                        colorFilter = ColorFilter.tint(Color.White),
                                                        contentScale = ContentScale.Fit
                                                    )
                                                    Text(text = "Speech to Text", modifier = Modifier.padding(start = 15.dp))
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Box{
                            Row{
                                Text(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(30.dp)).background(Color(0xFFCB070D))
                                        .clickable(
                                            onClick = {
                                                saveNote()
                                            }
                                        ).padding(vertical = 15.dp, horizontal = 20.dp),
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
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
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(noteBlockEntityList) { index, block ->
                        when (block) {
                            is NoteBlockEntityModel.TextBlock -> {
                                if (index > 0 && noteBlockEntityList[index - 1] is NoteBlockEntityModel.TextBlock && block.description.value.isEmpty()) {
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


                            is NoteBlockEntityModel.VoiceBlock -> {
                                VoiceWidget(
                                    voiceBlock = block,
                                    uri = block.uri.value!!,
                                    noteBlockEntityList = noteBlockEntityList,
                                    isChangeMade = isChangeMade
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

