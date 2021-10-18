package com.projconnectapi.routes

import com.projconnectapi.schemas.UserSession
import io.ktor.application.call
import io.ktor.auth.OAuthAccessTokenResponse
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.sessions.sessions

fun Route.oauthRoute() {
    authenticate("auth-oauth-google") {
        get("/login") {
            // Redirects to 'authorizeUrl' automatically
        }

        get("/callback") {
            val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
            call.sessions.set("user_session", UserSession(principal?.accessToken.toString(), principal?.extraParameters?.get("id_token").toString()))
            call.respondRedirect("${System.getenv("CLIENT_URL") ?: ""}/project/list")
        }
    }
    get("/logout") {
        call.sessions.clear("user_session")
    }
}
