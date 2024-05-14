package com.example.library.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.library.databinding.ItemBorrowPendingBinding
import com.example.library.model.BorrowReturnBook

class BorrowPendingAdapter: RecyclerView.Adapter<BorrowPendingAdapter.BorrowPendingViewHolder>() {
    private lateinit var list: ArrayList<BorrowReturnBook>
    inner class BorrowPendingViewHolder(val binding: ItemBorrowPendingBinding): RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorrowPendingViewHolder {
        return BorrowPendingViewHolder(ItemBorrowPendingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BorrowPendingViewHolder, position: Int) {
        val data = list[position]
        holder.binding.tvTitle.text = "${data.bookTitle}"
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<BorrowReturnBook>){
        this.list = list
        notifyDataSetChanged()
    }
}