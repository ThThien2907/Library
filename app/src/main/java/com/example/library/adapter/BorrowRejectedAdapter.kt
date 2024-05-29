package com.example.library.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.library.databinding.ItemBorrowRejectedBinding
import com.example.library.databinding.ItemBorrowReturnBinding
import com.example.library.model.BorrowReturnBook
import com.example.library.utils.OnBorrowAgainClickListener
import com.example.library.utils.OnItemClickListener

class BorrowRejectedAdapter(private val onItemClickListener: OnItemClickListener):
    RecyclerView.Adapter<BorrowRejectedAdapter.BorrowRejectedViewHolder>() {
    private lateinit var list: ArrayList<BorrowReturnBook>

    inner class BorrowRejectedViewHolder(val binding: ItemBorrowReturnBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorrowRejectedViewHolder {
        return BorrowRejectedViewHolder(ItemBorrowReturnBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BorrowRejectedViewHolder, position: Int) {
        val data = list[position]
        holder.binding.tvTitle.text = "${data.bookTitle}"
        if (data.status == "2"){
            holder.apply {
                binding.tvStatus.text = "Bị từ chối"
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