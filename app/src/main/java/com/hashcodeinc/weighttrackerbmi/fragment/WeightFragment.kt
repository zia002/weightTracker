package com.hashcodeinc.weighttrackerbmi.fragment

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.hashcodeinc.weighttrackerbmi.R
import com.hashcodeinc.weighttrackerbmi.database.MyData
import com.hashcodeinc.weighttrackerbmi.database.weightDatabase
import com.hashcodeinc.weighttrackerbmi.databinding.FragmentWeightBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class TipsAdapter(private val context: Context,private val dataList:ArrayList<String>):BaseAdapter(){
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
        var convertView=ConvertView
        if(convertView==null){
            val inflater=LayoutInflater.from(context)
            convertView=inflater.inflate(R.layout.custom_tips,parent,false)
        }
        val txt=convertView?.findViewById<TextView>(R.id.txtTips)
        txt?.text=dataList[position]
        val colorList = MyData.initColorList(context)
        if(position<10) txt?.setBackgroundColor(colorList[position])
        else txt?.setBackgroundColor(colorList[position% colorList.size])
        return convertView
    }
}

class WeightFragment : Fragment() {
    private lateinit var binding:FragmentWeightBinding
    var extra:String="kg"
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding=FragmentWeightBinding.inflate(inflater,container,false)
        val listView=binding.weightTips
        //--------------------getting the value--------------------//
        val tmpStr=context?.resources?.getStringArray(R.array.weight_gain)
        val stringGain= ArrayList<String>()
        tmpStr?.let {
            stringGain.addAll(it.toList())
        }
        val adapter1=TipsAdapter(requireContext(),stringGain)
        listView.adapter=adapter1
        val tmpStr2=context?.resources?.getStringArray(R.array.weight_loss)
        val stringLoss=ArrayList<String>()
        tmpStr2?.let {
            stringLoss.addAll(it.toList())
        }
        val adapter2=TipsAdapter(requireContext(),stringLoss)
        //--------------------to set the tips ---------------------//
        binding.weightGain.setOnClickListener {
            binding.tipsImg.visibility=View.GONE
            binding.weightTips.visibility=View.VISIBLE
            listView.adapter=adapter1
            adapter1.notifyDataSetChanged()
            adapter2.notifyDataSetChanged()
        }
        binding.weightLoss.setOnClickListener {
            binding.tipsImg.visibility=View.GONE
            binding.weightTips.visibility=View.VISIBLE
            listView.adapter=adapter2
            adapter1.notifyDataSetChanged()
            adapter2.notifyDataSetChanged()
        }
        //---------------------------------------------------------//
        binding.weightspinner.onItemSelectedListener=object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                val weightType = binding.weightspinner.selectedItem.toString()
                when(weightType){
                    "Kg"->{
                        binding.TextInputLayout.hint="kg"
                        extra=" kg"
                    }
                    "Pound"->{
                        binding.TextInputLayout.hint="Pound"
                        extra=" lb"
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        //--------------------for date month calculation---------------//
        val currentDate = LocalDate.now()
        val date = currentDate.dayOfMonth
        val monthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.getDefault())
        val month = currentDate.format(monthFormatter)
        //--------------------database related task--------------------//
        val db=weightDatabase(requireContext())
        binding.addWeight.setOnClickListener {
            val value=binding.weightInput.text
            if(value?.isNotEmpty() == true) {
                val id=db.insertWeightData(value.toString()+extra, date.toString(), month)
                Toast.makeText(requireContext(),"Weight Added",Toast.LENGTH_SHORT).show()
                //---------for share preference data---------------//
                val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
                val formattedDate = currentDate.format(formatter)
                MyData.addData(requireContext(),"LAST_ADD",formattedDate.toString())
                MyData.addData(requireContext(),"CUR_WEIGHT",value.toString())
                binding.weightInput.text=null
                val keyBoard=requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyBoard.hideSoftInputFromWindow(it.windowToken,0)
            }
            else{
                Toast.makeText(requireContext(),"Empty",Toast.LENGTH_SHORT).show()
            }
        }
        binding.reload.setOnClickListener {
            if(binding.weightInput.text!=null){
                binding.weightInput.text=null
            }
        }
        return binding.root
    }
}