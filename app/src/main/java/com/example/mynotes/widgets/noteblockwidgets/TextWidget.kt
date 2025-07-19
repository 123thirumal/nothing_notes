package com.example.mynotes.widgets.noteblockwidgets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.model.NoteBlockEntityModel
import com.example.mynotes.ui.theme.NRegular
import kotlinx.coroutines.launch

@Composable
fun TextWidget(textBlock: NoteBlockEntityModel.TextBlock, isChangeMade: MutableState<Boolean>) {

    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    BasicTextField(
        modifier = Modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester)
            .onFocusEvent{ focusState ->
                if( focusState.isFocused){
                    coroutineScope.launch {
                        bringIntoViewRequester.bringIntoView()
                    }
                }
            }
            .clip(RoundedCornerShape(20.dp))
            .padding(20.dp),
        value = textBlock.description.value,
        onValueChange = {
            textBlock.description.value = it
            isChangeMade.value=true
        },
        textStyle = TextStyle(
            fontFamily = NRegular, // Ensure NRegular is defined
            fontSize = 16.sp,
            color = Color(0xCBFFFFFF),
            fontWeight = FontWeight.W100
        ),
        decorationBox = { innerTextField ->
            if (textBlock.description.value.isEmpty()) {
                Text(
                    text = "...",
                    style = TextStyle(
                        fontFamily = NRegular,
                        fontSize = 16.sp,
                        color = Color(0xFF8C8C8C)
                    )
                )
            }
            innerTextField()
        },
        cursorBrush = SolidColor(Color.White)
    )
}