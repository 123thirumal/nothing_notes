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
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.LayoutDirection
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

@Composable
fun PrivateLockPage(lockViewModel: LockViewModel,navController: NavController,isUnlocked: MutableState<Boolean>) {

    val lock= lockViewModel.lock.collectAsState()

    val passcode = lock.value?.passcode

    val isPhonePasscode= lock.value?.isPhonePasscode ?: false



    val context = LocalContext.current
    val activity = context as? FragmentActivity

    val pinMismatch = remember { mutableStateOf(false) }

    val tempPins = remember { mutableStateListOf("", "", "", "") }
    val tempPinPtr = remember { mutableStateOf(0) }  // 0-based index

    val msg=remember{mutableStateOf("Enter Your Pin")}

    val requestPhonePassword = remember{mutableStateOf(false)}


    if (pinMismatch.value) {
        LaunchedEffect(pinMismatch.value) {
            delay(1000)
            pinMismatch.value = false
        }
    }

    LaunchedEffect(requestPhonePassword.value) {
        if(requestPhonePassword.value){
            activity?.let {
                authenticateUser(
                    it,
                    onSuccess = {
                        isUnlocked.value=true
                        Toast.makeText(context, "Verified", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                        // You can navigate or unlock here
                    },
                    onError = { code, msg ->
                        Toast.makeText(context, "Error: $msg", Toast.LENGTH_SHORT).show()
                    },
                    onFailed = {
                        Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show()
                    }
                )
            } ?: run {
                Toast.makeText(context, "Error: Cannot access activity", Toast.LENGTH_SHORT).show()
            }
            requestPhonePassword.value=false
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
                        Spacer(modifier = Modifier.width(20.dp))
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
        if(!isPhonePasscode){
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Icon(
                    imageVector = TablerIcons.Lock,
                    contentDescription = "Lock",
                    tint = Color(0xFFDEDEDE),
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = msg.value,
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
                ) {
                    Box(modifier = Modifier.padding(10.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.circle),
                            contentDescription = "Pin",
                            modifier = Modifier.requiredSize(18.dp),
                            colorFilter = ColorFilter.tint(
                                if (pinMismatch.value) Color.Red
                                else if (tempPins[0].isEmpty()) Color(0xFF2D2D2D) else Color(
                                    0xFF8C8C8C
                                )
                            ),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Box(modifier = Modifier.padding(10.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.circle),
                            contentDescription = "Pin",
                            modifier = Modifier.requiredSize(18.dp),
                            colorFilter = ColorFilter.tint(
                                if (pinMismatch.value) Color.Red
                                else if (tempPins[1].isEmpty()) Color(0xFF2D2D2D) else Color(
                                    0xFF8C8C8C
                                )
                            ),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Box(modifier = Modifier.padding(10.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.circle),
                            contentDescription = "Pin",
                            modifier = Modifier.requiredSize(18.dp),
                            colorFilter = ColorFilter.tint(
                                if (pinMismatch.value) Color.Red
                                else if (tempPins[2].isEmpty()) Color(0xFF2D2D2D) else Color(
                                    0xFF8C8C8C
                                )
                            ),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Box(modifier = Modifier.padding(10.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.circle),
                            contentDescription = "Pin",
                            modifier = Modifier.requiredSize(18.dp),
                            colorFilter = ColorFilter.tint(
                                if (pinMismatch.value) Color.Red
                                else if (tempPins[3].isEmpty()) Color(0xFF2D2D2D) else Color(
                                    0xFF8C8C8C
                                )
                            ),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
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
                                        .background(
                                            if (key == "done" || key == "back") Color.Transparent else Color(
                                                0xDA19181E
                                            )
                                        ) // Button background
                                        .clickable(enabled = key.isNotEmpty()) {
                                            if (key == "back") {
                                                if (tempPinPtr.value > 0) {
                                                    tempPinPtr.value -= 1
                                                    tempPins[tempPinPtr.value] = ""
                                                }
                                            } else if (key == "done") {
                                                if (tempPinPtr.value != 4) {
                                                    Toast.makeText(
                                                        context,
                                                        "Enter all 4 digits",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } else {
                                                    if(tempPins.joinToString("")==passcode){
                                                        isUnlocked.value=true
                                                        navController.popBackStack()
                                                        Toast.makeText(context, "PIN Verified!", Toast.LENGTH_SHORT).show()
                                                    }
                                                    else{
                                                        pinMismatch.value = true //to show red colour
                                                        Toast.makeText(context, "PIN MISMATCH!", Toast.LENGTH_SHORT).show()
                                                        tempPinPtr.value = 0
                                                        for (i in 0..3) {
                                                            tempPins[i] = ""
                                                        }
                                                    }
                                                }
                                            } else {
                                                // Number input
                                                if (tempPinPtr.value <= 3) {
                                                    tempPins[tempPinPtr.value] = key
                                                    tempPinPtr.value += 1
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (key == "back") {
                                        Icon(
                                            imageVector = TablerIcons.Backspace,
                                            contentDescription = "Back",
                                            tint = Color(0xFFDEDEDE),
                                            modifier = Modifier.size(35.dp)
                                        )
                                    } else if (key == "done") {
                                        Icon(
                                            imageVector = Icons.Default.Done,
                                            contentDescription = "Done",
                                            tint = Color(0xFFDEDEDE),
                                            modifier = Modifier.size(35.dp)
                                        )
                                    } else {
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
        } else{
            LaunchedEffect(Unit) {
            activity?.let {
                authenticateUser(
                    it,
                    onSuccess = {
                        isUnlocked.value=true
                        Toast.makeText(context, "Verified", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                        // You can navigate or unlock here
                    },
                    onError = { code, msg ->
                        Toast.makeText(context, "Error: $msg", Toast.LENGTH_SHORT).show()
                    },
                    onFailed = {
                        Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show()
                    }
                )
            } ?: run {
                Toast.makeText(context, "Error: Cannot access activity", Toast.LENGTH_SHORT).show()
            }
        }
            Column(
                modifier = Modifier.fillMaxSize().padding(top = innerPadding.calculateTopPadding()+80.dp, bottom = innerPadding.calculateBottomPadding(),
                    start = innerPadding.calculateLeftPadding(LayoutDirection.Ltr), end = innerPadding.calculateRightPadding(LayoutDirection.Ltr)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            )    {
                Box(){
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceAround){
                        Icon(imageVector = TablerIcons.Lock,
                            contentDescription = "Lock",
                            tint = Color(0xFFDEDEDE),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(50.dp))
                        Text(text = "Verify Your Identity",
                            style = TextStyle(
                                fontFamily = NRegular,
                                fontSize = 18.sp,
                                color = Color(0xFFDEDEDE),
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Button(
                    modifier = Modifier
                        .padding(10.dp)
                        .clip(RoundedCornerShape(50.dp)),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFCB070D)
                    ),
                    onClick = {
                        requestPhonePassword.value=true
                    }) {
                    Text(
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
                        text = "UNLOCK",
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





