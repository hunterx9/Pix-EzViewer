package com.perol.asdpl.pixivez.viewmodel

import androidx.lifecycle.ViewModel
import com.perol.asdpl.pixivez.services.PxEZApp
import com.perol.asdpl.pixivez.sql.AppDatabase
import com.perol.asdpl.pixivez.sql.entity.BlockTagEntity
import com.perol.asdpl.pixivez.sql.entity.BlockUserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BlockViewModel : ViewModel() {
    private val appDatabase = AppDatabase.getInstance(PxEZApp.instance)
    suspend fun getAllTags() =
        withContext(Dispatchers.IO) {
            appDatabase.blockTagDao().getAllTags()
        }

    suspend fun getAllUsers() =
        withContext(Dispatchers.IO) {
            appDatabase.blockUserDao().getAllUsers()
        }

    suspend fun deleteSingleTag(blockTagEntity: BlockTagEntity) =
        appDatabase.blockTagDao().deleteTag(blockTagEntity)

    suspend fun insertBlockTag(blockTagEntity: BlockTagEntity) = appDatabase.blockTagDao()
        .insert(blockTagEntity)

    suspend fun deleteSingleUser(blockUserEntity: BlockUserEntity) =
        appDatabase.blockUserDao().deleteUser(blockUserEntity)

    suspend fun insertBlockUser(blockUserEntity: BlockUserEntity) = appDatabase.blockUserDao()
        .insert(blockUserEntity)
}