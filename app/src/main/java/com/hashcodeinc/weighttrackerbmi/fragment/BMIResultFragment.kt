package com.hashcodeinc.weighttrackerbmi.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.hashcodeinc.weighttrackerbmi.R
import com.hashcodeinc.weighttrackerbmi.database.BMIDatabase
import com.hashcodeinc.weighttrackerbmi.database.MyData
import com.hashcodeinc.weighttrackerbmi.databinding.FragmentBmiResultBinding
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

//-------------------------------custom adapter to set the recommendation text------------------------//
class MyAdapter(private val fragment: BMIResultFragment, private val myDataList: MutableList<String>): BaseAdapter() {
    override fun getCount(): Int {
        return myDataList.size
    }
    override fun getItem(position: Int): Any {
        return myDataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertview = convertView
        if (convertview == null) {
            val inflater = LayoutInflater.from(fragment.requireContext())
            convertview = inflater.inflate(R.layout.custom_list, parent, false)
        }
        val textId = convertview?.findViewById<TextView>(R.id.txt)
        textId?.text = myDataList[position]
        return convertview
    }
}
//-----------------------------------BMI result fragment start from here----------------------------------//

class BMIResultFragment : Fragment() {
    private lateinit var binding: FragmentBmiResultBinding
    private lateinit var indicator: ImageView
    private lateinit var db:BMIDatabase
    //--------------to get the context of the fragment------------//
    override fun onAttach(context: Context) {
        super.onAttach(context)
        db= BMIDatabase(requireActivity())
    }
    //--------------fragment other task start  here---------------//
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ConstraintLayout {
        binding = FragmentBmiResultBinding.inflate(inflater, container, false)
        //--------------------- start fragment work here-------------------//
        val BMI = arguments?.getString("BMI")
        val tmp= BMI?.toDouble()
        val df = DecimalFormat("#.##")
        df.maximumFractionDigits = 2
        val value = df.format(tmp).toDouble()
        binding.result.text = value.toString()
        //----------------------Database related task----------------------//
        binding.addBMI.setOnClickListener {
            val currentDate = LocalDate.now()
            val date = currentDate.dayOfMonth
            val monthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.getDefault())
            val month = currentDate.format(monthFormatter)
            //---------for share preference data---------------//
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
            val formattedDate = currentDate.format(formatter)
            MyData.addData(requireContext(),"LAST_ADD",formattedDate.toString())
            MyData.addData(requireContext(),"CUR-BMI",value.toString())
            //---------------------other---------------------//
            Toast.makeText(requireContext(),"BMI Value Added",Toast.LENGTH_SHORT).show()
            db.insertBMIData(value.toString(), date.toString(), month)
            db.close()
        }
        //-------------------Result---------------------//
        if (value < 18.5) {
            if(value<=5){
                binding.condition.text = "Severe Thinness"
                binding.resultIndicator.setImageResource(R.drawable.bmi_0)
            }
            else if(value<=16){
                binding.condition.text = "Severe Thinness"
                binding.resultIndicator.setImageResource(R.drawable.bmi_1_to_9)
            }
            else if(value<=17){
                binding.condition.text = "Moderate Thinness"
                binding.resultIndicator.setImageResource(R.drawable.bmi_10_to_18)
            }
            else {
                binding.condition.text = "Mild Thinness"
                binding.resultIndicator.setImageResource(R.drawable.bmi_18)
            }
            binding.result.setTextColor(resources.getColor(R.color.first))
            binding.condition.setTextColor(resources.getColor(R.color.first))

            //---------------to add the recommendation value in the list View---------------//
            val tmpstr=this.resources.getStringArray(R.array.first_bmi)
            val stringList= mutableListOf<String>()
            stringList.addAll(tmpstr)
            binding.listViewRecomendation.adapter= MyAdapter(this,stringList)
        }
            else if(value>=18.5 && value<=24.9){
                binding.condition.text="Normal"
                binding.result.setTextColor(resources.getColor(R.color.second))
                binding.condition.setTextColor(resources.getColor(R.color.second))
                if(value<=20) binding.resultIndicator.setImageResource(R.drawable.bmi_18_to_20)
                else if(value<=22) binding.resultIndicator.setImageResource(R.drawable.bmi_21_to_22)
                else if(value<=25) binding.resultIndicator.setImageResource(R.drawable.bmi_22_to_24)
                //---------------to add the recommendation value in the list View---------------//
                val tmpstr=this.resources.getStringArray(R.array.second_bmi)
                var stringList= mutableListOf<String>()
                stringList.addAll(tmpstr)
                binding.listViewRecomendation.adapter= MyAdapter(this,stringList)
            }
            else if(value>=25.0 && value<=29.9){
                binding.condition.text="Over Weight"
                binding.result.setTextColor(resources.getColor(R.color.third))
                binding.condition.setTextColor(resources.getColor(R.color.third))
                if(value<=27) binding.resultIndicator.setImageResource(R.drawable.bmi_25_to_27)
                else binding.resultIndicator.setImageResource(R.drawable.bmi_27_29)
                //---------------to add the recommendation value in the list View---------------//
                val tmpstr=this.resources.getStringArray(R.array.third_bmi)
                val stringList= mutableListOf<String>()
                stringList.addAll(tmpstr)
                binding.listViewRecomendation.adapter= MyAdapter(this,stringList)
            }
            else if(value>=30.0 && value<=39.9){
                if(value<=34.9) {
                    binding.condition.text = "Obese-1"
                    binding.resultIndicator.setImageResource(R.drawable.bmi_30_to_35)
                    //---------------to add the recommendation value in the list View---------------//
                    val tmp=this.resources.getStringArray(R.array.forth_bmi)
                    val stringList= mutableListOf<String>()
                    stringList.addAll(tmp)
                    binding.listViewRecomendation.adapter= MyAdapter(this,stringList)
                }
                else{
                    binding.condition.text = "Obese-2"
                    binding.resultIndicator.setImageResource(R.drawable.bmi_35_40)
                    //---------------to add the recommendation value in the list View---------------//
                    val tmpstr=this.resources.getStringArray(R.array.fifth_bmi)
                    val stringList= mutableListOf<String>()
                    stringList.addAll(tmpstr)
                    binding.listViewRecomendation.adapter= MyAdapter(this,stringList)
                }
                binding.result.setTextColor(resources.getColor(R.color.forth))
                binding.condition.setTextColor(resources.getColor(R.color.forth))
            }
            else if(value>=40.0){
                if(value<=60) binding.resultIndicator.setImageResource(R.drawable.bmi_40_to_60)
                else if(value<=100) binding.resultIndicator.setImageResource(R.drawable.bmi_60_to_100)
                else binding.resultIndicator.setImageResource(R.drawable.bmi_max)
                binding.condition.text="Severely Obese"
                binding.result.setTextColor(resources.getColor(R.color.fifth))
                binding.condition.setTextColor(resources.getColor(R.color.fifth))
                //---------------to add the recommendation value in the list View---------------//
                val tmp=this.resources.getStringArray(R.array.sixth_bmi)
                val stringList= mutableListOf<String>()
                stringList.addAll(tmp)
                binding.listViewRecomendation.adapter= MyAdapter(this,stringList)
            }
        return binding.root
    }
}