package com.projconnectapi.plugins

import com.projconnectapi.dbactions.database
import com.projconnectapi.models.Post
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import org.bson.types.ObjectId
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection

fun Application.configureRouting() {

    routing {
        get("/status") {
            call.response.status(HttpStatusCode.OK)
        }

        get("/post") {
            val postStorage = database.getCollection<Post>().find().toList()
            if (postStorage.isNotEmpty()) {
                call.respond(postStorage)
            } else {
                call.respondText("No post found", status = HttpStatusCode.NotFound)
            }
        }

        get("/post/{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            val post = database.getCollection<Post>().findOneById(ObjectId(id))
            if (post != null) {
                call.respond(post)
            } else {
                call.respondText("No post found", status = HttpStatusCode.NotFound)
            }
        }

        get("/user/{id}") {
            call.response.status(HttpStatusCode.OK)
        }
    }
}
