package com.projconnectapi.routes

import com.projconnectapi.clients.database
import com.projconnectapi.clients.tokenVerifier
import com.projconnectapi.models.Post
import com.projconnectapi.schemas.UserSession
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import org.bson.types.ObjectId
import org.litote.kmongo.contains
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection

fun Route.postsRoute() {
    get("/posts") {
        val userSession: UserSession? = call.sessions.get<UserSession>()
        if (userSession != null) {
            val postStorage = database.getCollection<Post>().find().toList()
            if (postStorage.isNotEmpty()) {
                call.respond(postStorage)
            } else {
                call.respondText("No post found", status = HttpStatusCode.NotFound)
            }
        } else {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }

    get("/posts/mine") {
        val userSession: UserSession? = call.sessions.get<UserSession>()
        val auth = tokenVerifier.verify(userSession?.idToken)?.payload
        if (auth != null) {
            call.application.log.info(auth.toString())
            call.respond("To be implemented")
        } else {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }

    get("/search/post/id/{id}") {
        val id = call.parameters["id"] ?: return@get call.respondText(
            "Missing or malformed id",
            status = HttpStatusCode.BadRequest
        )
        val post = database.getCollection<Post>().findOneById(ObjectId(id))
        if (post != null) {
            call.respond(post)
        } else {
            call.respondText("No post found", status = HttpStatusCode.NotFound)
        }
    }

    get("/search/post/tags/{tag}") {
        val tag = call.parameters["tag"] ?: return@get call.respondText(
            "Missing or malformed tag",
            status = HttpStatusCode.BadRequest
        )
        val posts: List<Post> = database.getCollection<Post>().find(Post::tags contains tag).toList()
        if (posts.isNotEmpty()) {
            call.respond(posts)
        } else {
            call.respondText("No post found with tag $tag", status = HttpStatusCode.NotFound)
        }
    }
}
