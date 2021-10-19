package com.projconnectapi.routes

import com.projconnectapi.clients.tokenVerifier
import com.projconnectapi.clients.utils.getUser
import com.projconnectapi.models.User
import com.projconnectapi.schemas.UserSession
import io.ktor.application.call
import io.ktor.auth.OAuthAccessTokenResponse
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import org.litote.kmongo.eq

fun Route.oauthRoute() {
    authenticate("auth-oauth-google") {
        get("/login") {
            // Redirects to 'authorizeUrl' automatically
        }

        get("/callback") {
            val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
            call.sessions.set("user_session", UserSession(principal?.accessToken.toString(), principal?.extraParameters?.get("id_token").toString()))
            // Check if user exists in database
            // if it exists, then redirect to project page
            // else redirects to edit page.
            val userSession: UserSession? = call.sessions.get<UserSession>()
            if (userSession != null) {
                val email = tokenVerifier.verify(userSession.idToken).payload["email"].toString()
                val userProfile: User? = getUser(User::email eq email)
                if (userProfile != null) {
                    call.respondRedirect("${System.getenv("CLIENT_URL") ?: ""}/project/list")
                } else {
                    call.respondRedirect("${System.getenv("CLIENT_URL") ?: ""}/profile/edit")
                }
            }
        }
    }
    get("/logout") {
        call.sessions.clear("user_session")
        call.respond(HttpStatusCode.OK)
    }
}
