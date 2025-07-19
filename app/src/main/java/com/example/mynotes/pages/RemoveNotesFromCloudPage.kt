package com.example.mynotes.pages

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.mynotes.R
import com.example.mynotes.data.viewmodel.NoteViewModel
import com.example.mynotes.model.NoteModel
import com.example.mynotes.ui.theme.NDot
import com.example.mynotes.ui.theme.NRegular
import com.example.mynotes.utils.isNetworkAvailable
import com.example.mynotes.widgets.LoadingWidget
import com.example.mynotes.widgets.RemoveNotesFromCloudWidget
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Composable
fun RemoveNotesFromCloudPage(navController: NavController, noteViewModel: NoteViewModel) {

    val context = LocalContext.current

    val viewSelect = remember{mutableStateOf("ALL")}
    val isSelectAll = remember{mutableStateOf(false)}
    val isSelectPrivateAll = remember{mutableStateOf(false)}
    val selectedNoteList = remember { mutableStateListOf<NoteModel>() }
    val selectedPrivateNoteList = remember { mutableStateListOf<NoteModel>() }
    val noteListInCloud = remember { mutableStateListOf<NoteModel>() }
    val privateNoteListInCloud = remember { mutableStateListOf<NoteModel>() }

    val isLoading = remember { mutableStateOf(true)  }
    val loadDone = remember{mutableStateOf(false)}

    val isRemoving = remember { mutableStateOf(false) }
    val removeDone = remember { mutableStateOf(false) }



    //===================== get the notes ==================================
    val fireStore = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    val noInternet = remember{mutableStateOf(false)}

    LaunchedEffect(Unit) {

        noteListInCloud.clear()
        privateNoteListInCloud.clear()
        if(uid!=null){
            if (!isNetworkAvailable(context)) {
                delay(500L)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                    isLoading.value=false
                    loadDone.value=false
                    noInternet.value=true
                }
                return@LaunchedEffect
            }
            val notesRef = fireStore.collection("users").document(uid).collection("notes")

            try {
                isLoading.value=true
                loadDone.value=false
                val snapshot = notesRef.get().await()

                for (doc in snapshot.documents) {
                    val note = NoteModel(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        createdAt = doc.getLong("createdAt") ?: 0L,
                        updatedAt = doc.getLong("updatedAt") ?: 0L,
                        folderId = doc.getString("folderId"),
                        Private = doc.getBoolean("Private") ?: false,
                        SavedInCloud = doc.getBoolean("SavedInCloud") ?: true,
                        Synced = doc.getBoolean("Synced") ?: false
                    )
                    if (note.Private) {
                        privateNoteListInCloud.add(note)
                    } else {
                        noteListInCloud.add(note)
                    }

                    delay(500L)
                    loadDone.value=true

                    delay(500L)
                    isLoading.value=false
                }

            } catch (e: Exception) {
                Toast.makeText(context, "Error syncing notes: ${e.message}", Toast.LENGTH_LONG).show()
                isLoading.value=false
                loadDone.value=false
            }
        }
    }

    //====================================remove the notes====================================

    fun removeNote(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            if (!isNetworkAvailable(context)) {
                delay(500L)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                    isRemoving.value=false
                    removeDone.value=false
                    noInternet.value=true
                }
                return@launch
            }
            try {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val notesRef = fireStore.collection("users").document(uid).collection("notes")

                val noteList = mutableListOf<NoteModel>().apply {
                    addAll(selectedNoteList)
                    addAll(selectedPrivateNoteList)
                }

                for (note in noteList) {
                    val noteDoc = notesRef.document(note.id)


                    val blocksSnapshot = noteDoc.collection("blocks").get().await()
                    for (blockDoc in blocksSnapshot.documents) {
                        blockDoc.reference.delete().await()
                    }

                    // Then delete the note document itself
                    noteDoc.delete().await()

                    val updatedNote = note.copy(
                        updatedAt = System.currentTimeMillis(),
                        SavedInCloud = false,
                        Synced = false
                    )
                    noteViewModel.updateNote(updatedNote) //await
                }

                //  Success toast
                withContext(Dispatchers.Main) {

                    delay(500L)
                    Toast.makeText(context, "Notes removed from cloud", Toast.LENGTH_SHORT).show()
                    removeDone.value=true
                    delay(500L)
                    isRemoving.value=false
                    navController.popBackStack()
                }

            } catch (e: Exception) {
                //  Error toast
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error syncing notes: ${e.message}", Toast.LENGTH_LONG).show()
                    isRemoving.value=false
                }
            }
        }
    }


    val showDialogForRemoveNotes = remember{mutableStateOf(false)}
    if(showDialogForRemoveNotes.value){
        Dialog(onDismissRequest = {
            showDialogForRemoveNotes.value = false
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
                    Text(text = "Are You Sure To Remove Selected Notes", style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 18.sp,
                        color = Color(0xFFDEDEDE),
                        textAlign = TextAlign.Center
                    ))
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(textAlign = TextAlign.Start,
                        text = "Note: Selected notes saved in cloud will be deleted. But synced notes will be available in your" +
                                " local storage", style = TextStyle(
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
                            showDialogForRemoveNotes.value=false
                            removeDone.value=false
                            isRemoving.value=true
                            //====================remove from cloud ==============================
                            removeNote(context = context)

                        }) {
                        Text(modifier = Modifier.padding(vertical = 5.dp), text = "REMOVE", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = {
                            showDialogForRemoveNotes.value = false
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
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                    ) {
                        Icon(
                            imageVector = TablerIcons.ArrowLeft,
                            contentDescription = "back",
                            tint = Color.White,
                            modifier = Modifier.size(25.dp)
                                .clickable(onClick = { navController.popBackStack() })
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "Remove Notes",
                            style = TextStyle(
                                fontFamily = NDot,
                                fontSize = 25.sp,
                                color = Color.White,
                            ),
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if(!isLoading.value&&!isRemoving.value&&!noInternet.value){
                FloatingActionButton(
                    modifier = Modifier.padding(20.dp),
                    onClick = {
                        if(selectedNoteList.isEmpty()&&selectedPrivateNoteList.isEmpty()){
                            Toast.makeText(context, "Select Files", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            showDialogForRemoveNotes.value=true
                        }
                    },
                    containerColor = Color(0xFFCB070D),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(35.dp),
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                        text = "REMOVE",
                        style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    ){ innerPadding ->
        if(noInternet.value){
            if(noInternet.value){
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center) {
                        Image(
                            painter = painterResource(id= R.drawable.no_internet),
                            contentDescription = "add",
                            modifier = Modifier
                                .requiredSize(50.dp)
                                .padding(start = 1.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(60.dp))
                        Text( text = "No Internet Connection!", style = TextStyle(fontFamily = NRegular, fontSize = 18.sp,
                            color = Color(0xFFC4C4C4),))
                    }
                }
            }
        }
        else if(isRemoving.value){
            LoadingWidget(loadDone = removeDone, msg = "REMOVING")
        }
        else if(isLoading.value){
            LoadingWidget(loadDone = loadDone, msg = "LOADING")
        }
        else{
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                item{
                    Row(modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically){
                        //Spacer(modifier = Modifier.width( ( (screenWidth / 2)-50.dp))) // Adjustable

                        Box(modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp).clickable(onClick = {
                            viewSelect.value="ALL"
                        })) {
                            Text( text = "ALL", style = TextStyle(fontFamily = NRegular, fontSize = 15.sp,
                                color = if(viewSelect.value=="ALL")Color(0xFFE00A0A)else Color(0xFFC4C4C4),
                                fontWeight = FontWeight.Bold))
                        }
                        Box(modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp).clickable(onClick = {
                            viewSelect.value="PRIVATE"
                        })) {
                            Text( text = "PRIVATE", style = TextStyle(fontFamily = NRegular, fontSize = 15.sp,
                                color = if(viewSelect.value=="PRIVATE")Color(0xFFE00A0A)else Color(0xFFC4C4C4),
                                fontWeight = FontWeight.Bold))
                        }

                        //Spacer(modifier = Modifier.width( ( (screenWidth / 2)-50.dp))) // Adjustable
                    }
                }

                item{
                    Spacer(modifier = Modifier.height(30.dp))
                    RemoveNotesFromCloudWidget(noteViewModel = noteViewModel, viewSelect = viewSelect, allNotesInCloud = noteListInCloud,
                        privateNotesInCloud = privateNoteListInCloud, selectedNoteList = selectedNoteList, selectedPrivateNoteList = selectedPrivateNoteList,
                        isSelectAll = isSelectAll, isSelectPrivateAll = isSelectPrivateAll)
                }
            }
        }
    }
}