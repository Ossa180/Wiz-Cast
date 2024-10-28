package com.example.wiz_cast.Model.Alarm

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AlarmDao {
    @Insert
    suspend fun insertAlarm(alarm: Alarm): Long

    @Query("SELECT * FROM alarms WHERE id = :id")
    suspend fun getAlarmById(id: Int): Alarm?

    @Query("SELECT * FROM alarms")
    suspend fun getAllAlarms(): List<Alarm>

    @Query("DELETE FROM alarms WHERE id = :id")
    suspend fun deleteAlarm(id: Int)

    // delete expired alarm
    @Query("DELETE FROM alarms WHERE timeInMillis < :currentTime")
    suspend fun deleteExpiredAlarms(currentTime: Long)
}
