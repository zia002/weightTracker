package com.hashcodeinc.weighttrackerbmi.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.google.android.material.textfield.TextInputEditText
import com.hashcodeinc.weighttrackerbmi.R
import com.hashcodeinc.weighttrackerbmi.database.BMIDatabase
import com.hashcodeinc.weighttrackerbmi.database.MyData
import com.hashcodeinc.weighttrackerbmi.database.weightDatabase
import com.hashcodeinc.weighttrackerbmi.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding
    private lateinit var fragmentManager:FragmentManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentHomeBinding.inflate(inflater,container,false)
        fragmentManager=requireActivity().supportFragmentManager
        //--------------to set the value from shared preference-------------//
        var targetWeight=MyData.getData(requireContext(),"TARGET_WEIGHT")
        val curWeight=MyData.getData(requireContext(),"CUR_WEIGHT")
        val curBMI=MyData.getData(requireContext(),"CUR-BMI")
        val lastAdd=MyData.getData(requireContext(),"LAST_ADD")
        //-----set the target weight-----//
        var existText=binding.targetWeight.text
        if(targetWeight=="No Data") targetWeight="Set Target"
        var newTxt="$existText$targetWeight"
        binding.targetWeight.text=newTxt
        //-----set the current weight-----//
        existText=binding.currentWeight.text
        newTxt="$existText$curWeight"
        binding.currentWeight.text=newTxt
        //-----set the current BMI-----//
        existText=binding.currentBMI.text
        newTxt="$existText$curBMI"
        binding.currentBMI.text=newTxt
        //-----set the last update-----//
        existText=binding.lastAdd.text
        newTxt="$existText$lastAdd"
        binding.lastAdd.text=newTxt
        //-----set the condition-----//
        if(curBMI!="No Data") {
            binding.conditionImg.setImageResource(R.drawable.bmi_1)
            if (curBMI?.toFloat()!! < 18.5) {
                if (curBMI.toFloat() <= 5) {
//                    binding.condition e.text = "Health Condition:\nSevere Thinness"
                    binding.conditionHoome.text =getText(R.string.HF1)
                }
                else if (curBMI.toFloat() <= 16) {
                    //binding.conditionHoome.text = "Health Condition:\nSevere Thinness"
                    binding.conditionHoome.text =getText(R.string.HF2)
                }
                else if (curBMI.toFloat() <= 17) {
                    //binding.conditionHoome.text = "Health Condition:\nModerate Thinness"
                    binding.conditionHoome.text =getText(R.string.HF3)
                }
                else {
                    binding.conditionHoome.text =getText(R.string.HF4)
//                    binding.conditionHoome.text = "Health Condition:\nMild Thinness"
                }
            }
            else if(curBMI.toFloat()>=18.5 && curBMI.toFloat()<=24.9) {
                binding.conditionImg.setImageResource(R.drawable.bmi_2)
                //binding.conditionHoome.text="Health Condition:\nNormal"
                binding.conditionHoome.text =getText(R.string.HF5)
            }
            else if(curBMI.toFloat()>=25.0 && curBMI.toFloat()<=29.9) {
                binding.conditionImg.setImageResource(R.drawable.bmi_3)
                //binding.conditionHoome.text="Health Condition:\nOver Weight"
                binding.conditionHoome.text =getText(R.string.HF6)
            }
            else if(curBMI.toFloat()>=30.0 && curBMI.toFloat()<=39.9){
                if(curBMI.toFloat()<=34.9) {
                    binding.conditionImg.setImageResource(R.drawable.bmi_4)
                    //binding.conditionHoome.text = "Health Condition:\nObese-1"
                    binding.conditionHoome.text =getText(R.string.HF7)
                }
                else {
                    binding.conditionImg.setImageResource(R.drawable.bmi_5)
                    //binding.conditionHoome.text = "Health Condition:\nObese-2"
                    binding.conditionHoome.text =getText(R.string.HF8)
                }
            }
            else if(curBMI.toFloat()>=40.0) {
                binding.conditionImg.setImageResource(R.drawable.bmi_6)
                //binding.conditionHoome.text="Health Condition:\nSeverely Obese"
                binding.conditionHoome.text =getText(R.string.HF9)
            }
        }
        else {
            binding.conditionImg.setImageResource(R.drawable.bmi_2)
            //binding.conditionHoome.text="Health Condition:\nNo Data"
            binding.conditionHoome.text =getText(R.string.HF10)
        }
        //--------to set target weight value---------//
        binding.targetWeight.setOnClickListener {
            val customDialog = layoutInflater.inflate(R.layout.target_weight, null)
            val dialog = Dialog(requireContext())
            dialog.setContentView(customDialog)
            val inputTarget = dialog.findViewById<TextInputEditText>(R.id.targetWeightIp)
            val addTarget = dialog.findViewById<TextView>(R.id.addTarget)
            val layParams = WindowManager.LayoutParams()
            layParams.copyFrom(dialog.window?.attributes)
            layParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layParams
            dialog.show()
            addTarget.setOnClickListener {
                val target=inputTarget.text.toString().trim()
                if (target.isNotEmpty()) {
                    val targetVal = inputTarget.text.toString()
                    val keyboard=requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    MyData.addData(requireContext(), "TARGET_WEIGHT", targetVal)
                    Toast.makeText(requireContext(), "Target Added", Toast.LENGTH_SHORT).show()
                    binding.targetWeight.text="Target \nWeight:$targetVal"
                    val view = activity?.currentFocus
                    keyboard.hideSoftInputFromWindow(view?.windowToken, 0)
                    dialog.dismiss()
                } else Toast.makeText(requireContext(), "Empty Target", Toast.LENGTH_SHORT).show()
            }
        }
        binding.navigationImg.setOnClickListener {
            binding.drawerLayout.open()
            binding.drawerNavi.setNavigationItemSelectedListener {menuItem->
                when(menuItem.itemId){
                    R.id.aboutApp->{
                        true
                    }
                    R.id.privacyPolicy->{
                        //-------------link of the google site privacy policy---------//
                        val url="https://sites.google.com/view/hash-code-wt-privacy-policy/home"
                        val uri= Uri.parse(url)
                        val intent=Intent(Intent.ACTION_VIEW,uri)
                        startActivity(intent)
                        true
                    }
                    R.id.share->{
                        //---------------This is the app link----------------//
                        val url="https://play.google.com/store/apps?hl=en&gl=US"
                        val intent=Intent(Intent.ACTION_SEND)
                        intent.type="text/plain"
                        intent.putExtra("Share This",url)
                        val chooser=Intent.createChooser(intent,"Share Using..")
                        startActivity(chooser)
                        true
                    }
                    R.id.rateUs->{
                        //--------------This is app rating providing link-----------//
                        val url="https://play.google.com/store/apps/details?id=com.android.chrome&hl=en&gl=US"
                        val uri= Uri.parse(url)
                        val intent=Intent(Intent.ACTION_VIEW,uri)
                        startActivity(intent)
                        true
                    }
                    R.id.deleteData->{
                        binding.drawerLayout.close()
                        //-----------showing the dialog in the screen--------------//
                        val customDialog = layoutInflater.inflate(R.layout.custom_delete_dialog, null)
                        val dialog = Dialog(requireContext())
                        dialog.setContentView(customDialog)
                        val layParams = WindowManager.LayoutParams()
                        layParams.copyFrom(dialog.window?.attributes)
                        layParams.width = WindowManager.LayoutParams.MATCH_PARENT
                        layParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                        dialog.window?.attributes = layParams
                        dialog.show()
                        //----------- finding the ID------------//
                        val weight=dialog.findViewById<RadioButton>(R.id.weightDia)
                        val bmi=dialog.findViewById<RadioButton>(R.id.bmiDia)
                        val seekBar=dialog.findViewById<SeekBar>(R.id.seekBar)
                        val days=dialog.findViewById<TextView>(R.id.textViewDays)
                        val deleteBtn=dialog.findViewById<TextView>(R.id.deleteDataDia)
                        var limit:Int=0
                        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                            override fun onProgressChanged(seekBar: SeekBar?,progress: Int, fromUser: Boolean) {
                                days.text=progress.toString()
                                limit=progress
                            }
                            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                            }
                            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                            }
                        })
                        deleteBtn.setOnClickListener {
                            if(weight.isChecked){
                                val db=weightDatabase(requireContext())
                                db.DelWeightLimit(limit)
                                Toast.makeText(requireContext(),"Weight $limit Data Deleted",Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            else if (bmi.isChecked){
                                val db=BMIDatabase(requireContext())
                                db.DelBMILimit(limit)
                                Toast.makeText(requireContext(),"BMI $limit Data Deleted",Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                        }
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }
        return binding.root
    }

}