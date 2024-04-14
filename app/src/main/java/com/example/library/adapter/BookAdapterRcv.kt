package com.example.library.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.library.R
import com.example.library.databinding.ItemBookBinding
import com.example.library.model.Book

class BookAdapterRcv: RecyclerView.Adapter<BookAdapterRcv.BookViewHolder>() {
    private lateinit var listBook: ArrayList<Book>
    inner class BookViewHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        return BookViewHolder(ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return this.listBook.size
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        var book = listBook[position]
//        holder.binding.imgCardBook.setImageResource(R.mipmap.book_img)
        holder.binding.tvCardTitle.text = book.title
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<Book>) {
        this.listBook = list
        notifyDataSetChanged()
    }
}