package com.example.mynotes.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.data.Graph
import com.example.mynotes.model.FolderModel
import com.example.mynotes.data.repository.FolderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FolderViewModel(private val folderRepository: FolderRepository=Graph.folderRepository): ViewModel() {
    val allFolders: StateFlow<List<FolderModel>> = folderRepository.getAllFolders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    fun insertFolder(folder: FolderModel){
        viewModelScope.launch(Dispatchers.IO) {
            folderRepository.insertFolder(folder)
        }
    }

    suspend fun deleteFolder(folder: FolderModel){
        folderRepository.deleteFolder(folder)
    }

    fun updateFolder(folder: FolderModel){
        viewModelScope.launch(Dispatchers.IO) {
            folderRepository.updateFolder(folder)
        }
    }

    fun getFolderById(id: Long,onResult: (FolderModel) -> Unit){
        viewModelScope.launch{
            val folder = folderRepository.getFolderById(id)
            onResult(folder)
        }
    }

}