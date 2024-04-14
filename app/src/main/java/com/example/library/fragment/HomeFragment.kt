package com.example.library.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.library.R
import com.example.library.adapter.BookAdapterRcv
import com.example.library.databinding.FragmentHomeBinding
import com.example.library.model.Book

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: BookAdapterRcv
    private lateinit var listBook: ArrayList<Book>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =  FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    //http://localhost/CT06/do_an/api/routes/book/get_books.php?limit=99&page=1
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listBook = ArrayList()
        listBook.add(Book(1,
            "Logan Kade (Fallen Crest High #5.5)",
            93,
            "https://books.toscrape.com/media/cache/2b/44/2b4404e00c242bf1b8263bdd99c07354.jpg",
            "People think that just because they know my name, my reputation, and my family that they know me.",
            "ACC8",
            "Samuel Ramirez",
            "Academic"
        ))
        listBook.add(Book(1,
            "Logan Kade (Fallen Crest High #5.5)",
            93,
            "https://books.toscrape.com/media/cache/2b/44/2b4404e00c242bf1b8263bdd99c07354.jpg",
            "People think that just because they know my name, my reputation, and my family that they know me.",
            "ACC8",
            "Samuel Ramirez",
            "Academic"
        ))
        listBook.add(Book(1,
            "Logan Kade (Fallen Crest High #5.5)",
            93,
            "https://books.toscrape.com/media/cache/2b/44/2b4404e00c242bf1b8263bdd99c07354.jpg",
            "People think that just because they know my name, my reputation, and my family that they know me.",
            "ACC8",
            "Samuel Ramirez",
            "Academic"
        ))
        listBook.add(Book(1,
            "Logan Kade (Fallen Crest High #5.5)",
            93,
            "https://books.toscrape.com/media/cache/2b/44/2b4404e00c242bf1b8263bdd99c07354.jpg",
            "People think that just because they know my name, my reputation, and my family that they know me.",
            "ACC8",
            "Samuel Ramirez",
            "Academic"
        ))
        listBook.add(Book(1,
            "Logan Kade (Fallen Crest High #5.5)",
            93,
            "https://books.toscrape.com/media/cache/2b/44/2b4404e00c242bf1b8263bdd99c07354.jpg",
            "People think that just because they know my name, my reputation, and my family that they know me.",
            "ACC8",
            "Samuel Ramirez",
            "Academic"
        ))
        adapter = BookAdapterRcv()
        adapter.setData(listBook)
        binding.rcvBook.adapter = adapter
        binding.rcvBook.layoutManager = GridLayoutManager(this.activity, 3)
    }
}