package com.example.colorgenerator.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.main.colorgenerator.Model.ColorModel

@Database(entities = [ColorModel::class], version = 2)
abstract class ColorDatabase : RoomDatabase() {
    abstract fun colorDao(): ColorDao
}

