package com.glowka.rafal.ai.chat.models

import com.glowka.rafal.ai.chat.models.gemini.GeminiModelCaller
import com.glowka.rafal.ai.chat.models.openai.OpenAIModelCaller

object Models {

    val GEMINI_PRO = Model.Gemini("GEMINI PRO", "gemini-pro")
    val GEMINI_PRO_VISION = Model.Gemini("GEMINI PRO VISION", "gemini-pro-vision")
    val OPENAI_GPT_3_5_TURBO = Model.OpenAI("OPENAI GPT 3.5 Turbo", "gpt-3.5-turbo")

    fun getModelCaller(model: Model): ModelCaller? {
        return when (model) {
            is Model.Gemini ->
                GeminiModelCaller(model.modelName)

            is Model.OpenAI ->
                OpenAIModelCaller(model.modelName)

            else ->
                null
        }
    }
}
