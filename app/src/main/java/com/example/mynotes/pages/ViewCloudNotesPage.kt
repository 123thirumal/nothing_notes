package com.example.mynotes.pages

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mynotes.R
import com.example.mynotes.data.viewmodel.NoteViewModel
import com.example.mynotes.model.NoteModel
import com.example.mynotes.ui.theme.NDot
import com.example.mynotes.ui.theme.NRegular
import com.example.mynotes.utils.isNetworkAvailable
import com.example.mynotes.widgets.LoadingWidget
import com.example.mynotes.widgets.ViewCloudNotesWidget
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Composable
fun ViewCloudNotesPage(navController: NavController, noteViewModel: NoteViewModel) {

    val context = LocalContext.current

    val noteListInCloud = remember { mutableStateListOf<NoteModel>() }
    val privateNoteListInCloud = remember { mutableStateListOf<NoteModel>() }

    val viewSelect = remember{mutableStateOf("ALL")}

    val isLoading = remember { mutableStateOf(true)  }
    val loadDone = remember{mutableStateOf(false)}


    val noInternet = remember{mutableStateOf(false)}
    //===================== get the notes ==================================
    val fireStore = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

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

                    delay(200L)
                    loadDone.value=true

                    delay(200L)
                    isLoading.value=false
                }

            } catch (e: Exception) {
                Toast.makeText(context, "Error syncing notes: ${e.message}", Toast.LENGTH_LONG).show()
                isLoading.value=false
                loadDone.value=false
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
                            text = "Saved Notes In Cloud",
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
                    ViewCloudNotesWidget(noteViewModel = noteViewModel, viewSelect = viewSelect, noteListInCloud = noteListInCloud,
                        privateNoteListInCloud = privateNoteListInCloud)
                }
            }
        }
    }
}