package com.example.mynotes.widgets.noteblockwidgets.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.R
import com.example.mynotes.model.CheckListModel
import com.example.mynotes.model.NoteBlockEntityModel
import com.example.mynotes.ui.theme.NRegular
import compose.icons.TablerIcons
import compose.icons.tablericons.Circle


@Composable
fun CheckListWidget(checkListBlock: NoteBlockEntityModel.CheckListBlock, isChangeMade: MutableState<Boolean>){
    val lists = checkListBlock.items

    Column(modifier = Modifier.fillMaxSize()) {
        lists.forEachIndexed { index, lst ->
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(9.dp)
            ) {

                val isChecked = remember { mutableStateOf(lst.isChecked) }
                // checkbox
                if(isChecked.value)
                    Image(
                        painter = painterResource(id=R.drawable.check_circle),
                        contentDescription = "checked",
                        modifier = Modifier.requiredSize(25.dp).
                                clickable(
                                    onClick = {
                                        isChecked.value=!isChecked.value
                                        lst.isChecked=isChecked.value
                                        isChangeMade.value=true
                                    }
                                ),
                        colorFilter = ColorFilter.tint(Color.White),
                        contentScale = ContentScale.Fit
                    )
                else
                    Icon(
                        modifier = Modifier.clickable(
                            onClick = {
                                isChecked.value=!isChecked.value
                                lst.isChecked=isChecked.value
                                isChangeMade.value=true
                            }
                        ).size(25.dp),
                        imageVector = TablerIcons.Circle,
                        contentDescription = "Unchecked",
                        tint = Color(0xCBFFFFFF)
                    )

                Spacer(modifier = Modifier.width(5.dp))
                // Text Field
                val text = remember { mutableStateOf(lst.description) }
                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = if (text.value.isEmpty()) " " else text.value,
                    onValueChange = {
                        if (it.isEmpty()) {
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
                        textDecoration = if(isChecked.value) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    decorationBox = { innerTextField ->
                        if (text.value.isEmpty()) {
                            Text(
                                text = " ...",
                                style = TextStyle(
                                    fontFamily = NRegular,
                                    fontSize = 16.sp,
                                    color = Color(0xFF8C8C8C)
                                )
                            )
                        }
                        innerTextField()
                    },
                    cursorBrush = SolidColor(Color.White),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            lists.add(CheckListModel(description = ""))
                            isChangeMade.value=true
                        }
                    ),
                )
            }
        }
    }

}