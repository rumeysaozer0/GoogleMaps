package com.rumeysaozer.seyahatharitasi.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rumeysaozer.seyahatharitasi.model.Travel

@Database(entities = arrayOf(Travel::class), version = 1)
abstract class TravelDatabase : RoomDatabase() {
    abstract fun travelDao():TravelDao
}