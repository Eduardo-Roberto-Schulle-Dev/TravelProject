package com.example.travelproject.TopAppBar

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

suspend fun generateSuggestionFromGemini(cidade: String, dataFinal: String, dataInicio: String, orcamento: Double, tipo: String): String {
    return try {
        val generativeModel = GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = "AIzaSyBBddS3SdxTHO4oLd7sRoj2LTY5uoFda9E"
        )

        val prompt = content {
            text("Monte um roteiro de viagem com pontos turísticos e atividades para a cidade de $cidade entre os dias $dataInicio e $dataFinal. seguindo o seguinte orçamento $orcamento e o seguinte tipo de viagem $tipo")
        }

        val response = generativeModel.generateContent(prompt)

        response.text ?: "Não foi possível gerar uma sugestão no momento."
    } catch (e: Exception) {
        Log.e("GeminiAPI", "Erro ao gerar sugestão de roteiro", e)
        "Erro ao gerar sugestão: ${e.localizedMessage ?: "Verifique sua conexão ou chave da API."}"
    }
}
