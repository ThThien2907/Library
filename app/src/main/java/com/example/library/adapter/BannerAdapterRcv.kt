package com.example.library.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.library.databinding.ItemBannerBinding
import com.example.library.model.Banner

class BannerAdapterRcv: RecyclerView.Adapter<BannerAdapterRcv.BannerViewHolder>() {
    private lateinit var list: ArrayList<Banner>
    inner class BannerViewHolder(val binding: ItemBannerBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        return BannerViewHolder(ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = this.list.size

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = list[position]
        holder.binding.imgBanner.setImageResource(banner.img!!)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<Banner>){
        this.list = list
        notifyDataSetChanged()
    }
}