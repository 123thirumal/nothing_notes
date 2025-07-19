package com.example.mynotes.utils

import android.content.Context
import android.util.Log
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.StorageService

class SpeechToTextManager(
    private val context: Context,
    private val onResultCallback: (String) -> Unit,
) : RecognitionListener {

    private var model: Model? = null
    private var speechService: SpeechService? = null

    fun loadModel(onLoaded: () -> Unit) {
        StorageService.unpack(context, "model_en_us", "model",
            { unpackedModel ->
                model = unpackedModel
                onLoaded()
            },
            { exception ->
                Log.d("model", exception.message.toString())
                onResult("Model load error: ${exception.message}")
            }
        )
    }

    fun startListening() {
        val recognizer = Recognizer(model, 16000.0f)
        speechService = SpeechService(recognizer, 16000.0f)
        speechService?.startListening(this)
    }

    fun stopListening() {
        speechService?.stop()
        speechService = null
    }

    override fun onResult(hypothesis: String?) {
        hypothesis?.let {  onResultCallback(it) }
    }

    override fun onPartialResult(hypothesis: String?) {
        hypothesis?.let {  }
    }

    override fun onFinalResult(hypothesis: String?) {
        hypothesis?.let { }
    }

    override fun onError(e: java.lang.Exception?) {
        onResult("Error: ${e?.message}")
    }

    override fun onTimeout() {
        onResult("Timeout reached")
    }
}