package com.example.timify

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text



class TimifyAdapter(private val Recycleitem: ArrayList<Task>): RecyclerView.Adapter<TimifyAdapter.MyViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_adapter, parent, false)
        return MyViewHolder(itemView)
    }



    override fun getItemCount(): Int {
        return Recycleitem.size
    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = Recycleitem[position]
        holder.category.text = currentitem.category
        holder.s_date.text = currentitem.date
        holder.descrip.text = currentitem.description
        holder.start_time.text = currentitem.startTime.toString()
        holder.end_time.text = currentitem.endTime.toString()
        holder.minGoal.text = currentitem.minDailyGoal.toString()
        holder.maxGoal.text = currentitem.maxDailyGoal.toString()
    }



    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){



        val category : TextView =  itemView.findViewById(R.id.tvCategory)
        val s_date : TextView = itemView.findViewById(R.id.tvDate)
        val start_time : TextView = itemView.findViewById(R.id.tvStartTime)
        val end_time : TextView = itemView.findViewById(R.id.tvEndTime)
        val descrip : TextView = itemView.findViewById(R.id.tvDescription)
        val minGoal : TextView = itemView.findViewById(R.id.tvMinDailyGoal)
        val maxGoal : TextView = itemView.findViewById(R.id.tvMaxDailyGoal)



    }



}