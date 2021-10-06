package com.trishala13kohad.myapplication

import android.content.Context
import android.content.SharedPreferences

//Class to know when the accessibility service is on
class PreferenceManager(context: Context) {

    //getting sharedPreferences
    private val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MySP", Context.MODE_PRIVATE)

    //getting shared preferences editor
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun setISON(isOn: Boolean){
        //set true when accessibility service is on
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