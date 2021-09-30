package com.projconnectapi

import com.fasterxml.jackson.databind.SerializationFeature
import com.projconnectapi.plugins.configureOAuth
import com.projconnectapi.plugins.configureRouting
import com.projconnectapi.plugins.configureSessions
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.litote.kmongo.id.jackson.IdJacksonModule

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(ContentNegotiation) {
            jackson {
                enable(SerializationFeature.INDENT_OUTPUT)
                registerModule(IdJacksonModule())
            }
        }
        configureOAuth()
        configureRouting()
        configureSessions()
    }.start(wait = true)
}
