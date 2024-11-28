package com.example.tipcalculator

import android.animation.ArgbEvaluator
import android.icu.util.Currency
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.tipcalculator.ui.theme.TipCalculatorTheme

private const val TAG = "MainActivity"
class MainActivity : ComponentActivity() {

    private lateinit var etBaseAmount: EditText
    private lateinit var seekBar: SeekBar
    private lateinit var tvTipPrecentLabel: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvadjective: TextView
    private lateinit var currencyspinner: Spinner
    private var currency = "$"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etBaseAmount = findViewById(R.id.etbaseamount)
        seekBar = findViewById(R.id.seekBar)
        tvTipPrecentLabel = findViewById(R.id.tippercentage)
        tvTipAmount = findViewById(R.id.tipamount)
        tvTotalAmount = findViewById(R.id.textView8)
        tvadjective = findViewById(R.id.feedback)
        currencyspinner = findViewById(R.id.spinner)


        seekBar.max = 30
        seekBar.progress = 15
        updateTextandColor(15)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                Log.i(TAG,"Progress Updated to $p1")
                tvTipPrecentLabel.text = p1.toString() + "%"
                computeTipAndTotal()
                updateTextandColor(p1)
                if(currency.isEmpty())
                {
                    Toast.makeText(this@MainActivity,"Enter the Currency",Toast.LENGTH_SHORT).show()
                    return
                }
                updatecurrency(currency)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
        etBaseAmount.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.i(TAG,"Before Text changed $p0")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.i(TAG,"On Text changed $p0")
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.i(TAG,"After Text changed $p0")
                computeTipAndTotal()
                if(currency.isEmpty())
                {
                    Toast.makeText(this@MainActivity,"Enter the Currency",Toast.LENGTH_SHORT).show()
                    return
                }
                updatecurrency(currency)
            }
        })
        val currencies = resources.getTextArray(R.array.currency)

        var adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item,currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        currencyspinner.adapter = adapter

        currencyspinner.setSelection(1)

        currencyspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currency = currencies[position].toString()
                updatecurrency(currency)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(this@MainActivity,"Select your Currency",Toast.LENGTH_SHORT).show()
            }


        }



    }

    private fun updatecurrency(currency: String) {

     if(!("$" in tvTipAmount.text.toString() || "£" in tvTipAmount.text.toString() || "€" in tvTipAmount.text.toString() || "₹" in tvTipAmount.text.toString() ) ){
        tvTipAmount.text = "$currency"+tvTipAmount.text.toString()
         tvTotalAmount.text = "$currency"+tvTotalAmount.text.toString()
     }
     else if("$" in tvTipAmount.text.toString()){
         tvTipAmount.text = tvTipAmount.text.toString().replace("$","$currency")
         tvTotalAmount.text = tvTotalAmount.text.toString().replace("$","$currency")
     }
     else if("£" in tvTipAmount.text.toString()){
         tvTipAmount.text = tvTipAmount.text.toString().replace("£","$currency")
         tvTotalAmount.text = tvTotalAmount.text.toString().replace("£","$currency")

     }
     else if("€" in tvTipAmount.text.toString()){
         tvTipAmount.text = tvTipAmount.text.toString().replace("€","$currency")
         tvTotalAmount.text = tvTotalAmount.text.toString().replace("€","$currency")

     }
     else if("₹" in tvTipAmount.text.toString()){
         tvTipAmount.text = tvTipAmount.text.toString().replace("₹","$currency")
         tvTotalAmount.text = tvTotalAmount.text.toString().replace("₹","$currency")
     }


    }

    private fun updateTextandColor(tipPercent: Int) {

        var tipdescription = when(tipPercent) {
            in 0..6 -> "Poor"
            in 7..12 -> "Bad"
            in 13..18 -> "Acceptable"
            in 19..24 -> "Good"
            else -> "Amazing"}
        tvadjective.text = tipdescription

        val color =ArgbEvaluator().evaluate(
            tipPercent.toFloat()/seekBar.max,
            ContextCompat.getColor(this,R.color.worst_tip),
            ContextCompat.getColor(this,R.color.Best_tip))as Int
        tvadjective.setTextColor(color)
    }

    private fun computeTipAndTotal() {

        if(etBaseAmount.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }
        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tippercent = seekBar.progress
        Log.i(TAG,"Reached Compute")
        val tipamount = (tippercent * baseAmount)/100
        val totalamount = baseAmount + tipamount

        tvTipAmount.text = "%.2f".format(tipamount)
        tvTotalAmount.text = "%.2f".format(totalamount)

    }
}

