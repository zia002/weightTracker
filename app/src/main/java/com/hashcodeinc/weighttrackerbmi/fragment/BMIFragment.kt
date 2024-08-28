package com.hashcodeinc.weighttrackerbmi.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.hashcodeinc.weighttrackerbmi.R
import com.hashcodeinc.weighttrackerbmi.databinding.FragmentBmiBinding

//---------------------this fragment is for BMI input taking and calculation-----------------//
class BMIFragment : Fragment() {
    private lateinit var binding:FragmentBmiBinding
    private lateinit var fragmentManager: FragmentManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentBmiBinding.inflate(inflater,container,false)
        val view=binding.root
        fragmentManager=parentFragmentManager
        //---------------------Get the value--------------------------//
        var WEIGHT:Double?=0.0
        var HEIGHT:Double?=0.0
        var INCH:Double?
        var BMI:Double?=0.0
        var weightType:String=""
        var heightType:String=""
        //--------------------Control the spinner value---------------//
        binding.weightSpinner.onItemSelectedListener=object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                weightType=binding.weightSpinner.selectedItem.toString()
                when(weightType){
                    "Kg"->{
                        binding.textInputLayout.hint="Kg"
                    }
                    "Pound"->{
                        binding.textInputLayout.hint="Pound"
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.heightSpinner.onItemSelectedListener=object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                heightType = binding.heightSpinner.selectedItem.toString()
                when(heightType){
                    "Meter"->{
                        binding.textInputLayout2.hint="Meter"
                        binding.heightInput2.visibility=View.INVISIBLE
                        binding.inchInput.visibility=View.INVISIBLE
                    }
                    "Inch"->{
                        binding.textInputLayout2.hint="Inch"
                        binding.heightInput2.visibility=View.INVISIBLE
                        binding.inchInput.visibility=View.INVISIBLE
                    }
                    else->{
                        binding.textInputLayout2.hint="Feet"
                        binding.heightInput2.visibility=View.VISIBLE
                        binding.inchInput.visibility=View.VISIBLE
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        //-----------------------Reload the value--------------------------//
        binding.reload.setOnClickListener{
            binding.weightInput.text=null
            binding.heightInput1.text=null
            binding.heightInput2.text=null
        }
        //-----------------------Action on the Calculate Button----------------//
        binding.calculate.setOnClickListener {
            //-----------------Get input---------------------//
            val weight= (binding.weightInput.text).toString()
            val height=binding.heightInput1.text.toString()
            val inch=binding.heightInput2.text.toString()

            //------------------Calculate the value------------------//
            WEIGHT = if(weightType!="Kg") weight.toDoubleOrNull()?.times(0.454)
            else weight.toDoubleOrNull()
            if(heightType=="Inch") HEIGHT=(height.toDoubleOrNull())?.times(0.0254)
            else if(heightType=="Feet-Inch"){
                HEIGHT=(height.toDoubleOrNull())?.times(0.305)
                INCH=(inch.toDoubleOrNull())?.times(0.0254)
                HEIGHT = INCH?.let { it1 -> HEIGHT?.plus(it1) }
            }
            else HEIGHT=height.toDoubleOrNull()
            if(HEIGHT!=0.0 && WEIGHT!=0.0 && HEIGHT!=null && WEIGHT!=null){
                BMI= (WEIGHT)?.div((HEIGHT?.times(HEIGHT!!)!!))
                val bundle=Bundle().apply {
                    putString("BMI",BMI.toString())
                }
                val bmiResultFragment= BMIResultFragment()
                bmiResultFragment.arguments=bundle
                fragmentManager.beginTransaction().replace(R.id.fragmentHolder,bmiResultFragment).addToBackStack(null).commit()
            }
            else {
                Toast.makeText(context,"Fill The Input", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }
}