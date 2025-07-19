package com.example.mynotes.pages

import android.app.Activity
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetCredentialRequest
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CustomCredential
import androidx.navigation.NavController
import com.example.mynotes.R
import com.example.mynotes.data.viewmodel.NoteBlockViewModel
import com.example.mynotes.data.viewmodel.NoteViewModel
import com.example.mynotes.model.NoteBlockModel
import com.example.mynotes.model.NoteModel
import com.example.mynotes.ui.theme.NDot
import com.example.mynotes.ui.theme.NRegular
import com.example.mynotes.utils.isNetworkAvailable
import com.example.mynotes.widgets.LoadingWidget
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import androidx.compose.runtime.getValue

@Composable
fun GoogleSignInPage(navController: NavController, setUser: () -> Unit,noteViewModel: NoteViewModel,
                     noteBlockViewModel: NoteBlockViewModel) {




    val context = LocalContext.current
    val activity = context as Activity

    val credentialManager = CredentialManager.create(context)
    val coroutineScope = rememberCoroutineScope()

    val startSync = remember{mutableStateOf(false)}
    val isSyncComplete = remember{mutableStateOf(false)}


    //===================== sync the notes and save it in local storage ==================================
    val fireStore = remember{mutableStateOf(FirebaseFirestore.getInstance())}

    val nonPrivateNotesInCloud by noteViewModel.nonPrivateNotesInCloud.collectAsState(initial = emptyList())
    val privateNotesInCloud by noteViewModel.privateNotesInCloud.collectAsState(initial = emptyList())
    LaunchedEffect(startSync.value) {
        if(startSync.value){
            try {
                if (!isNetworkAvailable(context)) {
                    delay(500L)
                    isSyncComplete.value=false
                    startSync.value=false
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                    }
                    return@LaunchedEffect
                }
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                    }
                    startSync.value=false
                    return@LaunchedEffect
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
                    isSyncComplete.value=true
                    delay(500L)
                    startSync.value=false
                }
            } catch (e: Exception) {
                //  Error toast
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error syncing notes: ${e.message}", Toast.LENGTH_LONG).show()
                    isSyncComplete.value=false
                    startSync.value=false
                }
            }
        }
    }



    fun handleSignIn(result: GetCredentialResponse) {
        val credential = result.credential

        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken

                        // Send this token to your backend or Firebase
                        FirebaseAuth.getInstance()
                            .signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Sign-in success: ${task.result?.user?.email}", Toast.LENGTH_SHORT).show()
                                    setUser()
                                    fireStore.value = FirebaseFirestore.getInstance()
                                    startSync.value=true
                                } else {
                                    Toast.makeText(context, "Sign-in failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } catch (e: GoogleIdTokenParsingException) {
                        Toast.makeText(context, "Sign-in failed:", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            else -> {
                Toast.makeText(context, "Sign-in failed:", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun launchSignIn() {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false) // Allows new users
            .setServerClientId(context.getString(R.string.server_client_id))
            .setAutoSelectEnabled(false)
            .setNonce(UUID.randomUUID().toString())
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = activity
                )
                // User selected an account successfully
                handleSignIn(result)

            } catch (e: GetCredentialException) {
                if (e.message?.contains("user canceled", ignoreCase = true) == true ||
                    e.toString().contains("user canceled", ignoreCase = true)
                ) {
                    // User cancelled the sign-in flow
                    Toast.makeText(context, "Sign-in cancelled", Toast.LENGTH_SHORT).show()
                } else {
                    // Some other failure
                    Toast.makeText(context, "Sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
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
                            text = "Google",
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
        if(startSync.value){
            LoadingWidget(loadDone = isSyncComplete, msg = "SYNCING")
        }
        else if(isSyncComplete.value){
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            )
            {
                Box(
                    modifier = Modifier.weight(1f)
                ){
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id= R.drawable.cloudsync),
                            contentDescription = "sync",
                            modifier = Modifier
                                .requiredSize(60.dp)
                                .padding(start = 1.dp),
                            colorFilter = ColorFilter.tint(Color.White),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(50.dp))
                        Text(text = "Sync Your Notes To Cloud",
                            style = TextStyle(
                                fontFamily = NRegular,
                                fontSize = 18.sp,
                                color = Color(0xFFDEDEDE),
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Box(
                    modifier = Modifier.weight(1f)
                ){
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(
                            modifier = Modifier
                                .padding(10.dp)
                                .clip(RoundedCornerShape(50.dp)),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xDA19181E)
                            ),
                            onClick = {
                                navController.popBackStack()
                                navController.navigate("add_note_to_cloud_page")
                            }) {
                            Text(
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                                text = "Select Notes To Sync",
                                style = TextStyle(
                                    fontFamily = NRegular,
                                    fontSize = 15.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            modifier = Modifier
                                .padding(10.dp)
                                .clip(RoundedCornerShape(50.dp)),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xDA19181E)
                            ),
                            onClick = {
                                navController.popBackStack()
                            }) {
                            Text(
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                                text = "CANCEL",
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
        else{
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            )
            {
                Box(
                    modifier = Modifier.weight(1f)
                ){
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id= R.drawable.google),
                            contentDescription = "google",
                            modifier = Modifier
                                .requiredSize(55.dp)
                                .padding(start = 1.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(50.dp))
                        Text(text = "Sign-In with Google To Save Notes In Cloud",
                            style = TextStyle(
                                fontFamily = NRegular,
                                fontSize = 18.sp,
                                color = Color(0xFFDEDEDE),
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Text(text = "NOTE : As this application is completely free, images and voice notes can't be stored in cloud. Hope you understand.",
                    style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 15.sp,
                        color = Color(0xFFDEDEDE),
                    ),
                    textAlign = TextAlign.Start
                )
                Box(
                    modifier = Modifier.weight(1f)
                ){
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(
                            modifier = Modifier
                                .padding(10.dp)
                                .clip(RoundedCornerShape(50.dp)),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xDA19181E)
                            ),
                            onClick = {
                                launchSignIn()
                            }) {
                            Text(
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                                text = "SIGN-IN",
                                style = TextStyle(
                                    fontFamily = NRegular,
                                    fontSize = 15.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            modifier = Modifier
                                .padding(10.dp)
                                .clip(RoundedCornerShape(50.dp)),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xDA19181E)
                            ),
                            onClick = {
                                navController.popBackStack()
                            }) {
                            Text(
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                                text = "CANCEL",
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









