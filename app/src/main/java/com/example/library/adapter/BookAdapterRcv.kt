package com.example.library.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.library.R
import com.example.library.databinding.ItemBookBinding
import com.example.library.databinding.ItemLoadingBinding
import com.example.library.model.Book

class BookAdapterRcv(val context: Context): RecyclerView.Adapter<BookAdapterRcv.BookViewHolder>() {
    private lateinit var listBook: ArrayList<Book>
    private var isLoadingAdd: Boolean = false

    companion object {
        const val TYPE_ITEM = 1
        const val TYPE_LOADING = 2
    }
    inner class BookViewHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root){}
//    inner class LoadingViewHolder(val binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root){}



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
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<Book>) {
        this.listBook = list
        notifyDataSetChanged()
    }
//
//    override fun getItemViewType(position: Int): Int {
//        if (position == listBook.size - 1 && isLoadingAdd)
//            return TYPE_LOADING
//        return TYPE_ITEM
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return if (TYPE_ITEM == viewType){
//            BookViewHolder(ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false))
//        } else {
//            LoadingViewHolder(ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return listBook.size
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        if (holder.itemViewType == TYPE_ITEM){
//            val book = listBook[position]
//            val bookViewHolder = holder as BookViewHolder
//            Glide.with(context).load(book.image).into(bookViewHolder.binding.imgCardBook)
//            bookViewHolder.binding.tvCardTitle.text = book.title
//        }
//    }
//
//    fun addFooterLoading(){
//        isLoadingAdd = true
//        listBook.add(Book(0, "", 0, "","","","",""))
//        notifyItemInserted(listBook.size - 1)
//    }
//
//    fun removeFooterLoading(){
//        isLoadingAdd = false
//        val position = listBook.size - 1
//        val book = listBook[position]
//        listBook.removeAt(position)
//        notifyItemRemoved(position)
//    }
}