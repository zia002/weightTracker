package com.hashcodeinc.weighttrackerbmi.database

import android.content.Context
import androidx.core.content.ContextCompat
import com.hashcodeinc.weighttrackerbmi.R

object MyData {
    val sharedPreferencesName="My_Sharedpref_Name"
    fun addData(context: Context, key:String, value:String){
        val myPref=context.getSharedPreferences(sharedPreferencesName,Context.MODE_PRIVATE)
        val editor=myPref.edit()
        editor.putString(key,value)
        editor.apply()
    }
    fun getData(context: Context,key:String): String? {
        val myPref=context.getSharedPreferences(sharedPreferencesName,Context.MODE_PRIVATE)
        return myPref.getString(key,"No Data")
    }
    fun initColorList(context: Context): List<Int> {
        val colorList = listOf(
            ContextCompat.getColor(context, R.color.a),
            ContextCompat.getColor(context, R.color.b),
            ContextCompat.getColor(context, R.color.c),
            ContextCompat.getColor(context, R.color.d),
            ContextCompat.getColor(context, R.color.e),
            ContextCompat.getColor(context, R.color.f),
            ContextCompat.getColor(context, R.color.g),
            ContextCompat.getColor(context, R.color.h),
            ContextCompat.getColor(context, R.color.i),
            ContextCompat.getColor(context, R.color.j)
        )
        return colorList
    }
}