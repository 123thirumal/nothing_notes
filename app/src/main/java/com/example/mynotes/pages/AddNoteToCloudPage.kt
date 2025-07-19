package com.example.mynotes.pages

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mynotes.R
import com.example.mynotes.data.viewmodel.NoteBlockViewModel
import com.example.mynotes.data.viewmodel.NoteViewModel
import com.example.mynotes.model.NoteModel
import com.example.mynotes.ui.theme.NDot
import com.example.mynotes.ui.theme.NRegular
import com.example.mynotes.utils.isNetworkAvailable
import com.example.mynotes.widgets.AddNoteToCloudNoteWidget
import com.example.mynotes.widgets.LoadingWidget
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
fun AddNoteToCloudPage(navController: NavController, noteViewModel: NoteViewModel, noteBlockViewModel: NoteBlockViewModel) {

    val context = LocalContext.current

    val isSyncing = remember{mutableStateOf(false)}
    val syncDone = remember{mutableStateOf(false)}




    val syncSelect = remember{mutableStateOf("ALL")}
    val isSelectAll = remember{mutableStateOf(false)}
    val isSelectPrivateAll = remember{mutableStateOf(false)}
    val selectedNoteList = remember { mutableStateListOf<NoteModel>() }
    val selectedPrivateNoteList = remember { mutableStateListOf<NoteModel>() }

    val noInternet = remember{mutableStateOf(false)}



    //===================== sync to cloud ==================================
    val fireStore = FirebaseFirestore.getInstance()

    fun uploadNote(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (!isNetworkAvailable(context)) {
                    delay(500L)
                    isSyncing.value = false
                    noInternet.value=true
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
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

                val notesRef = fireStore.collection("users").document(uid).collection("notes")

                val noteList = mutableListOf<NoteModel>().apply {
                    addAll(selectedNoteList)
                    addAll(selectedPrivateNoteList)
                }

                for (note in noteList) {
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

                    val blocks = noteBlockViewModel.getBlocksByNoteId(note.id) // suspend
                    val blocksRef = noteDoc.collection("blocks")

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

                //  Success toast
                withContext(Dispatchers.Main) {
                    delay(500L)
                    Toast.makeText(context, "Notes synced to cloud", Toast.LENGTH_SHORT).show()
                    syncDone.value=true
                    delay(500L)
                    isSyncing.value=false
                    navController.popBackStack()
                }

            } catch (e: Exception) {
                //  Error toast
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error syncing notes: ${e.message}", Toast.LENGTH_LONG).show()
                    isSyncing.value=false
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
                            text = "Sync Notes",
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
            if(!isSyncing.value&&!noInternet.value){
                FloatingActionButton(
                    modifier = Modifier.padding(20.dp),
                    onClick = {
                        if(selectedNoteList.isEmpty()&&selectedPrivateNoteList.isEmpty()){
                            Toast.makeText(context, "Select Files", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            syncDone.value=false
                            isSyncing.value=true
                            //====================sync to cloud ==============================
                            uploadNote(context = context)

                        }
                    },
                    containerColor = Color(0xFFCB070D),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(35.dp),
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                        text = "UPLOAD",
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
        else if(isSyncing.value){
            Box(modifier = Modifier.padding(innerPadding)) {
                LoadingWidget(loadDone = syncDone, msg = "SAVING")
            }
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
                            syncSelect.value="ALL"
                        })) {
                            Text( text = "ALL", style = TextStyle(fontFamily = NRegular, fontSize = 15.sp,
                                color = if(syncSelect.value=="ALL")Color(0xFFE00A0A)else Color(0xFFC4C4C4),
                                fontWeight = FontWeight.Bold))
                        }
                        Box(modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp).clickable(onClick = {
                            syncSelect.value="PRIVATE"
                        })) {
                            Text( text = "PRIVATE", style = TextStyle(fontFamily = NRegular, fontSize = 15.sp,
                                color = if(syncSelect.value=="PRIVATE")Color(0xFFE00A0A)else Color(0xFFC4C4C4),
                                fontWeight = FontWeight.Bold))
                        }

                        //Spacer(modifier = Modifier.width( ( (screenWidth / 2)-50.dp))) // Adjustable
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically){
                        Text( text = "Select All Files", style = TextStyle(fontFamily = NRegular, fontSize = 15.sp,
                            color = Color(0xFFC4C4C4)))

                        if(syncSelect.value=="ALL"){
                            SliderToggleSwitch(toggleState = isSelectAll)
                        }
                        else if(syncSelect.value=="PRIVATE"){
                            SliderToggleSwitch(toggleState = isSelectPrivateAll)
                        }
                    }
                }

                item{
                    Spacer(modifier = Modifier.height(30.dp))
                    AddNoteToCloudNoteWidget(noteViewModel = noteViewModel, syncSelect = syncSelect, selectedNoteList = selectedNoteList,
                        isSelectAll = isSelectAll, selectedPrivateNoteList = selectedPrivateNoteList,
                        isSelectPrivateAll = isSelectPrivateAll)
                }
            }
        }
    }
}















@Composable
fun SliderToggleSwitch(
    toggleState: MutableState<Boolean>,
) {

    val alignment by animateDpAsState(
        targetValue = if (toggleState.value) 24.dp else 2.dp,
        animationSpec = tween(durationMillis = 250),
        label = "alignment"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (toggleState.value) Color(0xFFE00A0A) else Color(0xFFB0BEC5),
        animationSpec = tween(durationMillis = 250),
        label = "backgroundColor"
    )

    Box(
        modifier = Modifier
            .width(50.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .clickable {
                toggleState.value = !toggleState.value
            }
            .padding(horizontal = 2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .offset(x = alignment)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}









