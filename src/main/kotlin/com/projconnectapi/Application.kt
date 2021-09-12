package com.projconnectapi

import com.projconnectapi.plugins.configureRouting
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.serialization.json

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(ContentNegotiation){
            json()
        }
        configureRouting()
    }.start(wait = true)
}
