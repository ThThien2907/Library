package com.example.library.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.library.R
import com.example.library.databinding.ItemBorrowAcceptedBinding
import com.example.library.model.BorrowReturnBook
import com.example.library.utils.OnBorrowAgainClickListener
import com.example.library.utils.OnItemBookClickListener
import com.example.library.utils.OnReadBookClickListener
import com.example.library.utils.OnReturnBookClickListener

class BorrowAcceptedAdapter(val context: Context,
                            private val returnBookListener: OnReturnBookClickListener,
                            private val readBookListener: OnReadBookClickListener,
                            private val borrowAgainListener: OnBorrowAgainClickListener):
    RecyclerView.Adapter<BorrowAcceptedAdapter.BorrowAcceptedViewHolder>() {
    private lateinit var list: ArrayList<BorrowReturnBook>

    inner class BorrowAcceptedViewHolder(val binding: ItemBorrowAcceptedBinding): RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorrowAcceptedViewHolder {
        return BorrowAcceptedViewHolder(ItemBorrowAcceptedBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BorrowAcceptedViewHolder, position: Int) {
        val data = list[position]
        holder.binding.tvTitle.text = "${data.bookTitle}"
        when(data.status){
            "1" -> {
                holder.binding.tvStatus.text = "Đang mượn"
                holder.binding.tvBorrowTime.text = "${data.borrowedDay}"
                holder.binding.layoutReturnTime.visibility = View.GONE
                holder.binding.layoutBorrowAgain.visibility = View.GONE
                holder.binding.layoutBtnFun.visibility = View.VISIBLE
            }
            "3" -> {
                holder.binding.tvStatus.text = "Đã trả"
                holder.binding.tvBorrowTime.text = "${data.borrowedDay}"
                holder.binding.tvReturnTime.text = "${data.returnedDay}"
                holder.binding.layoutReturnTime.visibility = View.VISIBLE
                holder.binding.layoutBorrowAgain.visibility = View.VISIBLE
                holder.binding.layoutBtnFun.visibility = View.GONE
            }
        }

        holder.binding.layoutReturnBook.setOnClickListener {
            returnBookListener.onReturnBookClick(data)
        }

        holder.binding.layoutReadBook.setOnClickListener {
            readBookListener.onReadBookClick(data.bookId!!)
        }

        holder.binding.layoutBorrowAgain.setOnClickListener {
            borrowAgainListener.onBorrowAgainClick(data.bookId!!)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<BorrowReturnBook>){
        this.list = list
        notifyDataSetChanged()
    }
}