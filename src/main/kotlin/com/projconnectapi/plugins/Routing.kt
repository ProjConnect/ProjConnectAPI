package com.projconnectapi.plugins

import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureRouting() {

    routing {
        get("/") {
            call.response.status(HttpStatusCode.OK)
        }

        get("/post") {
            call.response.status(HttpStatusCode.OK)
        }

        get("/post/{id}") {
            call.response.status(HttpStatusCode.OK)
        }

        get("/user/{id}") {
            call.response.status(HttpStatusCode.OK)
        }
    }
}
