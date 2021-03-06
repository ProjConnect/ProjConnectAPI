package com.projconnectapi.routes

import com.projconnectapi.clients.database
import com.projconnectapi.clients.postRequestCollection
import com.projconnectapi.clients.safeTokenVerification
import com.projconnectapi.clients.utils.createOrUpdateInfractor
import com.projconnectapi.clients.utils.createPost
import com.projconnectapi.clients.utils.createPostRequest
import com.projconnectapi.clients.utils.deletePostById
import com.projconnectapi.clients.utils.getPost
import com.projconnectapi.clients.utils.getPostById
import com.projconnectapi.clients.utils.getPostRequests
import com.projconnectapi.clients.utils.getUser
import com.projconnectapi.clients.utils.updatePost
import com.projconnectapi.models.Post
import com.projconnectapi.models.PostRequest
import com.projconnectapi.models.PostWithRequests
import com.projconnectapi.models.User
import com.projconnectapi.models.extensions.addRequests
import com.projconnectapi.schemas.NewPost
import com.projconnectapi.schemas.PostRequestResponse
import com.projconnectapi.schemas.PublicPostRequest
import com.projconnectapi.schemas.UserSession
import com.projconnectapi.schemas.extensions.toPost
import com.projconnectapi.schemas.extensions.toPostRequest
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
import org.litote.kmongo.contains
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.eq
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import org.litote.kmongo.id.toId

fun isAuthorizedToDelete(user: User, post: Post): Boolean {
    // To delete a post, you must be the owner or the moderator
    return user.isModerator || post.ownerId == user._id.toString()
}

