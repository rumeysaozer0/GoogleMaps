package com.rumeysaozer.seyahatharitasi.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class Travel (
    var name : String,
    var explanation : String,
    var latittude: Double,
    var longtitude : Double
        ) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id = 0
}