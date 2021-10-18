package com.projconnectapi.routes

import com.projconnectapi.clients.database
import com.projconnectapi.clients.oauthClient
import com.projconnectapi.models.User
import com.projconnectapi.schemas.UserSession
import io.ktor.application.call
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection

fun Route.userRoute() {
    get("/search/user/{id}") {
        call.respondText("Functionality not implemented!", status = HttpStatusCode.OK)
    }

    get("/user_profile") {
        val userSession: UserSession? = call.sessions.get<UserSession>()
        if (userSession != null) {
            val userInfo: UserInfo = oauthClient.get("https://www.googleapis.com/oauth2/v2/userinfo") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer ${userSession.token}")
                }
            }
            val userProfile: User? = database.getCollection<User>().findOne(User::email eq userInfo.email)
            if (userProfile != null) {
                call.respond(userProfile)
            } else {
                call.respond(
                    User(
                        "",
                        userInfo.email,
                        false,
                        userInfo.name,
                        userInfo.email,
                        "",
                        0.0f,
                        listOf<String>(),
                        listOf<String>(),
                        listOf<String>(),
                        "",
                        ""
                    )
                )
            }
            call.respondText("Hello, ${userInfo.name}! Your email is ${userInfo.email}")
        } else {
            call.respondRedirect("/login")
        }
    }

    post("/sign-up") {
        var user = call.receive<User>()
        val userSession: UserSession? = call.sessions.get<UserSession>()
        if (userSession != null) {
            val userInfo: UserInfo = oauthClient.get("https://www.googleapis.com/oauth2/v2/userinfo") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer ${userSession.token}")
                }
            }
            if (user.username == "" ||
                database.getCollection<User>()
                    .findOne(User::username eq user.username) != null
            ) {
                call.respondText(
                    "username is empty or already exists",
                    status = HttpStatusCode.BadRequest
                )
            } else if (user.email != userInfo.email) {
                call.respondText(
                    "email account doesn't match",
                    status = HttpStatusCode.BadRequest
                )
            } else if (user.isModerator) {
                call.respondText(
                    "action not permitted - raise privilege to moderator",
                    status = HttpStatusCode.BadRequest
                )
            } else {
                user.score = 0.0f
                user.history = listOf()
                database.getCollection<User>().insertOne(user)
            }
        } else {
            call.respondRedirect("/login")
        }
    }
}

data class UserInfo(
    val id: String,
    val email: String,
    val verified_email: Boolean,
    val name: String,
    val given_name: String,
    val family_name: String,
    val picture: String,
    val locale: String,
    val hd: String,
)
