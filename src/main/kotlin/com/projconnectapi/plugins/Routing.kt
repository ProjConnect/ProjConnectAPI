package com.projconnectapi.plugins

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.routing.get
import io.ktor.routing.routing

fun Application.configureRouting() {

    routing {
        get("/status") {
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
