package com.example.library.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.library.model.Token

class AuthDBHelper(val context: Context) : SQLiteOpenHelper(context, "auth", null, 1){
    companion object{
        const val TABLE = "token"
        const val ID = "id"
        const val ACCESS_TOKEN = "access_token"
        const val REFRESH_TOKEN = "refresh_token"
        const val ROLE = "role"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE "+TABLE+"(" +
                ID+ " Integer PRIMARY KEY AUTOINCREMENT, " +
                ACCESS_TOKEN+ " Text, " +
                REFRESH_TOKEN+ " Text, " +
                ROLE + " Text)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    @SuppressLint("Range", "Recycle")
    fun getToken() : Token {
        val db = this.readableDatabase
        val rs = db.rawQuery("SELECT * FROM $TABLE", null)
        var token = Token()
        if (rs.moveToFirst()) {
                val accessToken = rs.getString(rs.getColumnIndex(ACCESS_TOKEN))
                val refreshToken = rs.getString(rs.getColumnIndex(REFRESH_TOKEN))
                val role= rs.getString(rs.getColumnIndex(ROLE))

                token = Token(accessToken, refreshToken, role)
        }
        db.close()
        return token
    }

    fun addNewToken(token: Token): Long {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(ACCESS_TOKEN, token.accessToken)
        cv.put(REFRESH_TOKEN, token.refreshToken)
        cv.put(ROLE, token.role)
        val rs = db.insert(TABLE,null, cv)
        db.close()
        return rs
    }

    fun updateToken(token: Token, oldRefreshToken: String): Int{
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(ACCESS_TOKEN, token.accessToken)
        cv.put(REFRESH_TOKEN, token.refreshToken)
        val rs = db.update(TABLE, cv, "$REFRESH_TOKEN=?", arrayOf(oldRefreshToken))
        db.close()
        return rs
    }

    fun deleteToken(refreshToken: String): Int{
        val db = this.writableDatabase
        val rs = db.delete(TABLE, "$REFRESH_TOKEN=?", arrayOf(refreshToken))
        db.close()
        return rs
    }

    fun deleteAll(): Int{
        val db = this.writableDatabase
        val rs = db.delete(TABLE, "", arrayOf())
        db.close()
        return rs
    }
}