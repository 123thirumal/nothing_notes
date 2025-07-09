package com.example.mynotes.utils

import android.Manifest
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.abs

class AudioRecorderManager(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var amplitudeJob: Job? = null

    private val _amplitudeFlow = MutableStateFlow(0f)
    val amplitudeFlow: StateFlow<Float> = _amplitudeFlow

    fun startRecording() {
        val dir = File(context.filesDir, "audio_recordings")
        dir.mkdirs()
        outputFile = File(dir, "${System.currentTimeMillis()}.m4a")

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile!!.absolutePath)
            prepare()
            start()
        }

        // Launch amplitude tracking
        amplitudeJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                delay(100L) // update every 100ms
                try {
                    val amp = recorder?.maxAmplitude ?: 0
                    _amplitudeFlow.value = amp / 32767f
                } catch (_: Exception) {
                    _amplitudeFlow.value = 0f
                }
            }
        }
    }

    fun stopRecording() {
        amplitudeJob?.cancel()
        amplitudeJob = null

        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    fun getOutputFilePath(): String? = outputFile?.absolutePath
}
