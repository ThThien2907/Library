package com.example.library.utils

import com.example.library.model.BorrowReturnBook

interface OnReturnBookClickListener {
    fun onReturnBookClick(borrowReturnBook: BorrowReturnBook)
}