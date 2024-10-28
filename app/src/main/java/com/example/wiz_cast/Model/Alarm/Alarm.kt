package com.example.wiz_cast.Model.Alarm

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timeInMillis: Long,
    val isActive: Boolean = true
)

