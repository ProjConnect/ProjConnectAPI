package com.projconnectapi.routes

import com.projconnectapi.clients.database
import com.projconnectapi.models.NewPost
import com.projconnectapi.models.Post
import com.projconnectapi.models.Review
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import org.bson.types.ObjectId
import org.litote.kmongo.contains
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import io.ktor.request.*
import io.ktor.utils.io.*
import org.litote.kmongo.id.toId

fun Route.postsRoute() {
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

    post("/new-post") {
        val formParameters = call.receive<NewPost>()
        val postStorage = database.getCollection<Post>().insertOne(Post(
            _id = ObjectId().toId(),
            subject = formParameters.subject,
            ownerId = formParameters.ownerId,
            devId = formParameters.devId,
            body = formParameters.body,
            supporters=formParameters.supporters,
            finalProductScore = Review(0F,"",""),
            isArchived = formParameters.isArchived,
            tags = formParameters.tags,
            course = formParameters.course
        ))
        call.respond(postStorage)
    }
}
