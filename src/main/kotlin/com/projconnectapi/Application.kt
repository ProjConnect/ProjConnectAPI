package com.projconnectapi

import com.projconnectapi.plugins.configureOAuth
import com.projconnectapi.plugins.configureRouting
import com.projconnectapi.plugins.configureSerialization
import com.projconnectapi.plugins.configureSessions
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureSerialization()
        configureOAuth()
        configureRouting()
        configureSessions()
    }.start(wait = true)
}
