package com.plexyze.xinfo.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
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

    private lateinit var titleView: TextView
    private lateinit var editText: EditText
    private lateinit var btnCopy: Button

    private var lastType = InputType.TYPE_CLASS_TEXT
    private var show = false
    private var edit = ""

    var inputType
        get() = lastType
        set(v){
            show = false
            lastType = InputType.TYPE_CLASS_TEXT or v
            editText.inputType = lastType}

    var title:String
        get() = titleView.text.toString()
        set(v){titleView.text = v}

    var text:String
        get() = edit
        set(v){
            if(edit != v){
                edit = v
                editText.setText(v)
            }
        }

    var eventChange: (String)->Unit={}

    var isEnabled:Boolean?
        get() = editText.isEnabled
        set(v){
            if(v == true){
                show = false
                editText.inputType = lastType
                editText.isEnabled = true
                btnCopy.visibility = View.INVISIBLE
            }else{
                editText.isEnabled = false
                btnCopy.visibility = View.VISIBLE
                btnCopy.setOnClickListener(){
                    copyText()
                    showHidePassword()
                }
            }
        }


    var isCopy:Boolean = false

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
        editText = findViewById(R.id.TitleTextInput_text)
        btnCopy = findViewById(R.id.TitleTextInput_btnCopy)
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                s?.let {
                    val str = s.toString()
                    edit = str
                    eventChange(str)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        editText.setOnFocusChangeListener(){ view, hasF ->
            if(!hasF){
                val imm: InputMethodManager = view.context.getSystemService(
                    Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        initComponent()
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.TitleField, defStyle, 0
        )

        a.getInt(R.styleable.TitleField_android_inputType, 0).let{
            if(it != 0){
                inputType = it
            }else{
                inputType = InputType.TYPE_CLASS_TEXT
            }
        }

        a.getString(R.styleable.TitleField_title)?.let{
            title = it
        }

        a.getString(R.styleable.TitleField_text)?.let{
            text = it
        }

        isEnabled = a.getBoolean(R.styleable.TitleField_enabled, false)

        isCopy = a.getBoolean(R.styleable.TitleField_copy, false)

        a.recycle()
    }

    private fun showHidePassword() {
        if(show){
            show = false
            editText.inputType = lastType
        }else{
            show = true
            editText.inputType = lastType and InputType.TYPE_TEXT_VARIATION_PASSWORD.inv()
        }
    }

    private fun copyText() {
        if(!isCopy){
            return
        }
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("TAG", edit)
        clipboard.setPrimaryClip(clip)
        val text = "Copy: $edit"
        val duration = Toast.LENGTH_SHORT
        Toast.makeText(context, text, duration).show()
    }
}
