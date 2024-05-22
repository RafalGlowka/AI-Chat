package com.glowka.rafal.ai.chat.models

sealed interface Model {
    val label: String

    data class Gemini(override val label: String, val modelName: String) : Model
    data class OpenAI(override val label: String, val modelName: String) : Model
}
