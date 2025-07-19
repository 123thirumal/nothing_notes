package com.example.mynotes.pages

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mynotes.data.viewmodel.LockViewModel
import com.example.mynotes.data.viewmodel.NoteViewModel
import com.example.mynotes.ui.theme.NDot
import com.example.mynotes.ui.theme.NRegular
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft
import compose.icons.tablericons.Lock

@Composable
fun RemovePrivateFilesPage(noteViewModel: NoteViewModel, isPrivateUnlocked: MutableState<Boolean>, lockViewModel: LockViewModel,navController: NavController) {


    val privateFiles=noteViewModel.privateNotes.collectAsState(initial = emptyList()) //private files

    val removePrivateFilesRequest = remember{mutableStateOf(false)}
    LaunchedEffect(removePrivateFilesRequest.value) {
        if(removePrivateFilesRequest.value){
            privateFiles.value.forEach { note ->
                val updatedNote = note.copy(Private = false)
                noteViewModel.updateNote(updatedNote)
            }

            lockViewModel.deleteLock()

            isPrivateUnlocked.value=false

            removePrivateFilesRequest.value=false
            Toast.makeText(navController.context, "Private Files Removed", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }



    BackHandler(enabled = !removePrivateFilesRequest.value) {
        // Do nothing → disables when removing
        navController.popBackStack()
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
                    if(!removePrivateFilesRequest.value){
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
                                text = "Remove Private Files",
                                style = TextStyle(
                                    fontFamily = NDot,
                                    fontSize = 25.sp,
                                    color = Color.White,
                                ),
                            )
                        }
                    }
                }
            )
        },

        ){ innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        )    {
            Box(
                modifier = Modifier.weight(1f)
            ){
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = TablerIcons.Lock,
                        contentDescription = "Lock",
                        tint = Color(0xFFDEDEDE),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                    Text(text = "All Private Files Will Be Free Of Lock",
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
                if(removePrivateFilesRequest.value){
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "REMOVING...",
                            style = TextStyle(
                                fontFamily = NRegular,
                                fontSize = 15.sp,
                                color = Color(0xFFB9B9B9)
                            )
                        )
                    }
                }
                else{
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
                                backgroundColor = Color(0xFFCB070D)
                            ),
                            onClick = {
                                removePrivateFilesRequest.value=true
                            }
                        ) {
                            Text(
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                                text = "REMOVE",
                                style = TextStyle(
                                    fontFamily = NRegular,
                                    fontSize = 15.sp,
                                    color = Color(0xFFDEDEDE),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(50.dp))
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

