package com.example.mynotes.pages

import android.app.Activity
import android.credentials.CredentialOption
import android.credentials.GetCredentialRequest
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.room.util.TableInfo
import coil.compose.AsyncImage
import com.example.mynotes.R
import com.example.mynotes.data.viewmodel.LockViewModel
import com.example.mynotes.data.viewmodel.NoteViewModel
import com.example.mynotes.model.LockModel
import com.example.mynotes.ui.theme.NDot
import com.example.mynotes.ui.theme.NRegular
import com.example.mynotes.utils.authenticateUser
import com.example.mynotes.utils.isNetworkAvailable
import com.example.mynotes.widgets.LoadingWidget
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft
import compose.icons.tablericons.Backspace
import compose.icons.tablericons.CloudFog
import compose.icons.tablericons.CloudOff
import compose.icons.tablericons.CloudUpload
import compose.icons.tablericons.FilePlus
import compose.icons.tablericons.FileSearch
import compose.icons.tablericons.Lock
import compose.icons.tablericons.Logout
import compose.icons.tablericons.Search
import compose.icons.tablericons.Trash
import compose.icons.tablericons.Viewfinder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import androidx.compose.runtime.getValue


@Composable
fun ProfilePage(currentUser: FirebaseUser?, navController: NavController,
                removeUser: () -> Unit, noteViewModel: NoteViewModel) {

    val allNotes by noteViewModel.allNotes.collectAsState(initial = emptyList())
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val notesInCloudCount = remember { mutableIntStateOf(-1) }
    val context = LocalContext.current
    val activity = context as Activity

    val credentialManager = CredentialManager.create(context)
    val coroutineScope = rememberCoroutineScope()
    val userName = currentUser?.displayName ?: "User"
    val userPhotoUrl = currentUser?.photoUrl

    // to get notes count in cloud
    LaunchedEffect(uid) {
        uid?.let {
            if (!isNetworkAvailable(context)) {
                return@LaunchedEffect
            }
            val firestore = FirebaseFirestore.getInstance()
            val notesRef = firestore.collection("users").document(uid).collection("notes")
            try {
                val snapshot = notesRef.get().await()
                notesInCloudCount.intValue=snapshot.size()
            } catch (e: Exception) {
                Toast.makeText(context, "Error syncing notes: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }




    val showDialogForLogout = remember{mutableStateOf(false)}
    if(showDialogForLogout.value){
        Dialog(onDismissRequest = {
            showDialogForLogout.value = false
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
                        text = "Are You Sure To Logout", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 18.sp,
                            color = Color(0xFFB6B6B6)
                        ))
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(textAlign = TextAlign.Start,
                        text = "Note: Synced notes will be available in your local storage.", style = TextStyle(
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
                            showDialogForLogout.value = false
                            coroutineScope.launch {
                                FirebaseAuth.getInstance().signOut()
                                removeUser()
                                withContext(Dispatchers.Main) {
                                    noteViewModel.makeAllNotesNotSavedInCloud()
                                    Toast.makeText(context, "Logged Out", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                            }
                        }) {
                        Text(modifier = Modifier.padding(vertical = 5.dp), text = "LOGOUT", style = TextStyle(
                            fontFamily = NRegular,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = {
                            showDialogForLogout.value = false
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

    //===============for delete=========================
    val isDeleting = remember{mutableStateOf(false)}
    val deleteDone = remember{mutableStateOf(false)}
    val requestNoteListUpdate = remember{mutableStateOf(false)}

    fun handleDeleteAcc(result: GetCredentialResponse) {
        val credential = result.credential

        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        val selectedEmail = googleIdTokenCredential.id

                        val firebaseUser = FirebaseAuth.getInstance().currentUser

                        if (firebaseUser != null) {
                            if (firebaseUser.email != selectedEmail) {
                                // User selected a different account
                                Toast.makeText(context, "Please select your signed-in account", Toast.LENGTH_SHORT).show()
                                isDeleting.value = false
                                deleteDone.value = false
                                return
                            }
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

                            firebaseUser.reauthenticate(firebaseCredential)
                                .addOnCompleteListener { reauthTask ->
                                    if (reauthTask.isSuccessful) {
                                        coroutineScope.launch {
                                            try{
                                                //delete the notes
                                                val fireStore = FirebaseFirestore.getInstance()
                                                val userRef = fireStore.collection("users").document(firebaseUser.uid)
                                                val noteListSnapshot = userRef.collection("notes").get().await()
                                                for(noteDoc in noteListSnapshot.documents){
                                                    val noteId = noteDoc.id
                                                    val blocksSnapshot = userRef.collection("notes").document(noteId).collection("blocks").get().await()
                                                    for (blockDoc in blocksSnapshot.documents) {
                                                        blockDoc.reference.delete().await() //deleting blocks
                                                    }
                                                    noteDoc.reference.delete().await() //deleting note
                                                }


                                                //delete the account
                                                firebaseUser.delete().await()
                                                Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                                                requestNoteListUpdate.value = true
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show()
                                                isDeleting.value=false
                                                deleteDone.value=false
                                            }

                                        }
                                    } else {
                                        Toast.makeText(context, "Re-auth failed", Toast.LENGTH_SHORT).show()
                                        isDeleting.value=false
                                        deleteDone.value=false
                                    }
                                }
                        } else {
                            Toast.makeText(context, "No user logged in", Toast.LENGTH_SHORT).show()
                            isDeleting.value=false
                            deleteDone.value=false
                        }

                    } catch (e: GoogleIdTokenParsingException) {
                        Toast.makeText(context, "Delete Account failed:${e.message}", Toast.LENGTH_SHORT).show()
                        isDeleting.value=false
                        deleteDone.value=false
                    }
                }
            }

            else -> {
                Toast.makeText(context, "Delete Account failed", Toast.LENGTH_SHORT).show()
                isDeleting.value=false
                deleteDone.value=false
            }
        }
    }

    LaunchedEffect(requestNoteListUpdate.value) {
        if(requestNoteListUpdate.value){
            noteViewModel.makeAllNotesNotSavedInCloud()
            deleteDone.value=true
            delay(200L)
            isDeleting.value=false
            deleteDone.value=false
            removeUser()
            withContext(Dispatchers.Main) {
                navController.popBackStack()
            }
            requestNoteListUpdate.value=false
        }
    }

    val showDialogForDeleteAccount = remember{mutableStateOf(false)}
    if(showDialogForDeleteAccount.value){
        Dialog(onDismissRequest = {
            showDialogForDeleteAccount.value = false
            isDeleting.value=false
            deleteDone.value=false
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
                    Text(text = "Are You Sure To Delete Account", style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 18.sp,
                        color = Color(0xFFDEDEDE),
                        textAlign = TextAlign.Center
                    ))
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(textAlign = TextAlign.Start,
                        text = "Note: All your notes saved in cloud will be deleted. But synced notes will be available in your" +
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
                            if(!isNetworkAvailable(context)){
                                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                                showDialogForDeleteAccount.value=false
                                isDeleting.value=false
                                deleteDone.value=false
                            }
                            else{
                                showDialogForDeleteAccount.value=false
                                isDeleting.value=true
                                deleteDone.value=false

                                // 1. Build the Google ID credential option
                                val googleIdOption = GetGoogleIdOption.Builder()
                                    .setFilterByAuthorizedAccounts(false) // Allows new users
                                    .setServerClientId(context.getString(R.string.server_client_id))
                                    .setAutoSelectEnabled(false)
                                    .setNonce(UUID.randomUUID().toString())
                                    .build()

                                val request = androidx.credentials.GetCredentialRequest.Builder()
                                    .addCredentialOption(googleIdOption)
                                    .build()

                                coroutineScope.launch {
                                    try {
                                        val result = credentialManager.getCredential(
                                            request = request,
                                            context = activity
                                        )
                                        // User selected an account successfully
                                        handleDeleteAcc(result)
                                    } catch (e: GetCredentialException) {
                                        if (e.message?.contains("user canceled", ignoreCase = true) == true ||
                                            e.toString().contains("user canceled", ignoreCase = true)
                                        ) {
                                            // User cancelled the sign-in flow
                                            Toast.makeText(context, "verification cancelled", Toast.LENGTH_SHORT).show()
                                        } else {
                                            // Some other failure
                                            Toast.makeText(context, "verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }

                                        isDeleting.value=false
                                        deleteDone.value=false
                                    }
                                }
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
                            showDialogForDeleteAccount.value = false
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
                                .clickable(onClick = { navController.popBackStack() })
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "Profile",
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
        if(isDeleting.value){
            LoadingWidget(loadDone = deleteDone, msg = "Deleting...")
        }
        else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding(), start = 10.dp, end = 10.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                // Profile Header
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceAround
                    ) {

                        Spacer(modifier = Modifier.height(50.dp))


                        if (userPhotoUrl != null) {
                            AsyncImage(
                                model = userPhotoUrl,
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color.White, CircleShape)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Default Profile",
                                modifier = Modifier.size(150.dp),
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(80.dp))

                        Text(
                            text = userName,
                            style = TextStyle(
                                fontFamily = NDot,
                                fontSize = 35.sp,
                                color = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Top
                            ) {
                                Text(
                                    modifier = Modifier.weight(0.3f), text = "Notes Saved In Cloud",
                                    style = TextStyle(
                                        color = Color.Black, fontFamily = NRegular,
                                        fontSize = 15.sp, fontWeight = FontWeight.Bold
                                    ),
                                )

                                if (notesInCloudCount.intValue == -1) {
                                    Text(
                                        modifier = Modifier
                                            .weight(0.4f)
                                            .fillMaxWidth()
                                            .fillMaxHeight(),
                                        text = "...",
                                        style = TextStyle(
                                            color = Color.Black,
                                            fontFamily = NRegular,
                                            fontSize = 25.sp
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                } else {
                                    Text(
                                        modifier = Modifier
                                            .weight(0.4f)
                                            .fillMaxWidth()
                                            .fillMaxHeight(),
                                        text = notesInCloudCount.intValue.toString(),
                                        style = TextStyle(
                                            color = Color.Black,
                                            fontFamily = NDot,
                                            fontSize = 50.sp
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clickable(
                                    onClick = {
                                        navController.navigate("view_cloud_notes_page")
                                    }
                                ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xDA19181E))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {

                                Icon(
                                    imageVector = TablerIcons.FileSearch,
                                    contentDescription = "view",
                                    tint = Color.White,
                                    modifier = Modifier.size(50.dp)
                                )

                                Text(
                                    text = "View Saved Notes",
                                    style = TextStyle(
                                        color = Color.White,
                                        fontFamily = NRegular,
                                        fontSize = 15.sp
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }


                //row 2

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clickable(
                                    onClick = {
                                        navController.navigate("add_note_to_cloud_page")
                                    }
                                ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xDA19181E))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {

                                Image(
                                    painter = painterResource(id = R.drawable.file_add),
                                    contentDescription = "add",
                                    modifier = Modifier
                                        .requiredSize(50.dp)
                                        .padding(start = 1.dp),
                                    contentScale = ContentScale.Fit
                                )

                                Text(
                                    text = "Add Notes To Cloud",
                                    style = TextStyle(
                                        color = Color.White,
                                        fontFamily = NRegular,
                                        fontSize = 15.sp
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clickable(
                                    onClick = {
                                        navController.navigate("remove_notes_from_cloud_page")
                                    }
                                ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xDA19181E))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {

                                Image(
                                    painter = painterResource(id = R.drawable.file_remove),
                                    contentDescription = "remove",
                                    modifier = Modifier
                                        .requiredSize(50.dp)
                                        .padding(start = 1.dp),
                                    contentScale = ContentScale.Fit
                                )

                                Text(
                                    text = "Remove Notes From Cloud",
                                    style = TextStyle(
                                        color = Color.White,
                                        fontFamily = NRegular,
                                        fontSize = 15.sp
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // row 3

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .clickable(
                                    onClick = {
                                        showDialogForLogout.value = true
                                    }
                                ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xDA19181E))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {

                                Icon(
                                    imageVector = TablerIcons.Logout,
                                    contentDescription = "logout",
                                    tint = Color.White,
                                    modifier = Modifier.size(50.dp)
                                )

                                Text(
                                    text = "LOGOUT",
                                    style = TextStyle(
                                        color = Color.White,
                                        fontFamily = NRegular,
                                        fontSize = 15.sp
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .clickable(
                                    onClick = {
                                        showDialogForDeleteAccount.value = true
                                    }
                                ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xDA19181E))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {

                                Icon(
                                    imageVector = TablerIcons.Trash,
                                    contentDescription = "delete",
                                    tint = Color.Red,
                                    modifier = Modifier.size(50.dp)
                                )

                                Text(
                                    text = "DELETE ACCOUNT",
                                    style = TextStyle(
                                        color = Color.Red,
                                        fontFamily = NRegular,
                                        fontSize = 15.sp
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}





