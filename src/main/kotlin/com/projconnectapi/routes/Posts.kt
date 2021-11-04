package com.projconnectapi.routes

import com.projconnectapi.clients.database
import com.projconnectapi.clients.postRequestCollection
import com.projconnectapi.clients.safeTokenVerification
import com.projconnectapi.clients.utils.*
import com.projconnectapi.models.*
import com.projconnectapi.schemas.NewPost
import com.projconnectapi.schemas.PostRequestResponse
import com.projconnectapi.schemas.PublicPostRequest
import com.projconnectapi.schemas.UserSession
import com.projconnectapi.schemas.extensions.toPost
import com.projconnectapi.schemas.extensions.toPostRequest

import io.ktor.application.call
import io.ktor.application.log
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import org.bson.types.ObjectId
import io.ktor.request.*
import org.litote.kmongo.*
import org.litote.kmongo.id.toId

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
        val auth = safeTokenVerification(userSession)
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

    post("/new-post") {
        val userSession: UserSession? = call.sessions.get<UserSession>()
        if (userSession != null) {
            val formParameters = call.receive<NewPost>()
            val successful = createPost(formParameters.toPost(null)).wasAcknowledged()
            if (successful) {
                call.response.status(HttpStatusCode.Created)
            }
            else {
                call.response.status(HttpStatusCode.BadRequest)
            }
        }
        else {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }

    get("search/request/postid/{id}") {
        val postId = call.parameters["id"] ?: return@get call.respondText(
            "Missing or malformed id",
            status = HttpStatusCode.BadRequest
        )
        val requests: List<PostRequest> = postRequestCollection.find(PostRequest::post eq postId).toList()
        if (requests.isNotEmpty()) {
            call.respond(requests)
        } else {
            call.respondText("No requests found", status = HttpStatusCode.NotFound)
        }
    }

    post("request/post/create") {
        val postRequest = call.receive<PublicPostRequest>()
        val userSession: UserSession? = call.sessions.get<UserSession>()
        val auth = safeTokenVerification(userSession)
        if (auth != null) {
            createPostRequest(postRequest.toPostRequest(null))
        }
        else {
            call.response.status(HttpStatusCode.Unauthorized)
        }
    }

    post("request/post/response") {
        val response = call.receive<PostRequestResponse>()
        val userSession: UserSession? = call.sessions.get<UserSession>()
        val auth = safeTokenVerification(userSession)
        if (auth != null) {
            val email = auth["email"].toString()
            val user: User? = getUser(User::email eq email)
            if (user != null) {
                val request: PostRequest? = postRequestCollection.findOneById(ObjectId(response.requestId))
                if (request != null){
                    val post: Post? = getPostById(ObjectId(request.post))
                    val dev : User? = getUserById(ObjectId(request.devId))
                    if (post != null && dev != null) {
                        if (post.ownerId == user.username && dev.username !in post.devId) {
                            if (response.accepted) {
                                post.devId.add(dev.username)
                                updatePost(post)
                            }
                            postRequestCollection.deleteOneById(ObjectId(response.requestId))
                        }
                        else {
                            call.response.status(HttpStatusCode.Forbidden)
                        }
                    }
                    else {
                        call.response.status(HttpStatusCode.NotFound)
                    }
                }
                else {
                    call.response.status(HttpStatusCode.NotFound)
                }
            }
            else {
                call.response.status(HttpStatusCode.Unauthorized)
            }
        }
        else {
            call.response.status(HttpStatusCode.Unauthorized)
        }
    }

}
