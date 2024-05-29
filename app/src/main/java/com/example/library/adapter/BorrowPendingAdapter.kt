package com.example.library.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.library.databinding.ItemBorrowPendingBinding
import com.example.library.databinding.ItemBorrowReturnBinding
import com.example.library.model.BorrowReturnBook
import com.example.library.utils.OnItemClickListener

class BorrowPendingAdapter(private val onItemClickListener: OnItemClickListener): RecyclerView.Adapter<BorrowPendingAdapter.BorrowPendingViewHolder>() {
    private lateinit var list: ArrayList<BorrowReturnBook>
    inner class BorrowPendingViewHolder(val binding: ItemBorrowReturnBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorrowPendingViewHolder {
        return BorrowPendingViewHolder(ItemBorrowReturnBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BorrowPendingViewHolder, position: Int) {
        val data = list[position]
        holder.binding.tvTitle.text = "${data.bookTitle}"
        if (data.status == "0"){
            holder.apply {
                binding.tvStatus.text = "Đang xử lí"
                binding.layoutBorrowTime.visibility = View.VISIBLE
                binding.tvBorrowTime.text = "${data.borrowedDay}"
                binding.layoutExpirationTime.visibility = View.VISIBLE
                binding.tvExpirationTime.text = "${data.expirationDay}"
            }
        }

        holder.binding.container.setOnClickListener {
            onItemClickListener.onItemClick(data.bookId!!)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<BorrowReturnBook>){
        this.list = list
        notifyDataSetChanged()
    }
}