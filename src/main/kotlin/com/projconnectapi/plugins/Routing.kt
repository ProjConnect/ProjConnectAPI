package com.projconnectapi.plugins

import com.projconnectapi.dbactions.database
import com.projconnectapi.models.Post
import com.projconnectapi.routes.oauthRoute
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import org.bson.types.ObjectId
import org.litote.kmongo.contains
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection

fun Application.configureRouting() {

    routing {
        get("/status") {
            call.response.status(HttpStatusCode.OK)
        }
        oauthRoute()

        get("/posts") {
            val postStorage = database.getCollection<Post>().find().toList()
            if (postStorage.isNotEmpty()) {
                call.respond(postStorage)
            } else {
                call.respondText("No post found", status = HttpStatusCode.NotFound)
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

        get("/search/user/{id}") {
            call.respondText("Functionality not implemented!", status = HttpStatusCode.OK)
        }
    }
}
