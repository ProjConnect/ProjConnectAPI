package com.projconnectapi.routes

import com.projconnectapi.clients.infractorCollection
import com.projconnectapi.clients.safeTokenVerification
import com.projconnectapi.clients.utils.createUser
import com.projconnectapi.clients.utils.getUser
import com.projconnectapi.clients.utils.getUserById
import com.projconnectapi.clients.utils.updateInfractor
import com.projconnectapi.clients.utils.updateUser
import com.projconnectapi.models.Infractor
import com.projconnectapi.models.User
import com.projconnectapi.models.extensions.toPublicUser
import com.projconnectapi.schemas.EmailPayload
import com.projconnectapi.schemas.PublicUser
import com.projconnectapi.schemas.UserSession
import com.projconnectapi.schemas.extensions.toUser
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import org.bson.types.ObjectId
import org.litote.kmongo.eq
import org.litote.kmongo.id.toId

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
    get("/search/user/id/{id}") {
        val id = call.parameters["id"] ?: return@get call.respondText(
            "Missing or malformed id",
            status = HttpStatusCode.BadRequest
        )
        val user: User? = getUserById(ObjectId(id).toId())
        if (user != null) {
            call.respond(user.toPublicUser())
        } else {
            call.response.status(HttpStatusCode.NotFound)
        }
    }

    get("/search/user/username/{username}") {
        val username = call.parameters["username"] ?: return@get call.respondText(
            "Missing or malformed username",
            status = HttpStatusCode.BadRequest
        )
        val user: User? = getUser(User::username eq username)
        if (user != null) {
            call.respond(user.toPublicUser())
        } else {
            call.response.status(HttpStatusCode.NotFound)
        }
    }

    get("/search/user/email/{email}") {
        val email = call.parameters["email"] ?: return@get call.respondText(
            "Missing or malformed email",
            status = HttpStatusCode.BadRequest
        )
        val user: User? = getUser(User::email eq email)
        if (user != null) {
            call.respond(user.toPublicUser())
        } else {
            call.response.status(HttpStatusCode.NotFound)
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
                        null,
                    )
                )
            }
        } else {
            call.response.status(HttpStatusCode.Unauthorized)
        }
    }

    get("/access") {
        val userSession: UserSession? = call.sessions.get<UserSession>()
        val auth = safeTokenVerification(userSession)
        if (auth != null) {
            val email = auth["email"].toString()
            val userProfile = getUser(User::email eq email)
            if (userProfile != null && userProfile.isModerator) {
                call.response.status(HttpStatusCode.OK)
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

    post("/promote/user") {
        val userToPromote = call.receive<EmailPayload>()
        val userSession: UserSession? = call.sessions.get<UserSession>()
        val auth = safeTokenVerification(userSession)
        if (auth != null) {
            val email = auth["email"].toString()
            val mod: User? = getUser(User::email eq email)
            if (mod != null && mod.isModerator) {
                val updatedUser: User? = getUser(User::email eq userToPromote.email)
                if (updatedUser != null) {
                    updatedUser.isModerator = true
                    updateUser(updatedUser)
                    call.response.status(HttpStatusCode.OK)
                } else {
                    call.response.status(HttpStatusCode.NotFound)
                }
            } else {
                call.response.status(HttpStatusCode.Unauthorized)
            }
        } else {
            call.response.status(HttpStatusCode.Unauthorized)
        }
    }

    post("/ban/user") {
        val userToBan = call.receive<Infractor>()
        val userSession: UserSession? = call.sessions.get<UserSession>()
        val auth = safeTokenVerification(userSession)
        if (auth != null) {
            val email = auth["email"].toString()
            val mod: User? = getUser(User::email eq email)
            if (mod != null && mod.isModerator) {
                val updatedUser: User? = getUser(User::username eq userToBan.user)
                if (updatedUser != null && !updatedUser.isModerator) {
                    updatedUser.banned = true
                    updateUser(updatedUser)
                    userToBan.banStatus = true
                    updateInfractor(userToBan)
                    call.response.status(HttpStatusCode.OK)
                } else {
                    call.response.status(HttpStatusCode.NotFound)
                }
            } else {
                call.response.status(HttpStatusCode.Unauthorized)
            }
        } else {
            call.response.status(HttpStatusCode.Unauthorized)
        }
    }

    post("/unban/user") {
        val userToUnban = call.receive<Infractor>()
        val userSession: UserSession? = call.sessions.get<UserSession>()
        val auth = safeTokenVerification(userSession)
        if (auth != null) {
            val email = auth["email"].toString()
            val mod: User? = getUser(User::email eq email)
            if (mod != null && mod.isModerator) {
                val updatedUser: User? = getUser(User::username eq userToUnban.user)
                if (updatedUser != null && !updatedUser.isModerator) {
                    updatedUser.banned = false
                    updateUser(updatedUser)
                    userToUnban.banStatus = false
                    updateInfractor(userToUnban)
                    call.response.status(HttpStatusCode.OK)
                } else {
                    call.response.status(HttpStatusCode.NotFound)
                }
            } else {
                call.response.status(HttpStatusCode.Unauthorized)
            }
        } else {
            call.response.status(HttpStatusCode.Unauthorized)
        }
    }

    get("/search/user/infractor") {
        val userSession: UserSession? = call.sessions.get<UserSession>()
        val auth = safeTokenVerification(userSession)
        if (auth != null) {
            val email = auth["email"].toString()
            val mod: User? = getUser(User::email eq email)
            if (mod != null && mod.isModerator) {
                val infractors = infractorCollection.find().toList()
                if (infractors.isNotEmpty()) {
                    call.respond(infractors)
                } else {
                    call.respondText("No infractors found", status = HttpStatusCode.NoContent)
                }
            } else {
                call.response.status(HttpStatusCode.Unauthorized)
            }
        } else {
            call.response.status(HttpStatusCode.Unauthorized)
        }
    }
}
