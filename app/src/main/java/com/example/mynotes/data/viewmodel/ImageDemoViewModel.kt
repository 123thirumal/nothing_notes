package com.example.mynotes.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.data.Graph
import com.example.mynotes.data.repository.ImageDemoRepository
import com.example.mynotes.model.ImageDemoModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ImageDemoViewModel(
    private val imageDemoRepository: ImageDemoRepository = Graph.imageDemoRepository
) : ViewModel(){

    private val _imgDemo = MutableStateFlow<ImageDemoModel?>(null)

    //it is the lock
    val imgDemo: StateFlow<ImageDemoModel?> = _imgDemo

    init {
        viewModelScope.launch {
            imageDemoRepository.ensureRowExists()
            _imgDemo.value = imageDemoRepository.getImageDemo()
        }
    }

    suspend fun insertOrUpdateImageDemo(imageDemo: ImageDemoModel){
        imageDemoRepository.insertOrUpdateImageDemo(imageDemo = imageDemo)
        _imgDemo.value = imageDemoRepository.getImageDemo()
    }
}