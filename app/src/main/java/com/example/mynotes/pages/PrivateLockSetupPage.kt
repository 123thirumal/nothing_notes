@file:Suppress("DEPRECATION")

package com.example.mynotes.pages

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.example.mynotes.R
import com.example.mynotes.data.viewmodel.LockViewModel
import com.example.mynotes.model.LockModel
import com.example.mynotes.ui.theme.NDot
import com.example.mynotes.ui.theme.NRegular
import com.example.mynotes.utils.authenticateUser
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowLeft
import compose.icons.tablericons.Backspace
import compose.icons.tablericons.Lock
import kotlinx.coroutines.delay
import okio.Lock


val keys = listOf(
    listOf("1", "2", "3"),
    listOf("4", "5", "6"),
    listOf("7", "8", "9"),
    listOf("back", "0", "done")
)


@Composable
fun PrivateLockSetupPage(lockViewModel: LockViewModel,navController: NavController,isUnlocked: MutableState<Boolean>) {




    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val isCustomPassword = remember{mutableStateOf(false)}

    val pinMismatch = remember { mutableStateOf(false) }

    val pins = remember { mutableStateListOf("", "", "", "") }
    val pinPtr = remember { mutableStateOf(0) }  // 0-based index
    val tempPins = remember { mutableStateListOf("", "", "", "") }
    val tempPinPtr = remember { mutableStateOf(0) }  // 0-based index

    val msgList=remember{mutableStateListOf("Setup Your Pin","Confirm Your Pin")}
    val msgPtr=remember{mutableStateOf(0)}


    if (pinMismatch.value) {
        LaunchedEffect(pinMismatch.value) {
            delay(1000)
            pinMismatch.value = false
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
                            text = "Private Files",
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
        if(isCustomPassword.value){
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            )    {
                Icon(imageVector = TablerIcons.Lock,
                    contentDescription = "Lock",
                    tint = Color(0xFFDEDEDE),
                    modifier = Modifier.size(40.dp)
                )
                Text(text = msgList[msgPtr.value],
                    style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 16.sp,
                        color = Color(0xFFDEDEDE),
                    ),
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    Box(modifier = Modifier.padding(10.dp)){
                        Image(
                            painter = painterResource(id=R.drawable.circle),
                            contentDescription = "Pin",
                            modifier = Modifier.requiredSize(18.dp),
                            colorFilter = ColorFilter.tint(
                                if (msgPtr.value == 0)
                                    if (pinMismatch.value) Color.Red
                                    else if (tempPins[0].isEmpty()) Color(0xFF2D2D2D) else Color(0xFF8C8C8C)
                                else
                                    if (pins[0].isEmpty()) Color(0xFF2D2D2D) else Color(0xFF8C8C8C)
                            ),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Box(modifier = Modifier.padding(10.dp)){
                        Image(
                            painter = painterResource(id=R.drawable.circle),
                            contentDescription = "Pin",
                            modifier = Modifier.requiredSize(18.dp),
                            colorFilter = ColorFilter.tint(
                                if (msgPtr.value == 0)
                                    if (pinMismatch.value) Color.Red
                                    else if (tempPins[1].isEmpty()) Color(0xFF2D2D2D) else Color(0xFF8C8C8C)
                                else
                                    if (pins[1].isEmpty()) Color(0xFF2D2D2D) else Color(0xFF8C8C8C)
                            ),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Box(modifier = Modifier.padding(10.dp)){
                        Image(
                            painter = painterResource(id=R.drawable.circle),
                            contentDescription = "Pin",
                            modifier = Modifier.requiredSize(18.dp),
                            colorFilter = ColorFilter.tint(
                                if (msgPtr.value == 0)
                                    if (pinMismatch.value) Color.Red
                                    else if (tempPins[2].isEmpty()) Color(0xFF2D2D2D) else Color(0xFF8C8C8C)
                                else
                                    if (pins[2].isEmpty()) Color(0xFF2D2D2D) else Color(0xFF8C8C8C)
                            ),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Box(modifier = Modifier.padding(10.dp)){
                        Image(
                            painter = painterResource(id=R.drawable.circle),
                            contentDescription = "Pin",
                            modifier = Modifier.requiredSize(18.dp),
                            colorFilter = ColorFilter.tint(
                                if (msgPtr.value == 0)
                                    if (pinMismatch.value) Color.Red
                                    else if (tempPins[3].isEmpty()) Color(0xFF2D2D2D) else Color(0xFF8C8C8C)
                                else
                                    if (pins[3].isEmpty()) Color(0xFF2D2D2D) else Color(0xFF8C8C8C)
                            ),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center){
                    keys.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            row.forEach { key ->
                                Box(
                                    modifier = Modifier
                                        .padding(9.dp)
                                        .size(90.dp)
                                        .clip(CircleShape)
                                        .background(if(key=="done"||key=="back")Color.Transparent else Color(0xDA19181E)) // Button background
                                        .clickable(enabled = key.isNotEmpty()) {
                                            if (key == "back") {
                                                if (msgPtr.value == 0) {
                                                    if (tempPinPtr.value > 0) {
                                                        tempPinPtr.value -= 1
                                                        tempPins[tempPinPtr.value] = ""
                                                    }
                                                } else {
                                                    if (pinPtr.value > 0) {
                                                        pinPtr.value -= 1
                                                        pins[pinPtr.value] = ""
                                                    }
                                                }
                                            } else if (key == "done") {
                                                if (msgPtr.value == 0) {
                                                    if (tempPinPtr.value != 4) {
                                                        Toast.makeText(context, "Enter all 4 digits", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        msgPtr.value = 1
                                                        pinPtr.value = 0
                                                        for (i in 0..3) {
                                                            pins[i] = ""
                                                        }
                                                    }
                                                } else {
                                                    if (pinPtr.value != 4) {
                                                        Toast.makeText(context, "Enter all 4 digits", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        if (pins.joinToString("") == tempPins.joinToString("")) {
                                                            // Proceed to unlock screen or save PIN
                                                            val newLock=LockModel(passcode = pins.joinToString(""))
                                                            lockViewModel.insertOrUpdate(newLock)
                                                            navController.popBackStack()
                                                            Toast.makeText(context, "PIN Set Successfully!", Toast.LENGTH_SHORT).show()
                                                        } else {
                                                            pinMismatch.value = true //to show red colour

                                                            Toast.makeText(context, "PINs do not match", Toast.LENGTH_SHORT).show()
                                                            msgPtr.value = 0
                                                            // Reset both
                                                            pinPtr.value = 0
                                                            tempPinPtr.value = 0
                                                            for (i in 0..3) {
                                                                tempPins[i] = ""
                                                                pins[i] = ""
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                // Number input
                                                if (msgPtr.value == 0) {
                                                    if (tempPinPtr.value <= 3) {
                                                        tempPins[tempPinPtr.value] = key
                                                        tempPinPtr.value += 1
                                                    }
                                                } else {
                                                    if (pinPtr.value <= 3) {
                                                        pins[pinPtr.value] = key
                                                        pinPtr.value += 1
                                                    }
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if(key=="back"){
                                        Icon(imageVector = TablerIcons.Backspace, contentDescription = "Back",
                                            tint = Color(0xFFDEDEDE), modifier = Modifier.size(35.dp))
                                    }
                                    else if(key=="done"){
                                        Icon(imageVector = Icons.Default.Done, contentDescription = "Done",
                                            tint = Color(0xFFDEDEDE), modifier = Modifier.size(35.dp))
                                    }
                                    else{
                                        Text(
                                            text = key,
                                            style = TextStyle(
                                                color = Color(0xFFDEDEDE),
                                                fontSize = 30.sp,
                                                fontFamily = NRegular
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
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
                        Text(text = "Choose The Type Of Password",
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
                                if (activity != null) {
                                    // Use the activity safely
                                    authenticateUser(
                                        activity,
                                        onSuccess = {
                                            Toast.makeText(context, "Verification Successful!", Toast.LENGTH_SHORT).show()
                                            val newLock=LockModel(isPhonePasscode = true)
                                            lockViewModel.insertOrUpdate(newLock)
                                            navController.popBackStack()
                                        },
                                        onError = { code, msg -> /* ... */ },
                                        onFailed = { Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show() }
                                    )
                                } else {
                                    Toast.makeText(context, "Error: Cannot access activity", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                            Text(
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                                text = "Use Phone's Password",
                                style = TextStyle(
                                    fontFamily = NRegular,
                                    fontSize = 15.sp,
                                    color = Color.White,
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
                                isCustomPassword.value=true
                            }) {
                            Text(
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                                text = "Use Custom Password",
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





