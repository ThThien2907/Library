package com.example.library.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.library.R
import com.example.library.model.Category

class CategoryAdapter(var context: Activity, var resource: Int, var objects: List<Category>):
    ArrayAdapter<Category>(context, resource, objects) {

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val row = context.layoutInflater.inflate(R.layout.item_category_selected, null)
        val category = objects[position]

        val tvCategorySelected = row.findViewById<TextView>(R.id.tv_category_selected)

        tvCategorySelected.text = category.value
        return row
    }

    @SuppressLint("InflateParams")
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val row = context.layoutInflater.inflate(R.layout.item_category, null)
        val category = objects[position]

        val tvCategory = row.findViewById<TextView>(R.id.tv_category)

        tvCategory.text = category.value
        return row
    }
}