package com.example.mynotes.widgets

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.navigation.NavController
import com.example.mynotes.R
import com.example.mynotes.model.FolderModel
import com.example.mynotes.model.NoteModel
import com.example.mynotes.ui.theme.NRegular
import compose.icons.TablerIcons
import compose.icons.tablericons.CircleCheck
import compose.icons.tablericons.DotsVertical
import compose.icons.tablericons.Lock
import compose.icons.tablericons.LockOff


@Composable
fun AppBarWidget(searchSelect: MutableState<Boolean>, searchToggle: ()->Unit, headSelect: MutableState<String>,
                 isUnlocked: MutableState<Boolean>, showDialogForChangePassword: MutableState<Boolean>,
                 navController: NavController, isSelectedFiles: MutableState<Boolean>, isSelectedFolders: MutableState<Boolean>,
                 selectedNoteList: SnapshotStateList<NoteModel>, selectedFolderList: SnapshotStateList<FolderModel>,
                 isSelectedFilesInPrivate: MutableState<Boolean>, selectedFilesInPrivate: SnapshotStateList<NoteModel>){
    val searchColor: Color= if(searchSelect.value) Color.Red else Color.White
    var inputStr by remember{mutableStateOf("")}
    val focusRequester = remember { FocusRequester() }

    val isExpandedSettings = remember{mutableStateOf(false)}

    LaunchedEffect(searchSelect.value) {
        if (searchSelect.value) {
            focusRequester.requestFocus()
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
//            IconButton(onClick = {
//                searchToggle()
//            }) {
//                Icon(
//                    imageVector = Icons.Default.Search, contentDescription = "Search", tint = searchColor,
//                    modifier = Modifier.size(28.dp)
//                )
//            }
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
