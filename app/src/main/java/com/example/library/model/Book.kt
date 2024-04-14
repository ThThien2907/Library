package com.example.library.model

data class Book(
    var id: Int,
    var title: String,
    var available: Int,
    var image: String,
    var description: String,
    var category_code: String,
    var author: String,
    var category_value: String
)
