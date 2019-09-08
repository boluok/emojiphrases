package com.nubis.routes

import API_VERSION
import com.nubis.model.*
import com.nubis.repository.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.locations.*
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val PHRASES = "$API_VERSION/phrases"


@Location(PHRASES)
class Phrases

fun Route.phrases(db:EmojiPhrasesRepository){
    authenticate ("auth"){
        get<Phrases>{
             val user = call.authentication.principal as User
            call.respond(db.phrases())
        }
        post<Phrases>{
            val params = call.receive<EmojiPhrase>()
            db.add("",params.emoji,params.phrase)
            call.respond("Successfull")
            }

        }



}