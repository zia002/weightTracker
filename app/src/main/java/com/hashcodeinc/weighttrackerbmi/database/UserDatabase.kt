package com.hashcodeinc.weighttrackerbmi.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

//-------------------------------User BMI database--------------------------------//
//================================================================================//
data class bmiData(var id:String, var bmiValue:String, var date:String, var month:String)
class BMIDatabase(context: Context): SQLiteOpenHelper(context, databaseName,null,1) {
    companion object{
        const val databaseName="UserBMIData"
        const val tableName="BMIDATA"
        const val col1="ID"
        const val col2="BMI_VALUE"
        const val col3="BMI_DATE"
        const val col4="BMI_MONTH"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $tableName (" +
                "$col1 INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$col2 TEXT, " +
                "$col3 TEXT, " +
                "$col4 TEXT)"
        db?.execSQL(createTable)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
    fun insertBMIData(bmiValue:String,date:String,month: String){
        val db=this.writableDatabase
        val nextId = getNextAvailablePrimaryKey(db)
        val query="INSERT INTO $tableName ($col1,$col2,$col3,$col4) VALUES(?,?,?,?)"
        db.execSQL(query, arrayOf(nextId,bmiValue,date,month))
        db.close()
    }
    fun getNextAvailablePrimaryKey(db: SQLiteDatabase): Int {
        val cursor = db.rawQuery("SELECT MAX(id) FROM $tableName", null)
        cursor.moveToFirst()
        val maxPrimaryKey = cursor.getInt(0)
        cursor.close()
        return maxPrimaryKey + 1
    }
    fun showBMIData():ArrayList<bmiData>{
        val dataList=ArrayList<bmiData>()
        val db=this.readableDatabase
        val query= "SELECT * FROM $tableName"
        val cursor=db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            do{
                val currentData =bmiData("null","null","null","null")
                currentData.id=cursor.getString(cursor.getColumnIndex(col1).toInt())
                currentData.bmiValue=cursor.getString(cursor.getColumnIndex(col2).toInt())
                currentData.date=cursor.getString(cursor.getColumnIndex(col3).toInt())
                currentData.month=cursor.getString(cursor.getColumnIndex(col4).toInt())
                dataList.add(currentData)
            }while(cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return dataList
    }
    fun deleteBMIData(id: Int) {
        val db = this.writableDatabase
        db.delete(tableName, "$col1=?", arrayOf(id.toString()))
        db.execSQL("UPDATE $tableName SET id=id-1 WHERE id > ?", arrayOf(id))
        db.close()
    }
    fun DelBMILimit(limit:Int) {
        val db = this.writableDatabase
        db.use { db ->
            db.execSQL("DELETE FROM $tableName WHERE id IN (SELECT id FROM $tableName LIMIT $limit)")
        }
    }

}

//-------------------------------User Weight database--------------------------------//
//================================================================================//
data class weightData(var id:String,var weight:String,var date:String,var month: String)
class weightDatabase(context:Context):SQLiteOpenHelper(context, DBname,null,1){
    companion object{
        val DBname="WEIGHT_DATABASE"
        val tableName="WEIGHT_DATA_TABLE"
        val col1="ID"
        val col2="WEIGHT"
        val col3="DATE"
        val col4="MONTH"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $tableName (" +
                "$col1 INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$col2 TEXT, " +
                "$col3 TEXT, " +
                "$col4 TEXT)"
        db?.execSQL(createTable)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
    fun insertWeightData(weightValue:String,date:String,month: String): Int {
        val db=this.writableDatabase
        val nextId = getNextAvailablePrimaryKey(db)
        val query="INSERT INTO $tableName ($col1,$col2,$col3,$col4) VALUES(?,?,?,?)"
        db.execSQL(query, arrayOf(nextId,weightValue,date,month))
        db.close()
        return nextId;
    }
    fun getNextAvailablePrimaryKey(db: SQLiteDatabase): Int {
        val cursor = db.rawQuery("SELECT MAX(id) FROM $tableName", null)
        cursor.moveToFirst()
        val maxPrimaryKey = cursor.getInt(0)
        cursor.close()
        return maxPrimaryKey + 1
    }
    fun deleteWeightData(id:Int){
        val db=this.writableDatabase
        db.delete(tableName,"$col1=?", arrayOf(id.toString()))
        db.execSQL("UPDATE $tableName SET id=id-1 WHERE id > ?", arrayOf(id))
        db.close()
    }
    fun showWeightData():ArrayList<weightData>{
        val db=this.readableDatabase
        val query= "SELECT * FROM $tableName"
        val cursor=db.rawQuery(query,null)
        val dataList=ArrayList<weightData>()
        if(cursor.moveToFirst()){
            do{
                val tmpdata=weightData("null","null","null","null")
                tmpdata.id=cursor.getString(cursor.getColumnIndex(col1).toInt())
                tmpdata.weight=cursor.getString(cursor.getColumnIndex(col2).toInt())
                tmpdata.date=cursor.getString(cursor.getColumnIndex(col3).toInt())
                tmpdata.month=cursor.getString(cursor.getColumnIndex(col4).toInt())
                dataList.add(tmpdata)
            }while(cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return dataList
    }
    fun DelWeightLimit(limit:Int) {
        val db = this.writableDatabase
        db.use { db ->
            db.execSQL("DELETE FROM $tableName WHERE id IN (SELECT id FROM $tableName LIMIT $limit)")
        }
    }
}

//---------------------------User Notification database---------------------------//
//================================================================================//
data class notifyData(var id:Int,var note:String,var type:String,var time:String)
class notifyDatabase(context: Context):SQLiteOpenHelper(context, databaeName,null,1){
    companion object{
        val databaeName="Notification_Database"
        val tableName="NotifyDataTable"
        val col0="ID"
        val col1="NOTE"
        val col2="TYPE"
        val col3="TIME"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val query="CREATE TABLE $tableName (" +
                "$col0 INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "$col1 TEXT,"+
                "$col2 TEXT,"+
                "$col3 TEXT )"
        db?.execSQL(query)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
    fun addNotifyItem(nd:notifyData): Long {
        val db=this.writableDatabase
        val nextId = getNextAvailablePrimaryKey(db)
        val values = ContentValues().apply {
            put(col0,nextId)
            put(col1, nd.note)
            put(col2, nd.type)
            put(col3, nd.time)
        }
        db.insert(tableName, null, values)
        db.close()
        return nextId.toLong()
    }
    fun getNextAvailablePrimaryKey(db: SQLiteDatabase): Int {
        val cursor = db.rawQuery("SELECT MAX(id) FROM $tableName", null)
        cursor.moveToFirst()
        val maxPrimaryKey = cursor.getInt(0)
        cursor.close()
        return maxPrimaryKey + 1
    }
    fun getNotifyItem():ArrayList<notifyData>{
        val dataList=ArrayList<notifyData>()
        val db=this.readableDatabase
        val query="SELECT * FROM $tableName"
        val cursor=db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            do {
                val data = notifyData(0, "Nothing", "null", "Not Set")
                data.id = cursor.getInt(cursor.getColumnIndex(col0).toInt())
                data.note = cursor.getString(cursor.getColumnIndex(col1).toInt())
                data.type = cursor.getString(cursor.getColumnIndex(col2).toInt())
                data.time = cursor.getString(cursor.getColumnIndex(col3).toInt())
                dataList.add(data)
            }while(cursor.moveToNext())
        }
        cursor.close()
        return dataList
    }
    fun removeNotify(id:Int){
        val db=this.writableDatabase
        db.delete(tableName,"$col0=?", arrayOf(id.toString()))
        db.execSQL("UPDATE $tableName SET id=id-1 WHERE id > ?", arrayOf(id))
        db.close()
    }
}
