package com.glowka.rafal.ai.chat.models.gemini

import android.graphics.Bitmap
import android.util.Log
import com.glowka.rafal.ai.chat.BuildConfig
import com.glowka.rafal.ai.chat.models.ExecutorResult
import com.glowka.rafal.ai.chat.models.ModelCaller
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.runBlocking

class GeminiModelCaller(modelName: String) : ModelCaller {

    private val generativeModel = GenerativeModel(
        modelName = modelName,
        apiKey = BuildConfig.gemini_apiKey
    )

    override fun describeImage(
        bitmap: Bitmap,
        prompt: String,
        emptyAnswerReplacement: String
    ): ExecutorResult {
        return runBlocking {
            try {
                val response = generativeModel.generateContent(
                    content {
                        image(bitmap)
                        text(prompt)
                    }
                )
                response.text?.let { text ->
                    ExecutorResult.Success(text)
                } ?: ExecutorResult.Success(emptyAnswerReplacement)
            } catch (e: Exception) {
                Log.e("ZXC", "Prompt exception", e)
                e.printStackTrace()
                val message = e.localizedMessage ?: "Unexpected error"
                ExecutorResult.Error(message)
            }
        }
    }

    override fun chatPrompt(prompt: String): ExecutorResult {
        return runBlocking {
            try {
                val response = generativeModel.generateContent(
                    content {
                        text(
                            "I will ask you a question in a moment. Use friendly language and simple sentences. My question is."
                        )
                        text(prompt)
                    }
                )
                response.text?.let { text ->
                    ExecutorResult.Success(text)
                } ?: ExecutorResult.Success("I don't know")
            } catch (e: Exception) {
                Log.e("ZXC", "Prompt exception", e)
                e.printStackTrace()
                val message = e.localizedMessage ?: "Unexpected error"
                ExecutorResult.Error(message)
            }
        }
    }
}
