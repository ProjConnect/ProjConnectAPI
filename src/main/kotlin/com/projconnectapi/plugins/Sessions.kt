package com.projconnectapi.plugins

import com.projconnectapi.schemas.UserSession
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie

fun Application.configureSessions() {
    install(Sessions) {
        cookie<UserSession>("user_session")
    }
}