fun mergePostList(ownerPosts: List<Post>, devPosts: List<Post>): List<Post> {
    var list = devPosts
    for (post in ownerPosts) {
        list = list.filter { it._id.toString() != post._id.toString() }
    }
    return ownerPosts + list
}

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
            val email = auth["email"].toString()
            val user: User? = getUser(User::email eq email)
            if (user != null) {
                val postsOwner = database.getCollection<Post>().find(Post::ownerId eq user.username).toList()
                val postsDev = database.getCollection<Post>().find(Post::devId contains user.username).toList()
                call.respond(mergePostList(postsOwner, postsDev))
            }
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

    get("/search/post/name/{name}") {
        val name = call.parameters["name"] ?: return@get call.respondText(
            "Missing or malformed name",
            status = HttpStatusCode.BadRequest
        )
        val post = getPost(Post::subject eq name)
        if (post != null) {
            call.respond(post)
        } else {
            call.respondText("No post found", status = HttpStatusCode.NotFound)
        }
    }

    get("/search/post/reported") {
        val userSession: UserSession? = call.sessions.get<UserSession>()
        if (userSession != null) {
            val postStorage = database.getCollection<Post>().find(Post::reported eq true).toList()
            if (postStorage.isNotEmpty()) {
                call.respond(postStorage)
            } else {
                call.respondText("No reported post found", status = HttpStatusCode.NoContent)
            }
        } else {
            call.respond(HttpStatusCode.Unauthorized)
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

    get("/search/post/user/{username}") {
        val username = call.parameters["username"] ?: return@get call.respondText(
            "Missing or malformed username",
            status = HttpStatusCode.BadRequest
        )
        val userSession: UserSession? = call.sessions.get<UserSession>()
        val auth = safeTokenVerification(userSession)
        if (auth != null) {
            val postsOwner = database.getCollection<Post>().find(Post::ownerId eq username).toList()
            val postsDev = database.getCollection<Post>().find(Post::devId contains username).toList()
            call.respond(mergePostList(postsOwner, postsDev))
        } else {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }

    post("/new-post") {
        val userSession: UserSession? = call.sessions.get<UserSession>()
        if (userSession != null) {
            val formParameters = call.receive<NewPost>()
            val successful = createPost(formParameters.toPost(null)).wasAcknowledged()
            if (successful) {
                call.response.status(HttpStatusCode.Created)
            } else {
                call.response.status(HttpStatusCode.BadRequest)
            }
        } else {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }

    get("/search/request/postid/{id}") {
        val postId = call.parameters["id"] ?: return@get call.respondText(
            "Missing or malformed id",
            status = HttpStatusCode.BadRequest
        )
        val requests: List<PostRequest> = postRequestCollection.find(PostRequest::post eq postId).toList()
        call.respond(requests)
    }

    get("/request/received") {
        val userSession: UserSession? = call.sessions.get<UserSession>()
        val auth = safeTokenVerification(userSession)
        if (auth != null) {
            val email = auth["email"].toString()
            val user: User? = getUser(User::email eq email)
            if (user != null) {
                var postRequests = mutableListOf<PostWithRequests>()
                val postsOwner = database.getCollection<Post>().find(Post::ownerId eq user.username).toList()
                for (post in postsOwner) {
                    val requests = getPostRequests(PostRequest::post eq post._id.toString()).toMutableList()
                    if (requests.isNotEmpty()) {
                        postRequests += post.addRequests(requests)
                    }
                }
                call.respond(postRequests)
            }
        } else {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }

    get("/request/sent") {
        val userSession: UserSession? = call.sessions.get<UserSession>()
        val auth = safeTokenVerification(userSession)
        if (auth != null) {
            val email = auth["email"].toString()
            val user: User? = getUser(User::email eq email)
            if (user != null) {
                val requests = getPostRequests(PostRequest::devId eq user.username).toList()
                call.respond(requests)
            }
        } else {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }

    post("/request/create") {
        val userSession: UserSession? = call.sessions.get<UserSession>()
        val auth = safeTokenVerification(userSession)
        if (auth != null) {
            val postRequest = call.receive<PublicPostRequest>()
            createPostRequest(postRequest.toPostRequest(null))
            call.response.status(HttpStatusCode.Created)
        } else {
            call.response.status(HttpStatusCode.Unauthorized)
        }
    }

    post("/request/response") {
        val userSession: UserSession? = call.sessions.get<UserSession>()
        val auth = safeTokenVerification(userSession)
        if (auth != null) {
            val response = call.receive<PostRequestResponse>()
            val email = auth["email"].toString()
            val user: User? = getUser(User::email eq email)
            if (user != null) {
                val request: PostRequest? = postRequestCollection.findOneById(ObjectId(response.requestId))
                if (request != null) {
                    val post: Post? = getPostById(ObjectId(request.post).toId<Post>())
                    val dev: User? = getUser(User::username eq request.devId)
                    if (post != null && dev != null) {
                        if (post.ownerId == user.username && dev.username !in post.devId) {
                            if (response.accepted) {
                                post.devId.add(dev.username)
                                updatePost(post)
                            }
                            postRequestCollection.deleteOneById(ObjectId(response.requestId))
                            call.response.status(HttpStatusCode.OK)
                        } else {
                            call.response.status(HttpStatusCode.Forbidden)
                        }
                    } else {
                        call.response.status(HttpStatusCode.NotFound)
                    }
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

    post("/delete/post") {
        val post: Post = call.receive()
        val userSession: UserSession? = call.sessions.get<UserSession>()
        val auth = safeTokenVerification(userSession)
        if (auth != null) {
            val email = auth["email"].toString()
            val user: User? = getUser(User::email eq email)
            if (user != null && isAuthorizedToDelete(user, post)) {
                createOrUpdateInfractor(post)
                val deleted = deletePostById(post._id)
                if (deleted) {
                    call.response.status(HttpStatusCode.OK)
                } else {
                    call.response.status(HttpStatusCode.NoContent)
                }
            } else {
                call.response.status(HttpStatusCode.Unauthorized)
            }
        } else {
            call.response.status(HttpStatusCode.Unauthorized)
        }
    }

    post("/report/post") {
        val post: Post = call.receive<Post>()
        val userSession: UserSession? = call.sessions.get<UserSession>()
        val auth = safeTokenVerification(userSession)
        if (auth != null) {
            val email = auth["email"].toString()
            val user: User? = getUser(User::email eq email)
            if (user != null) {
                val targetPost: Post? = getPostById(post._id)
                if (targetPost != null) {
                    targetPost.reported = true
                    updatePost(targetPost)
                    call.response.status(HttpStatusCode.OK)
                } else {
                    call.response.status(HttpStatusCode.NoContent)
                }
            } else {
                call.response.status(HttpStatusCode.Unauthorized)
            }
        } else {
            call.response.status(HttpStatusCode.Unauthorized)
        }
    }

    post("/unreport/post") {
        val post: Post = call.receive<Post>()
        val userSession: UserSession? = call.sessions.get<UserSession>()
        val auth = safeTokenVerification(userSession)
        if (auth != null) {
            val email = auth["email"].toString()
            val user: User? = getUser(User::email eq email)
            if (user != null && user.isModerator) {
                val targetPost: Post? = getPostById(post._id)
                if (targetPost != null) {
                    targetPost.reported = false
                    updatePost(targetPost)
                    call.response.status(HttpStatusCode.OK)
                } else {
                    call.response.status(HttpStatusCode.NoContent)
                }
            } else {
                call.response.status(HttpStatusCode.Unauthorized)
            }
        } else {
            call.response.status(HttpStatusCode.Unauthorized)
        }
    }
}
