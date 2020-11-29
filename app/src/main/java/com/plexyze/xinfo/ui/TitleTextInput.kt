package com.plexyze.xinfo.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.plexyze.xinfo.R
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


/**
 * TODO: document your custom view class.
 */
class TitleTextInput : ConstraintLayout {

    private lateinit var titleView: TextView
    private lateinit var show: TextView
    private lateinit var editText: EditText
    private lateinit var btnCopy: Button

    var inputType
        get() = editText.inputType
        set(v){ editText.inputType = v}

    var title:String
        get() = titleView.text.toString()
        set(v){titleView.text = v}

    var text:String
        get() = show.text.toString()
        set(v){
            if(show.text != v){
                show.text = v
                editText.setText(v)
            }
        }

    var eventChange:(String)->Unit={}

    var isEnabled:Boolean?
        get() = editText.isEnabled
        set(v){
            if(v?:false){
                editText.isEnabled = true
                btnCopy.visibility = View.INVISIBLE
                show.visibility = View.INVISIBLE
            }else{
                editText.isEnabled = false
                btnCopy.visibility = View.VISIBLE
                btnCopy.setOnClickListener(){
                    copyText(show.text.toString())
                    showHidePassword()
                }
            }}

    var isCopy:Boolean = false
    var isShowPassword:Boolean = false




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
        inflater.inflate(R.layout.title_text_input, this)
        titleView = findViewById(R.id.TitleTextInput_title)
        show = findViewById(R.id.TitleTextInput_show)
        editText = findViewById(R.id.TitleTextInput_text)
        btnCopy = findViewById(R.id.TitleTextInput_btnCopy)
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                s?.let {
                    val str = s.toString()
                    show.text = str
                    eventChange(str)
                }
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        initComponent()


        val a = context.obtainStyledAttributes(
            attrs, R.styleable.TitleTextInput, defStyle, 0
        )

        a.getInt(R.styleable.TitleTextInput_android_inputType, 0).let{
            if(it != 0){
                inputType = it
            }
        }

        a.getString(R.styleable.TitleTextInput_title)?.let{
            title = it
        }

        a.getString(R.styleable.TitleTextInput_text)?.let{
            text = it
        }

        isEnabled = a.getBoolean(R.styleable.TitleTextInput_enabled, false)

        isCopy = a.getBoolean(R.styleable.TitleTextInput_copy, false)

        isShowPassword = a.getBoolean(R.styleable.TitleTextInput_showPassword, false)

        a.recycle()
    }

    private fun showHidePassword() {
        if(!isShowPassword){
            show.visibility = View.INVISIBLE
            return
        }
        if(show.visibility == View.INVISIBLE){
            show.visibility = View.VISIBLE
        }else{
            show.visibility = View.INVISIBLE
        }
    }

    private fun copyText(copiedText: String) {
        if(!isCopy){
            return
        }
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("TAG", copiedText)
        clipboard.setPrimaryClip(clip)

        val text = "Copy:$copiedText"
        val duration = Toast.LENGTH_SHORT

        Toast.makeText(context, text, duration).show()
    }
}
