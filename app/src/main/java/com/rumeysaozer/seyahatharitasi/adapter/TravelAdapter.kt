package com.rumeysaozer.seyahatharitasi.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rumeysaozer.seyahatharitasi.databinding.ItemRowBinding
import com.rumeysaozer.seyahatharitasi.model.Travel
import com.rumeysaozer.seyahatharitasi.view.MapsActivity

class TravelAdapter (val travelList : List<Travel>) : RecyclerView.Adapter<TravelAdapter.TravelHolder>() {
    class TravelHolder(val binding : ItemRowBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravelHolder {
        val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TravelHolder(binding)
    }

    override fun onBindViewHolder(holder: TravelHolder, position: Int) {
        holder.binding.rvTextView.text = travelList[position].name
        holder.itemView.setOnClickListener {
          val intent = Intent(holder.itemView.context, MapsActivity::class.java)
            intent.putExtra("travel",travelList[position])
            intent.putExtra("info","old")
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return travelList.size
    }
}