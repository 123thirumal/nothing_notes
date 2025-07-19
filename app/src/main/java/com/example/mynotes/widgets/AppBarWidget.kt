package com.example.mynotes.widgets

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextButton
import androidx.compose.material3.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.ui.theme.NDot
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.gif.GifDecoder
import coil3.request.ImageRequest
import com.example.mynotes.R
import com.example.mynotes.data.viewmodel.NoteBlockViewModel
import com.example.mynotes.data.viewmodel.NoteViewModel
import com.example.mynotes.model.FolderModel
import com.example.mynotes.model.NoteBlockModel
import com.example.mynotes.model.NoteModel
import com.example.mynotes.ui.theme.NRegular
import com.example.mynotes.utils.isNetworkAvailable
import compose.icons.TablerIcons
import compose.icons.tablericons.BrandGoogle
import compose.icons.tablericons.BrandGoogleAnalytics
import compose.icons.tablericons.CircleCheck
import compose.icons.tablericons.CloudUpload
import compose.icons.tablericons.DotsVertical
import compose.icons.tablericons.Lock
import compose.icons.tablericons.LockOff
import compose.icons.tablericons.Repeat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import compose.icons.tablericons.User
import compose.icons.tablericons.UserExclamation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext



@Composable
fun AppBarWidget(searchSelect: MutableState<Boolean>, searchToggle: ()->Unit, headSelect: MutableState<String>,
                 isUnlocked: MutableState<Boolean>, showDialogForChangePassword: MutableState<Boolean>,
                 navController: NavController, isSelectedFiles: MutableState<Boolean>, isSelectedFolders: MutableState<Boolean>,
                 selectedNoteList: SnapshotStateList<NoteModel>, selectedFolderList: SnapshotStateList<FolderModel>,
                 isSelectedFilesInPrivate: MutableState<Boolean>, selectedFilesInPrivate: SnapshotStateList<NoteModel>,
                 currentUser: FirebaseUser?,noteViewModel: NoteViewModel, noteBlockViewModel: NoteBlockViewModel,){
    var inputStr by remember{mutableStateOf("")}
    val focusRequester = remember { FocusRequester() }

    val isExpandedSettings = remember{mutableStateOf(false)}


    LaunchedEffect(searchSelect.value) {
        if (searchSelect.value) {
            focusRequester.requestFocus()
        }
    }


//==============================dialog for syncing notes==============================
    val isSyncDone = remember{mutableStateOf(false)}
    val showDialogForSyncingNotes = remember{mutableStateOf(false)}
    val isSyncing = remember{mutableStateOf(false)}
    val fireStore = remember{mutableStateOf(FirebaseFirestore.getInstance())}

    val context = LocalContext.current

    if(showDialogForSyncingNotes.value){
        Dialog(onDismissRequest = {
            showDialogForSyncingNotes.value = false
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
                    Text(textAlign = TextAlign.Center,
                        text = "Sign-In To Google To Sync Notes", style = TextStyle(
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
                            showDialogForSyncingNotes.value=false
                            navController.navigate("google_sign_in_page")
                        }) {
                        Text(modifier = Modifier.padding(vertical = 5.dp), text = "SIGN-IN", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = {
                            showDialogForSyncingNotes.value = false
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

    val nonPrivateNotesInCloud by noteViewModel.nonPrivateNotesInCloud.collectAsState(initial = emptyList())
    val privateNotesInCloud by noteViewModel.privateNotesInCloud.collectAsState(initial = emptyList())
    fun syncNote(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (!isNetworkAvailable(context)) {
                    delay(500L)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                        isSyncing.value = false
                        isSyncDone.value = false
                    }
                    return@launch
                }
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val notesRef = fireStore.value.collection("users").document(uid).collection("notes")


                //uploading the synced notes from local storage to cloud
                for (note in nonPrivateNotesInCloud) {
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
                }
                for (note in privateNotesInCloud) {
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
                }
                delay(200L)



                //downloading notes from cloud to local storage
                val notesSnapshot = notesRef.get().await()
                for (noteDoc in notesSnapshot.documents) {
                    val note = noteDoc.toObject(NoteModel::class.java)?.copy(
                        id = noteDoc.id,
                        SavedInCloud = true,
                        Synced = true,
                    ) ?: continue

                    // Insert note into Room
                    noteViewModel.insertNote(note)

                    // Fetch and insert blocks
                    val blocksRef = notesRef.document(note.id).collection("blocks")
                    val blocksSnapshot = blocksRef.get().await()

                    for (blockDoc in blocksSnapshot.documents) {
                        val block = blockDoc.toObject(NoteBlockModel::class.java)?.copy(
                            id = blockDoc.id,
                        ) ?: continue

                        //insert the block
                        noteBlockViewModel.insertNoteBlock(block)
                    }
                }

                //  Success toast
                withContext(Dispatchers.Main) {
                    delay(500L)
                    Toast.makeText(context, "Notes synced", Toast.LENGTH_SHORT).show()

                    isSyncDone.value=true
                    delay(500L)
                    isSyncing.value=false
                    isSyncDone.value=false
                }
            } catch (e: Exception) {
                //  Error toast
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error syncing notes: ${e.message}", Toast.LENGTH_LONG).show()
                    isSyncing.value=false
                    isSyncDone.value=false
                }
            }
        }
    }

    if(isSyncing.value){
        var hasShownToast by remember { mutableStateOf(false) }
        Dialog(onDismissRequest = {
            if (!hasShownToast) {
                Toast.makeText(context, "Syncing In Progress", Toast.LENGTH_SHORT).show()
                hasShownToast = true
            }
        }) {
            Box(
                modifier = Modifier
                    .width(300.dp) // You can control width here!
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFA131313))
                    .padding(50.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {
                    if(isSyncDone.value){
                        Image(
                            painter = painterResource(id= R.drawable.sync_done),
                            contentDescription = null,
                            modifier = Modifier.requiredSize(100.dp),
                            colorFilter = ColorFilter.tint(Color(0xFFB6B6B6))
                        )
                    }
                    else{
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(R.drawable.sync_progress)
                                .decoderFactory(GifDecoder.Factory())
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.requiredSize( 100.dp),
                            colorFilter = ColorFilter.tint(Color(0xFFB6B6B6))
                        )
                    }
                    Spacer(modifier = Modifier.height(50.dp))
                    Text(textAlign = TextAlign.Center,
                        text = if(isSyncDone.value)"DONE" else "SYNCING...", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 18.sp,
                            color = Color(0xFFB6B6B6)
                        )
                    )
                }
            }
        }
    }


    TopAppBar(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(top = 15.dp, bottom = 30.dp, start = 10.dp, end = 10.dp),
        backgroundColor = Color.Black,
        title = {
            if(!searchSelect.value) {
                Text(
                    text = "Notes",
                    style = TextStyle(fontFamily = NDot, fontSize = 33.sp, color = Color.White, fontWeight = FontWeight.Normal)
                )
            } else{
                OutlinedTextField(modifier = Modifier.focusRequester(focusRequester), value = inputStr, onValueChange = { inputStr= it }, maxLines = 1, singleLine = true,
                    textStyle = TextStyle(fontFamily = NRegular, fontSize = 18.sp, color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        errorBorderColor = Color.Transparent,
                        cursorColor = Color.White,
                    ),
                    keyboardActions = KeyboardActions(onAny = {searchToggle()})
                )
            }
        },
        actions = {
            IconButton(onClick = {
                if(FirebaseAuth.getInstance().currentUser==null){
                    showDialogForSyncingNotes.value=true
                }
                else{
                    isSyncing.value=true
                    isSyncDone.value=false
                    syncNote()
                }
            }) {
                Image(
                    painter = painterResource(id= R.drawable.cloudsync),
                    contentDescription = "sync",
                    modifier = Modifier
                        .requiredSize(30.dp)
                        .padding(start = 1.dp),
                    colorFilter = ColorFilter.tint(Color.White),
                    contentScale = ContentScale.Fit
                )
            }
            Box {
                IconButton(
                    onClick = { isExpandedSettings.value = true },
                ) {
                    Icon(
                        imageVector = TablerIcons.DotsVertical,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(23.dp)
                    )
                }
                DropdownMenu(
                    expanded = isExpandedSettings.value,
                    onDismissRequest = { isExpandedSettings.value = false },
                    offset = DpOffset(x = (-5).dp, y = (0).dp),
                    modifier = Modifier.background(Color(0xFA131313))
                ) {
                    DropdownMenuItem(
                        onClick = {
                            isExpandedSettings.value = false
                            if(headSelect.value=="ALL"){
                                if(isSelectedFiles.value){
                                    selectedNoteList.clear()
                                    isSelectedFiles.value=!isSelectedFiles.value
                                }
                                else{
                                    isSelectedFiles.value=!isSelectedFiles.value
                                }
                            }
                            else if(headSelect.value=="FOLDERS"){
                                if(isSelectedFolders.value){
                                    selectedFolderList.clear()
                                    isSelectedFolders.value=!isSelectedFolders.value
                                }
                                else{
                                    isSelectedFolders.value=!isSelectedFolders.value
                                }
                            }
                            else if(headSelect.value=="PRIVATE"&&isUnlocked.value){
                                if(isSelectedFilesInPrivate.value){
                                    selectedFilesInPrivate.clear()
                                    isSelectedFilesInPrivate.value=!isSelectedFilesInPrivate.value
                                }
                                else{
                                    isSelectedFilesInPrivate.value=!isSelectedFilesInPrivate.value
                                }
                            }
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    tint = Color.White,
                                    imageVector = TablerIcons.CircleCheck,
                                    contentDescription = "Select",
                                )
                                Text(
                                    text = if(isSelectedFiles.value||isSelectedFolders.value||isSelectedFilesInPrivate.value) "Done" else "Select",
                                    modifier = Modifier.padding(start = 15.dp)
                                )
                            }
                        }
                    )
                    if(!(isSelectedFiles.value||isSelectedFolders.value||isSelectedFilesInPrivate.value)){
                        Spacer(modifier = Modifier.height(10.dp))
                        if(currentUser==null){
                            DropdownMenuItem(
                                onClick = {
                                    isExpandedSettings.value = false
                                    navController.navigate("google_sign_in_page")
                                },
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            tint = Color.White,
                                            imageVector = TablerIcons.BrandGoogle,
                                            contentDescription = "Google"
                                        )
                                        Text(
                                            text = "Google",
                                            modifier = Modifier.padding(start = 15.dp)

                                        )

                                    }
                                }
                            )
                        }
                        else{
                            DropdownMenuItem(
                                onClick = {
                                    isExpandedSettings.value = false
                                    navController.navigate("profile_page")
                                },
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            tint = Color.White,
                                            imageVector = Icons.Default.AccountCircle,
                                            contentDescription = "Profile"
                                        )
                                        Text(
                                            text = "Profile",
                                            modifier = Modifier.padding(start = 15.dp)

                                        )

                                    }
                                }
                            )
                        }
                    }
                    if(headSelect.value=="PRIVATE"&&isUnlocked.value){
                        Spacer(modifier = Modifier.height(10.dp))
                        DropdownMenuItem(
                            onClick = {
                                isExpandedSettings.value = false
                                isUnlocked.value=false
                            },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        tint = Color.White,
                                        imageVector = TablerIcons.Lock,
                                        contentDescription = "Lock Private Files"
                                    )
                                    Text(
                                        text = "Lock Private Files",
                                        modifier = Modifier.padding(start = 15.dp)

                                    )

                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        DropdownMenuItem(
                            onClick = {
                                isExpandedSettings.value = false
                                showDialogForChangePassword.value=true
                            },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id= R.drawable.chng_passcode),
                                        contentDescription = "Change password",
                                        modifier = Modifier
                                            .requiredSize(25.dp)
                                            .padding(start = 1.dp),
                                        colorFilter = ColorFilter.tint(Color.White),
                                        contentScale = ContentScale.Fit
                                    )
                                    Text(
                                        text = "Change Password",
                                        modifier = Modifier.padding(start = 15.dp)
                                    )
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        DropdownMenuItem(
                            onClick = {
                                isExpandedSettings.value = false
                                navController.navigate("remove_private_files_page")
                            },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        tint = Color.White,
                                        imageVector = TablerIcons.LockOff,
                                        contentDescription = "Remove Lock"
                                    )
                                    Text(
                                        text = "Remove Lock",
                                        modifier = Modifier.padding(start = 15.dp)
                                    )
                                }
                            }
                        )
                    }
                }
            }

        }
    )
}
