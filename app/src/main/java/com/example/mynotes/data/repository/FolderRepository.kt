package com.example.mynotes.data.repository

import com.example.mynotes.data.dao.FolderDao
import com.example.mynotes.model.FolderModel
import kotlinx.coroutines.flow.Flow

class FolderRepository(
    private val folderDao: FolderDao
) {
    suspend fun insertFolder(folder: FolderModel): String{
        folderDao.insertFolder(folder)
        return folder.id
    }
    suspend fun deleteFolder(folder: FolderModel){
        folderDao.deleteFolder(folder)
    }
    suspend fun updateFolder(folder: FolderModel) {
        folderDao.updateFolder(folder)
    }

    fun getAllFolders() : Flow<List<FolderModel>>{
        return folderDao.getAllFolders()
    }

    suspend fun getFolderById(id: String) : FolderModel {
        return folderDao.getFolderById(id)
    }

}