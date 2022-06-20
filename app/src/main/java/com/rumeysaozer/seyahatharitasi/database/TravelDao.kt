package com.rumeysaozer.seyahatharitasi.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.rumeysaozer.seyahatharitasi.model.Travel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface TravelDao {

    @Query("SELECT * FROM travel")
    fun getAll(): Flowable<List<Travel>>

    @Insert
    fun insert(tavel : Travel) : Completable

    @Delete
    fun delete(travel : Travel) : Completable
}