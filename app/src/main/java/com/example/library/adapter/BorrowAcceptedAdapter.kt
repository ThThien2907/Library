package com.example.library.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.library.R
import com.example.library.databinding.ItemBorrowReturnBinding
import com.example.library.model.BorrowReturnBook
import com.example.library.utils.*

class BorrowAcceptedAdapter(val context: Context,
                            private val returnBookListener: OnReturnBookClickListener,
                            private val readBookListener: OnReadBookClickListener,
                            private val borrowAgainListener: OnBorrowAgainClickListener,
                            private val itemClickListener: OnItemClickListener
):
    RecyclerView.Adapter<BorrowAcceptedAdapter.BorrowAcceptedViewHolder>() {
    private lateinit var list: ArrayList<BorrowReturnBook>

    inner class BorrowAcceptedViewHolder(val binding: ItemBorrowReturnBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorrowAcceptedViewHolder {
        return BorrowAcceptedViewHolder(ItemBorrowReturnBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @Suppress("DEPRECATION")
    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onBindViewHolder(holder: BorrowAcceptedViewHolder, position: Int) {
        val data = list[position]
        holder.binding.tvTitle.text = "${data.bookTitle}"

        //kiểm tra trạng thái của phiếu mượn và đổ dữ liệu cho từng trạng thái
        when(data.status){
            "1" -> {
                holder.apply {
                    binding.tvStatus.text = "Đang mượn"
                    binding.layoutBorrowTime.visibility = View.VISIBLE
                    binding.tvBorrowTime.text = "${data.borrowedDay}"
                    binding.layoutExpirationTime.visibility = View.VISIBLE
                    binding.tvExpirationTime.text = "${data.expirationDay}"
                    binding.btnReadReturn.visibility = View.VISIBLE
                }
            }
            "3" -> {
                holder.apply {
                    binding.tvStatus.text = "Đã trả"
                    binding.layoutBorrowTime.visibility = View.VISIBLE
                    binding.tvBorrowTime.text = "${data.borrowedDay}"
                    binding.layoutExpirationTime.visibility = View.VISIBLE
                    binding.tvExpirationTime.text = "${data.expirationDay}"
                    binding.layoutReturnTime.visibility = View.VISIBLE
                    binding.tvReturnTime.text = "${data.returnedDay}"
                    binding.btnBorrowAgain.visibility = View.VISIBLE
                }

            }
            "4" -> {
                holder.apply {
                    binding.tvStatus.text = "Quá hạn"
                    binding.layoutBorrowTime.visibility = View.VISIBLE
                    binding.tvBorrowTime.text = "${data.borrowedDay}"
                    binding.layoutExpirationTime.visibility = View.VISIBLE
                    binding.tvExpirationTime.text = "${data.expirationDay}"

                    binding.btnReadReturn.visibility = View.VISIBLE
                    binding.btnReadBook.visibility = View.GONE
                    binding.container.background = context.resources.getDrawable(R.drawable.bg_stroke_red)
                }

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

        holder.binding.container.setOnClickListener {
            itemClickListener.onItemClick(data.bookId!!)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<BorrowReturnBook>){
        this.list = list
        notifyDataSetChanged()
    }
}