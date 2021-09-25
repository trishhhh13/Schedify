package com.trishala13kohad.myapplication

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MySP", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun setISON(isOn: Boolean){
        editor.putBoolean(ISON, isOn)
        editor.commit()
    }
    fun getISON(): Boolean{
        return  sharedPreferences.getBoolean(ISON, false)
    }

    companion object {
        private const val ISON = "ISON"
    }

}