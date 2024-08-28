package com.hashcodeinc.weighttrackerbmi.fragment

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.BaseAdapter
import android.widget.TextView
import com.hashcodeinc.weighttrackerbmi.R
import com.hashcodeinc.weighttrackerbmi.database.notifyData
import com.hashcodeinc.weighttrackerbmi.databinding.FragmentReminderBinding
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.google.android.material.textfield.TextInputEditText
import com.hashcodeinc.weighttrackerbmi.database.MyWorker
import com.hashcodeinc.weighttrackerbmi.database.notifyDatabase
import java.util.Calendar
import java.util.concurrent.TimeUnit
import android.app.TimePickerDialog
import android.util.Log
import android.widget.TimePicker
import androidx.work.OneTimeWorkRequestBuilder
import com.hashcodeinc.weighttrackerbmi.ScheduleNotificationWorker
import com.hashcodeinc.weighttrackerbmi.database.ReminderForegroundService
import java.text.SimpleDateFormat
import java.util.Locale

class myAdapterNotifyDataShow(val context: Context, private val dataList:ArrayList<notifyData>): BaseAdapter() {
    override fun getCount(): Int {
        return dataList.size
    }
    override fun getItem(position: Int): Any {
        return dataList[position]
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, ConvertView: View?, parent: ViewGroup?): View? {
        var convertView =ConvertView
        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.custom_reminder_item, parent, false)
        }
        val notifyText=convertView?.findViewById<TextView>(R.id.notifyText)
        val notifyTime=convertView?.findViewById<TextView>(R.id.notifyTime)
        val notifyDel=convertView?.findViewById<LinearLayout>(R.id.notifyDel)
        notifyText?.text=dataList[position].note
        if(dataList[position].time==""){
            notifyTime?.text=" Not Set"
        }
        else notifyTime?.text=dataList[position].time
        notifyDel?.setOnClickListener {
            //-------to cancel the reminder-----//
            val workManager=WorkManager.getInstance(context)
            workManager.cancelAllWorkByTag(dataList[position].id.toString())
            //--------delete from DB----------//
            val db=notifyDatabase(context)
            db.removeNotify(dataList[position].id)
            dataList.removeAt(position)
            notifyDataSetChanged()
            Toast.makeText(context,"Note Deleted",Toast.LENGTH_SHORT).show()
        }
        return convertView
    }
}

class ReminderFragment : Fragment() {
    val notifyReqCode=101
    private lateinit var binding:FragmentReminderBinding
    private lateinit var dataList: ArrayList<notifyData>
    private lateinit var db:notifyDatabase
    private  var timeDelay: Long = 0
    private var userTime:String="Set Time"
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding=FragmentReminderBinding.inflate(inflater,container,false)

        //-----notification permission taking here-----------//
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)!=(PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.POST_NOTIFICATIONS),notifyReqCode)
        }
        //---------------------------------------------------------------------------------------------------------//
        //-----to fetch the data and set in the list view with adapter-------//
        db=notifyDatabase(requireContext())
        dataList=db.getNotifyItem()
        val adapter=myAdapterNotifyDataShow(requireContext(),dataList)
        binding.reminderList.adapter=adapter
        adapter.notifyDataSetChanged()
        //------when need to add new item------//
        binding.addNotify.setOnClickListener {
            //-------------for custom dialog part ------------//
            val customDialog = layoutInflater.inflate(R.layout.custom_notify_alert, null)
            val dialog = Dialog(requireContext())
            dialog.setContentView(customDialog)
            val addUpdate=dialog.findViewById<TextView>(R.id.addUpdate)
            val time=dialog.findViewById<TextView>(R.id.notifyTimeDialog)
            val Note=dialog.findViewById<TextInputEditText>(R.id.NoteData)
            /*
                Here about the time calculation and time selection from the user
                and set the reminder for after 1 days periodic reminder
            */
            time.setOnClickListener {
                val currentTime = Calendar.getInstance()
                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    { _: TimePicker, hourOfDay: Int, minute: Int ->

                        val selectedTime = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                            set(Calendar.MINUTE, minute)
                        }
                        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                        userTime = timeFormat.format(selectedTime.time)
                        time.text=userTime
                        timeDelay = selectedTime.timeInMillis - currentTime.timeInMillis
                        if (timeDelay <= 0) {
                            selectedTime.add(Calendar.DAY_OF_MONTH, 1)
                            timeDelay = selectedTime.timeInMillis - currentTime.timeInMillis
                        }
                    },
                    currentTime.get(Calendar.HOUR_OF_DAY),
                    currentTime.get(Calendar.MINUTE),
                    false
                )
                timePickerDialog.show()
            }
            /*
               Here need to add the notify item in the database and also notify the listview adapter about the
               item added and also create the alarm manager and call the broadcast receiver
            */
            addUpdate.setOnClickListener {
                //-------here the alarm  manager part-------//
                val notifyData=notifyData(1,"","","")
                var id=(-1).toLong()
                notifyData.note=Note.text.toString()
                notifyData.time= userTime
                if(Note.text.toString().trim()!="") {
                    //-----------------for Database store----------------------//
                    id=db.addNotifyItem(notifyData)
                    notifyData.id=id.toInt()
                    dataList.add(notifyData)
                    adapter.notifyDataSetChanged()
                    //---------------------for the work manager and notification manager ---------------//
                    if (time.text!="Set Time") {
                        val constraints = Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                            .setRequiresCharging(false)
                            .setRequiresBatteryNotLow(false)
                            .build()
//                        val workRequest = PeriodicWorkRequest.Builder(MyWorker::class.java, 1, TimeUnit.DAYS)
//                                .setConstraints(constraints)
//                                .addTag(id.toString())
//                                .setInputData(workDataOf("noteData" to Note.text.toString()))
//                                .setInitialDelay(timeDelay, TimeUnit.MILLISECONDS)
//                                .build()
//                        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(id.toString(),ExistingPeriodicWorkPolicy.KEEP,workRequest)

                        //-----new code here-----//
                        val workManager = WorkManager.getInstance(requireContext())
                        val workRequest = OneTimeWorkRequestBuilder<ScheduleNotificationWorker>()
                            .setInitialDelay(1,TimeUnit.MINUTES)
                            .setConstraints(constraints)
                            .build()
                        workManager.enqueue(workRequest)
                        //-----------------------//
                    }
                    //-----------------for completing task  ----------------------//
                    userTime="Set Time"
                    Toast.makeText(requireContext(), "Remind Note Added", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
            //------------------------this part declare about the custom dialog box to add the notify item-------------------//
            val layParams=WindowManager.LayoutParams()
            layParams.copyFrom(dialog.window?.attributes)
            layParams.width=WindowManager.LayoutParams.MATCH_PARENT
            layParams.height=WindowManager.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes=layParams
            dialog.show()
            //----------------------------------------------------------------------------------------------------------------//
        }
        return binding.root
    }
    //-----------------------------------------------------Other method here--------------------------------------------------//
}