package com.example.library.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.library.R
import com.example.library.databinding.ActivityBookDetailBinding
import com.example.library.model.Book

@Suppress("DEPRECATION")
class BookDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        if (intent != null){
            val book = intent.getSerializableExtra("book") as Book

            Glide.with(this).load(book.image).into(binding.imgBook)
            binding.tvTitle.text = book.title
            binding.tvAuthor.text = book.author
            binding.tvAvailable.text = book.available
            binding.tvDescription.text = book.description
        }
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}