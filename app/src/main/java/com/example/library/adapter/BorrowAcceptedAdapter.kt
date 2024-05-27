package com.example.library.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.library.R
import com.example.library.databinding.ItemBorrowAcceptedBinding
import com.example.library.model.BorrowReturnBook
import com.example.library.utils.OnBorrowAgainClickListener
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

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: BorrowAcceptedViewHolder, position: Int) {
        val data = list[position]
        holder.binding.tvTitle.text = "${data.bookTitle}"
        when(data.status){
            "1" -> {
                holder.binding.tvStatus.text = "Đang mượn"
                holder.binding.tvBorrowTime.text = "${data.borrowedDay}"
                holder.binding.tvExpirationTime.text = "${data.expirationDay}"
                holder.binding.layoutReturnTime.visibility = View.GONE
                holder.binding.btnBorrowAgain.visibility = View.GONE
            }
            "3" -> {
                holder.binding.tvStatus.text = "Đã trả"
                holder.binding.tvBorrowTime.text = "${data.borrowedDay}"
                holder.binding.tvReturnTime.text = "${data.returnedDay}"
                holder.binding.layoutExpirationTime.visibility = View.GONE
                holder.binding.btnReadReturn.visibility = View.GONE
            }
            "4" -> {
                holder.binding.tvStatus.text = "Quá hạn"
                holder.binding.tvBorrowTime.text = "${data.borrowedDay}"
                holder.binding.tvExpirationTime.text = "${data.expirationDay}"
                holder.binding.layoutReturnTime.visibility = View.GONE
                holder.binding.btnBorrowAgain.visibility = View.GONE
                holder.binding.btnReadBook.visibility = View.GONE
                holder.binding.container.background = context.resources.getDrawable(R.drawable.bg_stroke_red)
            }
        }

        holder.binding.btnReturnBook.setOnClickListener {
            returnBookListener.onReturnBookClick(data)
        }

        holder.binding.btnReadBook.setOnClickListener {
            readBookListener.onReadBookClick(data.bookId!!)
        }

        holder.binding.btnBorrowAgain.setOnClickListener {
            borrowAgainListener.onBorrowAgainClick(data.bookId!!)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<BorrowReturnBook>){
        this.list = list
        notifyDataSetChanged()
    }
}