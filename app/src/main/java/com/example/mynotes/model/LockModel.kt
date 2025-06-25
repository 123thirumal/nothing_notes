package com.example.mynotes.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(tableName = "lock_table")
class LockModel (
    @PrimaryKey val id: Int = 0, // Always 0 — only one row
    var passcode: String?=null,
    var isPhonePasscode: Boolean =false,
)