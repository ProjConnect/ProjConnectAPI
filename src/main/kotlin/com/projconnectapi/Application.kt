package com.projconnectapi

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import com.projconnectapi.plugins.configureOAuth
import com.projconnectapi.plugins.configureRouting
import com.projconnectapi.plugins.configureSerialization
import com.projconnectapi.plugins.configureSessions
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.LoggerFactory

fun main() {
    // Logging
    val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    val rootLogger: Logger = loggerContext.getLogger("org.mongodb.driver")
    rootLogger.level = Level.OFF

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureSerialization()
        configureOAuth()
        configureRouting()
        configureSessions()
    }.start(wait = true)
}
