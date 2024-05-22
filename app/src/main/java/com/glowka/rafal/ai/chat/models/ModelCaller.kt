package com.glowka.rafal.ai.chat.models

import android.graphics.Bitmap

sealed interface ExecutorResult {
    data class Success(val text: String) : ExecutorResult
    data class Error(val errorMessage: String) : ExecutorResult
}

interface ModelCaller {

    fun describeImage(bitmap: Bitmap, prompt: String, emptyAnswerReplacement: String = ""): ExecutorResult

    fun chatPrompt(prompt: String): ExecutorResult
}