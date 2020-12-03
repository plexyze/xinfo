package com.plexyze.xinfo.model


fun String.isDirPath():Boolean{
    if(isEmpty()){
        return true
    }
    return last() == '/'
}