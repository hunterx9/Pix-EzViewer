package com.perol.asdpl.pixivez.sql.dao

import androidx.room.*

import com.perol.asdpl.pixivez.sql.entity.BlockUserEntity


@Dao
abstract class BlockUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(query: BlockUserEntity)

    @Query("SELECT * FROM blockUser")
    abstract suspend fun getAllUsers(): List<BlockUserEntity>

    @Delete
    abstract suspend fun deleteUser(blockUserEntity: BlockUserEntity)

}