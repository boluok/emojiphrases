package api



import com.nubis.*
import com.nubis.repository.*
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route


const val PHRASE_API_ENDPOINT = "$API_VERSION/phrases"

@Location(PHRASE_API_ENDPOINT)
class PhrasesApi

fun Route.phrasesApi(db: Repository) {

    authenticate("jwt") {
        get<PhrasesApi> {
            call.respond(db.phrases())
        }
    }
}