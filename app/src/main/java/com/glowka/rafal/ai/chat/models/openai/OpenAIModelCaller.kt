package com.glowka.rafal.ai.chat.models.openai

import android.graphics.Bitmap
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.glowka.rafal.ai.chat.BuildConfig
import com.glowka.rafal.ai.chat.models.ExecutorResult
import com.glowka.rafal.ai.chat.models.ModelCaller
import kotlinx.coroutines.runBlocking

class OpenAIModelCaller(val modelName: String) : ModelCaller {

    private val openAI = OpenAI(BuildConfig.openai_apiKey)

    override fun describeImage(
        bitmap: Bitmap,
        prompt: String,
        emptyAnswerReplacement: String
    ): ExecutorResult {
        return ExecutorResult.Error("This model do not support images")
    }

    @OptIn(BetaOpenAI::class)
    override fun chatPrompt(prompt: String): ExecutorResult {
        return runBlocking {
            try {
                val chatRequest = ChatCompletionRequest(
                    model = ModelId(modelName),
                    messages = listOf(
                        ChatMessage(
                            role = ChatRole.Assistant,
                            content = prompt
                        ),
                    )
                )
                val completion = openAI.chatCompletion(chatRequest)

                completion.choices.first().message?.content?.let { text ->
                    ExecutorResult.Success(text)
                } ?: ExecutorResult.Success("I don't know")
            } catch (e: Exception) {
                val message = e.localizedMessage?.toString() ?: "Error"
                ExecutorResult.Error(message)
            }
        }
    }
}
