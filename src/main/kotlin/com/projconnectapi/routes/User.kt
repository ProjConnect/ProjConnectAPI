package com.projconnectapi.routes

import com.projconnectapi.clients.database
import com.projconnectapi.clients.tokenVerifier
import com.projconnectapi.models.User
import com.projconnectapi.schemas.UserSession
import io.ktor.application.call
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

fun isUserSafe(user: User, googleAcc: String, newUser: Boolean): Boolean {
    return if (newUser) {
        user.username != "" &&
            database.getCollection<User>()
            .findOne(User::username eq user.username) == null &&
            user.email == googleAcc &&
            !user.isModerator
    } else {
        val userInDB: User? = database.getCollection<User>()
            .findOne(User::username eq user.username)
        userInDB != null &&
            user.username == userInDB.username &&
            user.email == userInDB.email &&
            (!user.isModerator || user.isModerator && userInDB.isModerator)
    }
}

fun ifSafeThenInsert(user: User, googleAcc: String): Boolean {
    return if (isUserSafe(user, googleAcc, true)) {
        user.score = 0.0f
        user.history = listOf()
        database.getCollection<User>().insertOne(user)
        true
    } else {
        false
    }
}

fun Route.userRoute() {
    get("/search/user/{id}") {
        call.respondText("Functionality not implemented!", status = HttpStatusCode.OK)
    }

    get("/my_profile") {
        val userSession: UserSession? = call.sessions.get<UserSession>()
        if (userSession != null) {
            val email = tokenVerifier.verify(userSession.idToken).payload["email"]
            val userProfile: User? = database.getCollection<User>().findOne(User::email eq email)
            if (userProfile != null) {
                call.respond(userProfile)
            } else {
                call.respond(
                    User(
                        "",
                        email as String,
                        false,
                        "",
                        "",
                        email,
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
        } else {
            call.respondRedirect("/login")
        }
    }

    post("/sign-up") {
        var user = call.receive<User>()
        val userSession: UserSession? = call.sessions.get<UserSession>()
        if (userSession != null) {
            val email = tokenVerifier.verify(userSession.idToken).payload["email"]
            val inserted = ifSafeThenInsert(user, email as String)
            if (inserted) {
                call.response.status(HttpStatusCode.Created)
            } else {
                call.response.status(HttpStatusCode.BadRequest)
            }
        } else {
            call.respondRedirect("/login")
        }
    }
}
