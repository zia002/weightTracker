package com.hashcodeinc.weighttrackerbmi.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.hashcodeinc.weighttrackerbmi.R
import com.hashcodeinc.weighttrackerbmi.database.BMIDatabase
import com.hashcodeinc.weighttrackerbmi.database.bmiData
import com.hashcodeinc.weighttrackerbmi.database.notifyData
import com.hashcodeinc.weighttrackerbmi.databinding.FragmentBmiFinalBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.hashcodeinc.weighttrackerbmi.database.MyData

class myAdapterDataShowBMI(val txtView:TextView,val context: Context, val datalist:ArrayList<bmiData>): BaseAdapter() {
    val curBMI= MyData.getData(context,"CUR-BMI")
    override fun getCount(): Int {
        return datalist.size
    }
    override fun getItem(position: Int): Any {
       return datalist[position]
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, ConvertView: View?, parent: ViewGroup?): View? {
        var convertView = ConvertView
        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.custom_bmi_item, parent, false)
        }
        val bmiValue = convertView?.findViewById<TextView>(R.id.bmiValue)
        val bmiType = convertView?.findViewById<ImageView>(R.id.bmiType)
        val bmiDate = convertView?.findViewById<TextView>(R.id.bmiDate)
        val bmiMonth = convertView?.findViewById<TextView>(R.id.bmiMonth)
        val gap=convertView?.findViewById<TextView>(R.id.gap)
        //---------------------To set the value gap and condition of the current data--------//
        if(position<datalist.size-1 && curBMI!="No Data"){
            val thisBMIValue=datalist[position].bmiValue.toFloat()
            val prevBMIValue=datalist[position+1].bmiValue.toFloat()
            val gapValueFormat= thisBMIValue-prevBMIValue
            val gapValue= String.format("%.2f",gapValueFormat)
            gap?.text= gapValue
            if(thisBMIValue>=prevBMIValue && thisBMIValue<=24.9) bmiType?.setImageResource(R.drawable.up_green)
            else if(thisBMIValue<=prevBMIValue && thisBMIValue<=24.9 && thisBMIValue>=18.5) bmiType?.setImageResource(R.drawable.down_green)
            else if(thisBMIValue>=prevBMIValue && thisBMIValue>24.9) bmiType?.setImageResource(R.drawable.up_red)
            else if(thisBMIValue<=prevBMIValue && thisBMIValue<18.5) bmiType?.setImageResource(R.drawable.down_red)
        }
        else{
            if(datalist[position].bmiValue.toFloat()<=24.5) bmiType?.setImageResource(R.drawable.up_green)
            else bmiType?.setImageResource(R.drawable.up_red)
            gap?.text="0.00"
        }
        //---------------------To perform the delete item-----------------------//
        val bmiDel = convertView?.findViewById<ImageView>(R.id.bmiDelete)
        bmiDel?.setOnClickListener {
            val db = BMIDatabase(context)
            db.deleteBMIData(datalist[position].id.toInt())
            datalist.removeAt(position)
            if(datalist.size!=0) {
                val valueBMI=datalist[0].bmiValue
                txtView.text="Current BMI: $valueBMI"
                MyData.addData(context,"CUR-BMI",valueBMI)
            }
            else {
                txtView.text="Current BMI: No Data"
                MyData.addData(context,"CUR-BMI","No Data")
            }
            notifyDataSetChanged()
            Toast.makeText(context,"BMI Data Deleted",Toast.LENGTH_SHORT).show()
        }
        //-----------------------other component which are set by the other element-----------------//
        bmiValue?.text = datalist[position].bmiValue
        bmiDate?.text = datalist[position].date
        bmiMonth?.text = datalist[position].month
        return convertView
    }
}
class BMIFinalFragment(resultFragment: ResultFragment) : Fragment() {
    private lateinit var binding:FragmentBmiFinalBinding
    var selectedNow=1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentBmiFinalBinding.inflate(inflater,container,false)
        val curBMITxt=binding.resultCurrentBMI
        val db=BMIDatabase(requireContext())
        val dataList=db.showBMIData()
        dataList.reverse()
        val curBMI=MyData.getData(requireContext(),"CUR-BMI")
        if(dataList.size==0) {
            binding.listView.visibility=View.INVISIBLE
            binding.lineGraphView.visibility=View.INVISIBLE
            binding.empty2.visibility=View.VISIBLE
        }
        else {
            binding.listView.visibility=View.VISIBLE
            binding.lineGraphView.visibility=View.INVISIBLE
            binding.empty2.visibility=View.INVISIBLE
        }
        binding.resultCurrentBMI.text = "Current BMI:$curBMI"
        //--------------------------Custom adapter set up in the list view initially----------------//
        val adapter=myAdapterDataShowBMI(curBMITxt,requireContext(),dataList)
        binding.listView.adapter=adapter
        //-------------------------custom set graph view or the history view-------------//
        binding.resultType.setOnClickListener {
            //---------to show the graph view--------//
            if(selectedNow==1){
               binding.resultType.setImageResource(R.drawable.type_histroy)
                selectedNow=2
                if(dataList.size==0) {
                    binding.listView.visibility=View.INVISIBLE
                    binding.lineGraphView.visibility=View.INVISIBLE
                    binding.empty2.visibility=View.VISIBLE
                }
                else{
                    binding.listView.visibility=View.INVISIBLE
                    binding.lineGraphView.visibility=View.VISIBLE
                    binding.empty2.visibility=View.INVISIBLE
                }
                //---------set the List View data---------//
                val entries = arrayListOf<Entry>()
                var count=1f
                for (i in dataList.size - 1 downTo 0) {
                    val data = dataList[i]
                    val bmiValue = data.bmiValue.toFloat()
                    val entry = Entry(count, bmiValue)
                    entries.add(entry)
                    count++
                }
                //------------to customize it's design-------------//
                val xaxis=binding.lineGraphView.axisRight
                val yaxis=binding.lineGraphView.axisLeft
                val dataSet = LineDataSet(entries, "BMI Data")
                dataSet.color = Color.BLUE
                dataSet.setCircleColor(Color.RED)
                dataSet.valueTextColor = Color.BLACK
                dataSet.valueTextSize=15f
                dataSet.valueTypeface=Typeface.DEFAULT_BOLD
                dataSet.valueTextColor=Color.BLACK
                dataSet.setCircleColor(R.color.app)
                val lineData = LineData(dataSet)
                binding.lineGraphView.data = lineData
                binding.lineGraphView.description.isEnabled = false
                binding.lineGraphView.setTouchEnabled(true)
                binding.lineGraphView.isDragEnabled = true
                binding.lineGraphView.setScaleEnabled(true)
                binding.lineGraphView.setPinchZoom(true)
                binding.lineGraphView.invalidate()
                yaxis.apply {
                    textSize=18f
                    textColor=Color.BLUE
                    typeface=Typeface.MONOSPACE
                }
                //----------------------------------------//
            }
            //----------to show the history view-------//
            else if(selectedNow==2){
                val adapter2=myAdapterDataShowBMI(curBMITxt,requireContext(),dataList)
                binding.listView.adapter=adapter2
                binding.resultType.setImageResource(R.drawable.type_graph)
                selectedNow=1
                if(dataList.size==0) {
                    binding.listView.visibility=View.INVISIBLE
                    binding.lineGraphView.visibility=View.INVISIBLE
                    binding.empty2.visibility=View.VISIBLE
                }
                else{
                    binding.listView.visibility=View.VISIBLE
                    binding.lineGraphView.visibility=View.INVISIBLE
                    binding.empty2.visibility=View.INVISIBLE
                }
                //---------set the List View data---------//
                binding.listView.adapter=adapter
                //----------------------------------------//
            }
        }
        return binding.root
    }
}