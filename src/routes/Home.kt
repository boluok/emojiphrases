package com.nubis.routes

import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.locations.get
import io.ktor.response.*
import io.ktor.routing.*


const val HOME = "/"

@Location(HOME)
class Home

fun Route.home(){
    get<Home>{
        call.respondText("Hello Bolu")
    }
}

