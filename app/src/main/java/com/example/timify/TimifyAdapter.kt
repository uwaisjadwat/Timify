package com.example.timify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TimifyAdapter(private val dataList: List<Task>) :
    RecyclerView.Adapter<TimifyAdapter.ViewHolder>() {



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textCategory: TextView = itemView.findViewById(R.id.textCategory)
        val textDate: TextView = itemView.findViewById(R.id.textDate)
        val textTime: TextView = itemView.findViewById(R.id.textTime)
        val imagePhoto: ImageView = itemView.findViewById(R.id.imagePhoto)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_adapter, parent, false)
        return ViewHolder(itemView)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]



        holder.textCategory.text = data.category
        holder.textDate.text = data.date
        holder.textTime.text = "${data.startTime} - ${data.endTime}"



    }



    override fun getItemCount(): Int {
        return dataList.size
    }
}