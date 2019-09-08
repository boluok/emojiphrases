package com.nubis

import api.*
import com.apple.eawt.*
import com.nubis.api.*
import com.nubis.model.*
import com.nubis.model.EmojiPhrases.phrase
import com.nubis.repository.*
import com.nubis.routes.*
import com.ryanharter.ktor.moshi.*
import freemarker.cache.*
import hash
import io.ktor.application.*
import io.ktor.application.Application
import io.ktor.auth.*
import io.ktor.auth.Authentication.Feature.install
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import java.net.*
import java.text.*
import java.util.concurrent.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(DefaultHeaders)

    install(StatusPages) {
        exception<Throwable> { e ->
            call.respondText(e.localizedMessage,
                ContentType.Text.Plain, HttpStatusCode.InternalServerError)
        }
    }

    install(ContentNegotiation) {
        gson()
    }

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    install(Locations)

//    install(Sessions) {
//        cookie<EPSession>("SESSION") {
//            transform(SessionTransportTransformerMessageAuthentication(hashKey))
//        }
//    }

    val hashFunction = { s: String -> hash(s) }

    DatabaseFactory.init()

    val db = EmojiPhrasesRepository()
    val jwtService = JwtService()

    install(Authentication) {
        jwt("jwt") {
            verifier(jwtService.verifier)
            realm = "emojiphrases app"
            validate {
                val payload = it.payload
                val claim = payload.getClaim("id")
                val claimString = claim.asString()
                val user = db.userById(claimString)
                user
            }
        }
    }

    routing {
        static("/static") {
            resources("images")
        }

//        home(db)
//        about(db)
       // phrases(db, hashFunction)
//        signin(db, hashFunction)
//        signout()
//        signup(db, hashFunction)

        // API
        login(db, jwtService)
        phrasesApi(db)
    }
}

const val API_VERSION = "/api/v1"

suspend fun ApplicationCall.redirect(location: Any) {
    respondRedirect(application.locations.href(location))
}

fun ApplicationCall.verifyCode(date: Long, user: User, code: String, hashFunction: (String) -> String) =
    securityCode(date, user, hashFunction) == code
            && (System.currentTimeMillis() - date).let { it > 0 && it < TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS) }

fun ApplicationCall.securityCode(date: Long, user: User, hashFunction: (String) -> String) =
    hashFunction("$date:${user.userId}:${request.host()}:${refererHost()}")

fun ApplicationCall.refererHost() = request.header(HttpHeaders.Referrer)?.let { URI.create(it).host }

