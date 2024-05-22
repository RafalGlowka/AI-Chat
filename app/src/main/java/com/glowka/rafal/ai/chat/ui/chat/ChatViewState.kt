package com.glowka.rafal.ai.chat.ui.chat

import com.glowka.rafal.ai.chat.models.Model
import com.glowka.rafal.ai.chat.models.Models

data class ChatViewState(
    val model: Model = Models.GEMINI_PRO,
    val conversation: List<MessageItem> = emptyList(),
    val listening: Boolean = false,
    val analyzing: Boolean = false,
    val speaking: Boolean = false,
)

data class MessageItem(
    val message: String,
    val isAI: Boolean,
)
