package com.plexyze.xinfo.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.plexyze.xinfo.R


/**
 * TODO: document your custom view class.
 */
class TitleSpinner : ConstraintLayout {

    private lateinit var titleView: TextView
    private lateinit var spinner: Spinner
    private lateinit var adapter:ArrayAdapter<String>
    private val adapterOptions = mutableListOf<String>()
    private var lastSelect = ""

    var options
        set(v){
            adapterOptions.clear()
            adapterOptions.addAll(v)
            adapter.notifyDataSetChanged()
        }
        get() = adapterOptions.toList()

    var title:String
        get() = titleView.text.toString()
        set(v){titleView.text = v}

    var text:String
        get() {
            val pos = spinner.selectedItemPosition
            if(pos == AdapterView.INVALID_POSITION) {
                return ""
            }
            return adapterOptions[pos]
        }
        set(v){
            val pos = adapterOptions.indexOf(v)
            if(pos != -1){
                spinner.setSelection(pos)
            }
        }

    var eventChange: (String)->Unit={}

    var isEnabled:Boolean?
        get() = spinner.isEnabled
        set(v){
            spinner.isEnabled = v?:false}

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun initComponent() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.title_spinner, this)
        titleView = findViewById(R.id.title)
        spinner = findViewById(R.id.spinner)
        adapter = ArrayAdapter(context, R.layout.spiner_row, R.id.item, adapterOptions)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View,
                pos: Int, id: Long ) {
                if(lastSelect != text){
                    lastSelect = text
                    eventChange(lastSelect)
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        initComponent()

        val a = context.obtainStyledAttributes(
            attrs, R.styleable.TitleField, defStyle, 0
        )


        a.getString(R.styleable.TitleField_title)?.let{
            title = it
        }

        a.getString(R.styleable.TitleField_text)?.let{
            text = it
        }

        isEnabled = a.getBoolean(R.styleable.TitleField_enabled, false)

        a.recycle()
    }
}
