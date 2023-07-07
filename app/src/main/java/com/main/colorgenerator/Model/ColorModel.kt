package com.main.colorgenerator.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "colors")
data class ColorModel(
    @ColumnInfo(name = "colorValue")
    val colorValue: Int,
    @PrimaryKey
    @ColumnInfo(name = "createdTime")
    val createdTime: Long,
    @ColumnInfo(name = "colorName")
    val colorName: String,
    @ColumnInfo(name = "synced")
    var synced: Boolean
)

