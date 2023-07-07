package com.example.colorgenerator.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.main.colorgenerator.Model.ColorModel

@Dao
interface ColorDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertColor(colorModel: ColorModel)

    @Update
    fun updateColors(colorModel: List<ColorModel>)

    @Query("SELECT * FROM colors")
    fun getAllColors(): LiveData<List<ColorModel>>
}



