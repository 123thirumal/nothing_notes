package com.example.mynotes.widgets.noteblockwidgets.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.model.ListModel
import com.example.mynotes.model.NoteBlockEntityModel
import com.example.mynotes.ui.theme.NRegular
import androidx.compose.ui.text.input.ImeAction


@Composable
fun BulletedListWidget(bulletListBlock: NoteBlockEntityModel.BulletedListBlock,
                       isChangeMade: MutableState<Boolean>) {
    val lists = bulletListBlock.items

    Column(modifier = Modifier.fillMaxSize()) {
        lists.forEachIndexed { index, lst ->
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(9.dp).padding(start = 3.dp)
            ) {
                // Bullet
                Box(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .size(15.dp)
                        .background(Color(0xFFD7D7D7), shape = CircleShape)
                )

                Spacer(modifier = Modifier.width(5.dp))
                // Text Field
                val text = remember { mutableStateOf(lst.description) }
                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = if(text.value.isEmpty()) " " else text.value,
                    onValueChange = {
                        if(it.isEmpty()){
                            lists.removeAt(index)
                        }
                        lst.description = it
                        text.value=it
                        isChangeMade.value=true
                    },
                    textStyle = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 16.sp,
                        color = Color(0xCBFFFFFF),
                        fontWeight = FontWeight.W100,
                    ),
                    cursorBrush = SolidColor(Color.White),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            lists.add(ListModel(description = ""))
                            isChangeMade.value=true
                        }
                    )
                )
            }
        }
    }
}
