package com.example.library.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.library.R
import com.example.library.activity.MainActivity
import com.example.library.model.Token
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SetTextI18n")
object Dialog {
    fun createDialog(context: Context,
                     onCreateDialog:(dialog: Dialog,
                                     tvTitle : TextView,
                                     tvContent : TextView,
                                     btnAccept: Button,
                                     btnCancel: Button
                     ) -> Unit) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_box)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvTitle : TextView = dialog.findViewById(R.id.tv_title)
        val tvContent : TextView = dialog.findViewById(R.id.tv_content)
        val btnAccept: Button = dialog.findViewById(R.id.btn_accept)
        val btnCancel: Button = dialog.findViewById(R.id.btn_cancel)
        onCreateDialog(dialog, tvTitle, tvContent, btnAccept, btnCancel)
    }

    fun createDialogLoginSessionExpired(activity: Activity){
        createDialog(activity){
                dialog, tvTitle, tvContent, btnAccept, _ ->
            tvTitle.text = "Thông báo"
            tvContent.text = "Hết phiên đăng nhập. Vui lòng đăng nhập lại để tiếp tục."
            dialog.setCancelable(false)

            btnAccept.setOnClickListener {
                dialog.dismiss()
                AuthToken.storeToken(activity, Token())
                val intent = Intent(activity, MainActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }

            dialog.show()
        }
    }


    fun createDialogConnectionError(activity: Activity){
        createDialog(activity){
            dialog, tvTitle, tvContent, btnAccept, _ ->
            dialog.setCancelable(false)
            tvTitle.text = "Lỗi kết nối"
            tvContent.text = "Lỗi kết nối đến máy chủ. Vui lòng kiểm tra lại."

            btnAccept.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun createDialogDatePicker(context: Context, onCheckedDateTime: (expirationTimestamp: Int) -> Unit){
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_date_picker)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnSelectDate: Button = dialog.findViewById(R.id.btn_select_date)
        val btnSelectTime: Button = dialog.findViewById(R.id.btn_select_time)
        val btnAccept: Button = dialog.findViewById(R.id.btn_accept)

        val currentDateTime = Calendar.getInstance()
        val dateTimeExpired = Calendar.getInstance()

        var isCheckedDate = false
        var isCheckedTime = false

        btnSelectDate.setOnClickListener {
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    dateTimeExpired.set(Calendar.YEAR, year)
                    dateTimeExpired.set(Calendar.MONTH, month)
                    dateTimeExpired.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formatDate = dateFormat.format(dateTimeExpired.time)
                    btnSelectDate.text = formatDate

                    if (dateTimeExpired.timeInMillis < currentDateTime.timeInMillis){
                        btnSelectDate.background = context.getDrawable(R.drawable.bg_stroke_red)
                        isCheckedDate = false
                    }
                    else{
                        btnSelectDate.background = context.getDrawable(R.drawable.bg_stroke_gray_200)
                        isCheckedDate = true

                    }
                },
                currentDateTime.get(Calendar.YEAR),
                currentDateTime.get(Calendar.MONTH),
                currentDateTime.get(Calendar.DAY_OF_MONTH)
            ).show()

        }

        btnSelectTime.setOnClickListener {
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    dateTimeExpired.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    dateTimeExpired.set(Calendar.MINUTE, minute)
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val formatTime = timeFormat.format(dateTimeExpired.time)
                    btnSelectTime.text = formatTime

                    if (dateTimeExpired.timeInMillis < currentDateTime.timeInMillis){
                        btnSelectTime.background = context.getDrawable(R.drawable.bg_stroke_red)
                        isCheckedTime = false
                    }
                    else{
                        btnSelectTime.background = context.getDrawable(R.drawable.bg_stroke_gray_200)
                        isCheckedTime = true
                    }
                },
                currentDateTime.get(Calendar.HOUR_OF_DAY),
                currentDateTime.get(Calendar.MINUTE),
                true
            ).show()
        }

        btnAccept.setOnClickListener {
            if (isCheckedDate && isCheckedTime){
                dialog.dismiss()
                val expirationTimestamp = (dateTimeExpired.timeInMillis / 1000) + 25200
                onCheckedDateTime(expirationTimestamp.toInt())
            }
            else {
                if (!isCheckedDate)
                    btnSelectDate.background = context.getDrawable(R.drawable.bg_stroke_red)
                if (!isCheckedTime)
                    btnSelectTime.background = context.getDrawable(R.drawable.bg_stroke_red)
            }
        }
        dialog.setCancelable(true)
        dialog.show()
    }
}