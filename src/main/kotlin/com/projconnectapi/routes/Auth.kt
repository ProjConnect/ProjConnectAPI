package com.projconnectapi.routes

import com.projconnectapi.schemas.UserSession
import io.ktor.application.call
import io.ktor.auth.OAuthAccessTokenResponse
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set

fun Route.oauthRoute() {
    authenticate("auth-oauth-google") {
        get("/login") {
            // Redirects to 'authorizeUrl' automatically
        }

        get("/callback") {
            val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
            principal?.accessToken.toString()
            call.sessions.set(UserSession(principal?.accessToken.toString()))
        }
    }
}
