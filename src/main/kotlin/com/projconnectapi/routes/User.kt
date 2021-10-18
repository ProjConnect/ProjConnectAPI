package com.projconnectapi.routes

import com.projconnectapi.clients.database
import com.projconnectapi.models.Post
import com.projconnectapi.models.User
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.*
import io.ktor.routing.Route
import io.ktor.routing.get
import org.bson.types.ObjectId
import org.litote.kmongo.*

fun Route.userRoute() {
    get("/search/user/{id}") {
        call.respondText("Functionality not implemented!", status = HttpStatusCode.OK)
    }

    get("/search/user/{userName}") {
        val userName = call.parameters["userName"] ?: return@get call.respondText(
            "Missing or malformed userName",
            status = HttpStatusCode.BadRequest
        )
        val user = database.getCollection<User>().findOne(User::username eq userName)
        if (user != null) {
            call.respond(user)
        } else {
            call.respondText("No user found", status = HttpStatusCode.NotFound)
        }
    }
}
