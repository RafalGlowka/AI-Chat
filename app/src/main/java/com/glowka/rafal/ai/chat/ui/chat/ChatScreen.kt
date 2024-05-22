@file:Suppress("MagicNumber")

package com.glowka.rafal.ai.chat.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glowka.rafal.ai.chat.R
import androidx.compose.ui.graphics.Color

@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel,
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    val uiState by chatViewModel.uiState.collectAsState()

    Box(modifier = modifier) {
        val viewState = uiState

        LazyColumn(
            modifier = Modifier.padding(5.dp)
        ) {
            items(viewState.conversation.size) { index ->
                ChatMessage(viewState.conversation[index])
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
        ) {
            when {
                viewState.listening -> Text("Listening...")
                viewState.analyzing -> Text("Analyzing...")
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                modifier = Modifier
                    .background(
                        color = if (uiState.listening) Color.DarkGray else Color.LightGray,
                        shape = RoundedCornerShape(8.dp, 8.dp, 8.dp, 8.dp)
                    )
                    .padding(10.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                try {
                                    chatViewModel.onMicrophonePress()
                                    awaitRelease()
                                } finally {
                                    chatViewModel.onMicrophoneRelease()
                                }
                            }
                        )
                    },
                painter = painterResource(id = R.drawable.microphone_icon),
                contentDescription = "Microphone"
            )
        }

        Row {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier.clickable {
                    chatViewModel.onChangeModel()
                },
                text = uiState.model.label,
                style = TextStyle.Default.copy(
                    fontSize = 15.sp,
                    drawStyle = Stroke(
                        miter = 10f,
                        width = 5f,
                        join = StrokeJoin.Round
                    )
                )
            )
        }
    }
}

private val ChatBubbleShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)

@Composable
private fun ChatMessage(
    messageItem: MessageItem,
    modifier: Modifier = Modifier
) {
    Spacer(modifier = Modifier.height(1.dp))
    val backgroundBubbleColor = if (!messageItem.isAI) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Surface(
        modifier = modifier,
        color = backgroundBubbleColor,
        shape = ChatBubbleShape
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = messageItem.message
        )
    }
}
