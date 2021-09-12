package com.projconnectapi.plugins

import com.projconnectapi.models.postStorage
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing

fun Application.configureRouting() {

    routing {
        get("/status") {
            call.response.status(HttpStatusCode.OK)
        }

        get("/post") {
            if (postStorage.isNotEmpty()){
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
            val post =
                postStorage.find { it.postId == id } ?: return@get call.respondText(
                    "No post with id $id",
                    status = HttpStatusCode.NotFound
                )
            call.respond(post)
        }

        get("/user/{id}") {
            call.response.status(HttpStatusCode.OK)
        }
    }
}
