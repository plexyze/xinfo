package com.plexyze.xinfo.model

import com.plexyze.xinfo.R


fun String.isDirPath():Boolean{
    if(isEmpty()){
        return true
    }
    return last() == '/'
}

fun String.validateFileName():List<Int>{
    val errors = mutableListOf<Int>()
    if(isEmpty()){
        errors.add(R.string.not_filled)
    }
    val regex = Regex("""[/:*?"'«≤>|»\\]""")
    if(contains(regex)){
        errors.add(R.string.invalid_file_name_characters)
    }
    if(length>128){
        errors.add(R.string.max_size_128_chars)
    }
    return errors
}

fun String.validatePassword():List<Int>{
    val errors = mutableListOf<Int>()
    if(length<8){
        errors.add(R.string.less_than_8_characters)
    }
    if(equals(toUpperCase())){
        errors.add(R.string.must_contain_lowercase_characters)
    }
    if(equals(toLowerCase())){
        errors.add(R.string.must_contain_uppercase_characters)
    }
    val regex = Regex("[0-9]")
    if(!contains(regex)){
        errors.add(R.string.must_contain_numbers)
    }

    return errors
}