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

    @Query("DELETE FROM alarms WHERE id = :id")
    suspend fun deleteAlarm(id: Int)
}
