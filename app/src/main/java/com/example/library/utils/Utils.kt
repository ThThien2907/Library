package com.example.library.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.library.R

object Utils {
    fun isOnline(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    class DialogBox(var context: Context) {
        lateinit var dialog: Dialog
        lateinit var tvTitle : TextView
        lateinit var tvContent : TextView
        lateinit var  btnAccept: Button
        lateinit var  btnCancel: Button

        fun createDialog() {
            dialog = Dialog(context)
            dialog.setContentView(R.layout.dialog_box)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            tvTitle = dialog.findViewById(R.id.tv_title)
            tvContent = dialog.findViewById(R.id.tv_content)
            btnAccept = dialog.findViewById(R.id.btn_accept)
            btnCancel = dialog.findViewById(R.id.btn_cancel)
        }
    }
}