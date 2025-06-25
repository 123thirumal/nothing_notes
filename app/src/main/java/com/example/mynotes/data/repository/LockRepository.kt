package com.example.mynotes.data.repository

import com.example.mynotes.data.dao.LockDao
import com.example.mynotes.data.dao.NoteDao
import com.example.mynotes.model.LockModel

class LockRepository(
    private val lockDao: LockDao
) {
    suspend fun getLock(): LockModel? {
        return lockDao.getLock()
    }

    suspend fun insertOrUpdate(lock: LockModel) {
        lockDao.insertOrUpdate(lock)
    }

    suspend fun deleteLock() {
        lockDao.deleteLock()
    }


}