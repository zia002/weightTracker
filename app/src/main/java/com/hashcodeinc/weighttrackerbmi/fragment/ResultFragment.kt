package com.hashcodeinc.weighttrackerbmi.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.tabs.TabLayout
import com.hashcodeinc.weighttrackerbmi.R
import com.hashcodeinc.weighttrackerbmi.databinding.FragmentResultBinding
//-------------------this fragment is for all over the result------------------------//
class ResultFragment : Fragment() {
    private lateinit var binding:FragmentResultBinding
    private lateinit var fragmentManager: FragmentManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentResultBinding.inflate(inflater,container,false)
        fragmentManager=parentFragmentManager
        fragmentManager.beginTransaction().replace(R.id.resultViewContainer,BMIFinalFragment(ResultFragment())).commit()
        binding.resultTab.addTab(binding.resultTab.newTab().setText("BMI").setIcon(R.drawable.bmi_svg))
        binding.resultTab.addTab(binding.resultTab.newTab().setText("Weight").setIcon(R.drawable.weight_svg))
        binding.resultTab.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(p0: TabLayout.Tab?) {
               when(p0?.text){
                   "BMI"->{ //fragmentManager.beginTransaction().replace(R.id.resultViewContainer,BMIFinalFragment(ResultFragment())).commit()
                       showFragment(BMIFinalFragment(ResultFragment()))
                   }
                   "Weight"->{ //fragmentManager.beginTransaction().replace(R.id.resultViewContainer,WeightFinalFragment(ResultFragment())).commit()
                       showFragment(WeightFinalFragment(ResultFragment()))
                   }
               }
            }
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabReselected(p0: TabLayout.Tab?) {}
        })
        return binding.root
    }
    private fun showFragment(fragment:Fragment){
        binding.progressBarResult.visibility= View.VISIBLE
        val currentFragment = fragmentManager.findFragmentById(binding.resultViewContainer.id)
        if (currentFragment != null) {
            currentFragment.view?.visibility = View.GONE
        }
        fragmentManager.beginTransaction().replace(R.id.resultViewContainer,fragment).commit()
        binding.root.post {
            binding.progressBarResult.visibility = View.GONE
        }
    }
}