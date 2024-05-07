package com.example.library.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.library.databinding.ItemBookBinding
import com.example.library.model.Book
import com.example.library.utils.OnItemBookClickListener

class BookAdapterRcv(val context: Context, val listener: OnItemBookClickListener): RecyclerView.Adapter<BookAdapterRcv.BookViewHolder>(){
    private lateinit var listBook: ArrayList<Book>

    class BookViewHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root){

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        return BookViewHolder(ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return this.listBook.size
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = listBook[position]
        Glide.with(context).load(book.image).into(holder.binding.imgCardBook)
        holder.binding.tvCardTitle.text = book.title
        holder.binding.itemBookLayout.setOnClickListener {
            listener.onItemBookClick(book)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<Book>) {
        this.listBook = list
        notifyDataSetChanged()
    }
}