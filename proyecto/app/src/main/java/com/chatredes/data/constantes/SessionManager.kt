package com.chatredes.data.constantes

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.chatredes.ui.activity.MainActivity

class SessionManager {

    lateinit var pref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var con: Context

    var PRIVATE_MODE = 0

    constructor(con: Context) {
        this.con = con
        pref = con.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
        return
    }

    companion object {
        private const val PREF_NAME = "LoginPrefs"
        private const val IS_LOGIN = "IsLoggedIn"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_USER_JID = "JID"
    }

    fun createLoginSession(
        username:String,
        password:String,
        user_jid: String,
        isLogged: Boolean

    ){
        editor.putBoolean(IS_LOGIN, isLogged)
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_PASSWORD, password)
        editor.putString(KEY_USER_JID, user_jid)
        Log.e("commit", editor.commit().toString())
        Log.e("apply", editor.apply().toString())
    }


    fun getUserDetails(): HashMap<String, String> {
        val user: HashMap<String, String> = HashMap()
        pref.getString(KEY_USERNAME, null)?.let {
            user[KEY_USERNAME] = it
        }
        pref.getString(KEY_PASSWORD, null)?.let {
            user[KEY_PASSWORD] = it
        }
        user[KEY_USER_JID] = pref.getString(KEY_USER_JID, null)?.let {
            user[KEY_USER_JID] = it
        }.toString()

        return user
    }



    fun LogoutUser(){
        editor.clear()
        editor.apply()
        var i : Intent = Intent(con, MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        con.startActivity(i)
    }


    fun isLoggedIn(): Boolean{
        return pref.getBoolean(IS_LOGIN, false)
    }
}
