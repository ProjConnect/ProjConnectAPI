package com.projconnectapi.routes

import com.projconnectapi.clients.database
import com.projconnectapi.clients.safeTokenVerification
import com.projconnectapi.clients.utils.createUser
import com.projconnectapi.clients.utils.getUser
import com.projconnectapi.clients.utils.updateUser
import com.projconnectapi.models.User
import com.projconnectapi.models.Post
import com.projconnectapi.models.extensions.toPublicUser
import com.projconnectapi.schemas.PublicUser
import com.projconnectapi.schemas.UserSession
import com.projconnectapi.schemas.extensions.toUser
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.*
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import org.bson.types.ObjectId
import org.litote.kmongo.*
fun isUserSafe(user: PublicUser, googleAcc: String): Boolean {
    return user.username != "" && user.email == googleAcc
}

fun ifSafeThenInsert(user: PublicUser, googleAcc: String): Boolean {
    return if (isUserSafe(user, googleAcc)) {
        val currentUser = getUser(User::email eq user.email)
        if (currentUser != null) {
            updateUser(user.toUser(currentUser))
        } else {
            createUser(user.toUser(null))
        }
        true
    } else {
        false
    }
}


fun Route.userRoute() {
    get("/search/user/{id}") {
        call.respondText("Functionality not implemented!", status = HttpStatusCode.OK)
    }


    get("/search/user/{userName}") {
        val userName = call.parameters["userName"] ?: return@get call.respondText(
            "Missing or malformed userName",
            status = HttpStatusCode.BadRequest
        )
        val user = database.getCollection<User>().findOne(User::username eq userName)
        if (user != null) {
            call.respond(user)
        } else {
            call.respondText("No user found", status = HttpStatusCode.NotFound)
        }
    }

    get("/my_profile") {
        val userSession: UserSession? = call.sessions.get<UserSession>()
        val auth = safeTokenVerification(userSession)
        if (auth != null) {
            val email = auth["email"].toString()
            val userProfile = getUser(User::email eq email)
            if (userProfile != null) {
                call.respond(userProfile.toPublicUser())
            } else {
                call.respond(
                    PublicUser(
                        "",
                        email,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                    )
                )
            }
        } else {
            call.response.status(HttpStatusCode.Unauthorized)
        }
    }

    post("/profile/update") {
        val user = call.receive<PublicUser>()
        val userSession: UserSession? = call.sessions.get<UserSession>()
        val auth = safeTokenVerification(userSession)
        if (auth != null) {
            val email = auth["email"].toString()
            val inserted = ifSafeThenInsert(user, email)
            if (inserted) {
                call.response.status(HttpStatusCode.Created)
            } else {
                call.response.status(HttpStatusCode.BadRequest)
            }
        } else {
            call.response.status(HttpStatusCode.Unauthorized)
        }
    }
}
