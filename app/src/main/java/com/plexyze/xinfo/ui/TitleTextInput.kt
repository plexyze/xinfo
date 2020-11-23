package com.plexyze.xinfo.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.plexyze.xinfo.R


/**
 * TODO: document your custom view class.
 */
class TitleTextInput : ConstraintLayout {

    private var isError = false
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
        get() = editText.text.toString()
        set(v){
            editText.setText(v)
            show.setText(v)
        }

    var isEnabled:Boolean?
        get() = editText.isFocusable
        set(v){
            if(v?:false){
                editText.isEnabled = true
                btnCopy.visibility = View.INVISIBLE
                show.visibility = View.INVISIBLE
            }else{
                editText.isEnabled = false
                btnCopy.visibility = View.VISIBLE
                btnCopy.setOnClickListener(){
                    copyText(editText.text.toString())
                    showHidePassword()
                }
            }}

    var markError:Boolean?
        get() = isError
        set(v){ isError = v?:false}


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

    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        initComponent()


        val a = context.obtainStyledAttributes(
            attrs, R.styleable.TitleTextInput, defStyle, 0
        )

        a.getInt(R.styleable.TitleTextInput_android_inputType,0).let{
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

        markError = a.getBoolean(R.styleable.TitleTextInput_markError, false)

        a.recycle()
    }

    private fun showHidePassword() {
        if(show.visibility == View.INVISIBLE){
            show.visibility = View.VISIBLE
        }else{
            show.visibility = View.INVISIBLE
        }
    }

    private fun copyText(copiedText: String) {
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("TAG", copiedText)
        clipboard.setPrimaryClip(clip)

        val text = "Copy:$copiedText"
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(context, text, duration)
        toast.show()
    }
}