package com.example.mynotes.data.repository

import com.example.mynotes.data.dao.ImageDemoDao
import com.example.mynotes.model.ImageDemoModel
import android.util.Log


class ImageDemoRepository(
    private val imageDemoDao: ImageDemoDao
) {
    suspend fun getImageDemo(): ImageDemoModel {
        return imageDemoDao.getImageDemo()
    }

    suspend fun insertOrUpdateImageDemo(imageDemo: ImageDemoModel) {
        imageDemoDao.insertOrUpdateImageDemo(imageDemo)
    }

    suspend fun ensureRowExists() {
        if (imageDemoDao.getImageDemo() == null) {
            imageDemoDao.insertOrUpdateImageDemo(ImageDemoModel(id = 0, showImageDemo = true))
        }
    }

}