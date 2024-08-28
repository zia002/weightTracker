package com.hashcodeinc.weighttrackerbmi.fragment

import android.content.Context
import android.graphics.Color
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
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.hashcodeinc.weighttrackerbmi.R
import com.hashcodeinc.weighttrackerbmi.database.BMIDatabase
import com.hashcodeinc.weighttrackerbmi.database.MyData
import com.hashcodeinc.weighttrackerbmi.database.bmiData
import com.hashcodeinc.weighttrackerbmi.database.weightData
import com.hashcodeinc.weighttrackerbmi.database.weightDatabase
import com.hashcodeinc.weighttrackerbmi.databinding.FragmentWeightFinalBinding
class myAdapterDataShowWeight(val txtView:TextView,val context: Context, val datalist:ArrayList<weightData>): BaseAdapter() {
    var targetWeight=MyData.getData(context,"TARGET_WEIGHT")
    override fun getCount(): Int {
        return datalist.size
    }
    override fun getItem(position: Int): Any {
        return datalist[position]
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int,ConvertView: View?, parent: ViewGroup?): View? {
        var convertView=ConvertView
        if(convertView==null){
            val inflater=LayoutInflater.from(context)
            convertView=inflater.inflate(R.layout.custom_bmi_item,parent,false)
        }
        val bmiValue=convertView?.findViewById<TextView>(R.id.bmiValue)
        val bmiType=convertView?.findViewById<ImageView>(R.id.bmiType)
        val gap=convertView?.findViewById<TextView>(R.id.gap)
        val bmiDate=convertView?.findViewById<TextView>(R.id.bmiDate)
        val bmiMonth=convertView?.findViewById<TextView>(R.id.bmiMonth)
        val bmiDelete=convertView?.findViewById<ImageView>(R.id.bmiDelete)
        bmiValue?.text=datalist[position].weight
        bmiDate?.text=datalist[position].date
        bmiMonth?.text=datalist[position].month
        if( position<datalist.size-1){

            //------------------to set the indicator of weight change-------------//
            val input = datalist[position].weight
            val regex = Regex("\\d+\\.?\\d*")
            val matchResult = regex.find(input)
            val curWeight = matchResult?.value?.toFloat()
            val input2=datalist[position+1].weight
            //val regex2=Regex("\\d+")
            val regex2 = Regex("\\d+\\.?\\d*")
            val matchResult2=regex2.find(input2)
            val prevWeight=matchResult2?.value?.toFloat()
            val gapFormat= curWeight!! - prevWeight!!
            val gapValue= String.format("%.2f",gapFormat)
            gap?.text=gapValue
            if(targetWeight!="No Data") {
                if (curWeight == targetWeight?.toFloat()) bmiType?.setImageResource(R.drawable.equal)
                else if (curWeight >= prevWeight) {
                    /*
                  current weight is increasing
                  if current weight is <= target then it's good increase:up_green
                  if current weight>= target then it's bad increase:up_red
                */
                    if (curWeight < targetWeight?.toFloat()!!) bmiType?.setImageResource(R.drawable.up_green)
                    else bmiType?.setImageResource(R.drawable.up_red)
                } else {
                    /*
                 current weight is decreasing
                 if current weight is <= target then it's bad decrease:down_red
                 if current weight>= target then it's good decreasing:down_green
               */
                    if (curWeight < targetWeight?.toFloat()!!) bmiType?.setImageResource(R.drawable.down_red)
                    else bmiType?.setImageResource(R.drawable.down_green)
                }
            }
        }
        else if(position==datalist.size-1){
            gap?.text="0.0"
            if(targetWeight!="No Data"){
                val input = datalist[position].weight
                val regex = Regex("\\d+\\.?\\d*")
                val matchResult = regex.find(input)
                val curWeight = matchResult?.value?.toFloat()
                if (curWeight!! > targetWeight?.toFloat()!!){
                    bmiType?.setImageResource(R.drawable.up_red)
                }
                else if(curWeight <targetWeight?.toFloat()!!) bmiType?.setImageResource(R.drawable.down_red)
                else  bmiType?.setImageResource(R.drawable.equal)
            }
        }
        //------------------to delete the data from datalist and sqlite------//
        bmiDelete?.setOnClickListener {
            val db = weightDatabase(context)
            db.deleteWeightData(datalist[position].id.toInt())
            datalist.removeAt(position)
            notifyDataSetChanged()
            if(datalist.size!=0){
                val curWeight=datalist[0].weight
                txtView.text="Current\nWeight:$curWeight"
                MyData.addData(context,"CUR_WEIGHT",curWeight)
            }
            else{
                MyData.addData(context,"CUR_WEIGHT","No Data")
                txtView.text="Current\nWeight:No Data"

            }
            Toast.makeText(context,"Weight Data Deleted",Toast.LENGTH_SHORT).show()
        }
        return convertView
    }
}
class WeightFinalFragment(resultFragment: ResultFragment) : Fragment() {
    private lateinit var binding:FragmentWeightFinalBinding
    var selectedNow=1
    var done=0
    var done2=0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentWeightFinalBinding.inflate(inflater,container,false)
        val targetWeight=MyData.getData(requireContext(),"TARGET_WEIGHT")
        binding.resultTargetWeight.text="Target \nWeight: $targetWeight"
        val curWeight=binding.resultCurrentWeight
        val db= weightDatabase(requireContext())
        val dataList=db.showWeightData()
        dataList.reverse()
        if(dataList.size>=1) {
            binding.resultCurrentWeight.text = binding.resultCurrentWeight.text.toString() + dataList[0].weight
        }
        else  {
            binding.empty1.visibility=View.VISIBLE
            binding.Weightgraph.visibility=View.INVISIBLE
            binding.showWeightData.visibility=View.INVISIBLE
            binding.resultCurrentWeight.text ="Current \nWeight: No Data"
        }
        binding.showWeightData.adapter=myAdapterDataShowWeight(curWeight,requireContext(),dataList)
        binding.resultType.setOnClickListener {
            //---------to show the graph view--------//
            if(selectedNow==1){
                binding.resultType.setImageResource(R.drawable.type_histroy)
                selectedNow=2
                if(dataList.size==0){
                    binding.empty1.visibility=View.VISIBLE
                    binding.Weightgraph.visibility=View.INVISIBLE
                    binding.showWeightData.visibility=View.INVISIBLE
                }
                else{
                    binding.empty1.visibility=View.INVISIBLE
                    binding.Weightgraph.visibility=View.VISIBLE
                    binding.showWeightData.visibility=View.INVISIBLE
                }
                //---------set the List View data---------//
                val entries = arrayListOf<Entry>()
                var count=1f
                for (i in dataList.size - 1 downTo 0) {
                    val input = dataList[i].weight
                    val regex = Regex("\\d+\\.?\\d*")
                    val matchResult = regex.find(input)
                    val number = matchResult?.value?.toFloatOrNull() ?: 0
                    val entry = Entry(count, number.toFloat())
                    entries.add(entry)
                    count++
                }
                val dataSet = LineDataSet(entries, "Weight Data")
                dataSet.color = Color.BLUE
                dataSet.setCircleColor(Color.RED)
                dataSet.valueTextColor = Color.BLACK
                dataSet.valueTextSize=18f
                dataSet.setCircleColor(R.color.app)
                val lineData = LineData(dataSet)
                binding.Weightgraph.data = lineData
                binding.Weightgraph.description.isEnabled = false
                binding.Weightgraph.setTouchEnabled(true)
                binding.Weightgraph.isDragEnabled = true
                binding.Weightgraph.setScaleEnabled(true)
                binding.Weightgraph.setPinchZoom(true)
                binding.Weightgraph.invalidate()
                //----------------------------------------//
            }
            //----------to show the history view-------//
            else if(selectedNow==2){
                binding.resultType.setImageResource(R.drawable.type_graph)
                selectedNow=1
                if(dataList.size==0){
                    binding.empty1.visibility=View.VISIBLE
                    binding.Weightgraph.visibility=View.INVISIBLE
                    binding.showWeightData.visibility=View.INVISIBLE
                }
                else{
                    binding.empty1.visibility=View.INVISIBLE
                    binding.Weightgraph.visibility=View.INVISIBLE
                    binding.showWeightData.visibility=View.VISIBLE
                }
                //---------set the List View data---------//
                binding.showWeightData.adapter=myAdapterDataShowWeight(curWeight,requireContext(),dataList)
                //----------------------------------------//
            }
        }
        return binding.root
    }

}