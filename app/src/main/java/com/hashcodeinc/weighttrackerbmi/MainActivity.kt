package com.hashcodeinc.weighttrackerbmi

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.hashcodeinc.weighttrackerbmi.databinding.ActivityMainBinding
import com.hashcodeinc.weighttrackerbmi.fragment.BMIFragment
import com.hashcodeinc.weighttrackerbmi.fragment.HomeFragment
import com.hashcodeinc.weighttrackerbmi.fragment.ReminderFragment
import com.hashcodeinc.weighttrackerbmi.fragment.ResultFragment
import com.hashcodeinc.weighttrackerbmi.fragment.WeightFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var fragmentManager: FragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fragmentManager=supportFragmentManager
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //-----------------------------To set the initial notification,if from notification or direct from app--------------------//
        if(intent.hasExtra("IS_IT_NOTIFICATION")){
            fragmentManager.beginTransaction().replace(R.id.fragmentHolder, ReminderFragment()).commit()
            binding.reminder.setBackgroundResource(R.drawable.custom_border)
        }
        else {
            fragmentManager.beginTransaction().replace(R.id.fragmentHolder, HomeFragment()).commit()
            binding.home.setBackgroundResource(R.drawable.custom_border)
        }
        //---------------------------click event to navigate for different fragment------------------------------//
        binding.bmi.setOnClickListener {
            onClickChange()
            binding.bmi.setBackgroundResource(R.drawable.custom_border)
            showFragment(BMIFragment())
        }
        binding.home.setOnClickListener {
            onClickChange()
            binding.home.setBackgroundResource(R.drawable.custom_border)
            showFragment(HomeFragment())
        }
        binding.allResult.setOnClickListener {
            onClickChange()
            binding.allResult.setBackgroundResource(R.drawable.custom_border)
            showFragment(ResultFragment())
        }
        binding.weight.setOnClickListener {
            onClickChange()
            binding.weight.setBackgroundResource(R.drawable.custom_border)
            showFragment(WeightFragment())
        }
        binding.reminder.setOnClickListener {
            onClickChange()
            binding.reminder.setBackgroundResource(R.drawable.custom_border)
            showFragment(ReminderFragment())
        }
    }
    fun onClickChange(){
        binding.home.setBackgroundResource(android.R.color.transparent)
        binding.allResult.setBackgroundResource(android.R.color.transparent)
        binding.reminder.setBackgroundResource(android.R.color.transparent)
        binding.weight.setBackgroundResource(android.R.color.transparent)
        binding.bmi.setBackgroundResource(android.R.color.transparent)
    }
    private fun showFragment(fragment:Fragment){
        binding.progressBar.visibility= View.VISIBLE
        val currentFragment = fragmentManager.findFragmentById(binding.fragmentHolder.id)
        if (currentFragment != null) {
            currentFragment.view?.visibility = View.GONE
        }
        fragmentManager.beginTransaction().replace(R.id.fragmentHolder,fragment).commit()
        binding.root.post {
            binding.progressBar.visibility = View.GONE
        }
    }
}