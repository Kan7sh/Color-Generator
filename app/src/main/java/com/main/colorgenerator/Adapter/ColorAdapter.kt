package com.main.colorgenerator.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.card.MaterialCardView
import com.main.colorgenerator.Model.ColorModel
import com.main.colorgenerator.R
import java.text.SimpleDateFormat
import java.util.*

class ColorAdapter(
):Adapter<ColorAdapter.HolderColor>(){
    private var colorList: List<ColorModel> = emptyList()


    fun setColorList(newList: List<ColorModel>) {
        colorList = newList
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): HolderColor {
        val view =LayoutInflater.from(p0.context).inflate(R.layout.row_color,p0,false)
        return HolderColor(view)
    }

    override fun onBindViewHolder(holder: HolderColor, index: Int) {
        var colorModel = colorList[index]
        holder.colorCode.text = colorModel.colorName
        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(colorModel.createdTime))
        holder.date.text = date
        holder.card.setCardBackgroundColor(colorModel.colorValue)
    }

    override fun getItemCount(): Int {
        return colorList.size
    }


    inner class HolderColor(itemView: View):ViewHolder(itemView){
        var card:MaterialCardView = itemView.findViewById(R.id.card)
        var colorCode:TextView = itemView.findViewById(R.id.colorCode)
        var date:TextView = itemView.findViewById(R.id.date)
    }
}