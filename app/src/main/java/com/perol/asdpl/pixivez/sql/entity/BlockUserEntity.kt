package com.perol.asdpl.pixivez.sql.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blockUser")
class BlockUserEntity constructor(@ColumnInfo(name = "name") var name: String, @ColumnInfo(name = "account") var account: String) {
    @PrimaryKey(autoGenerate = true)
    var Id: Long = 0
}