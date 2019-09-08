package com.nubis.utils

import io.ktor.http.*

object Validator {

    //Returns null if valid and error String if Invalid
    fun validateInput(vararg requiredFields:String,params: Parameters):String?{
        var error:String? = null
        var isValid = true
        requiredFields.forEach { requiredField ->
            if(params[requiredField] == null){
                isValid = false
                if(error == null){ error = "$requiredField is missing"}else{error += ", $requiredField is missing"}
            }
        }
        return error
    }
}