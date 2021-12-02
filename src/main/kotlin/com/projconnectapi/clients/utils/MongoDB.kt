package com.projconnectapi.clients.utils

import com.mongodb.BasicDBObject
import com.mongodb.client.FindIterable
import com.mongodb.client.result.InsertOneResult
import com.projconnectapi.clients.infractorCollection
import com.projconnectapi.clients.postCollection
import com.projconnectapi.clients.postRequestCollection
import com.projconnectapi.clients.userCollection
import com.projconnectapi.models.Infractor
import com.projconnectapi.models.Post
import com.projconnectapi.models.PostRequest
import com.projconnectapi.models.User
import org.bson.conversions.Bson
import org.litote.kmongo.Id
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.findOneById

// User functions
fun createUser(user: User) {
    userCollection.insertOne(user)
}

fun updateUser(user: User) {
    val updateObject = BasicDBObject()
    updateObject["\$set"] = user
    userCollection.updateOne(User::_id eq user._id, updateObject)
}

fun getUser(filter: Bson): User? {
    return userCollection.findOne(filter)
}

fun getUserById(id: Id<User>): User? {
    return userCollection.findOneById(id)
}

// Post functions
fun createPost(post: Post): InsertOneResult {
    return postCollection.insertOne(post)
}

fun updatePost(post: Post) {
    val updateObject = BasicDBObject()
    updateObject["\$set"] = post
    postCollection.updateOne(Post::_id eq post._id, updateObject)
}

fun deletePostById(id: Id<Post>): Boolean {
    return postCollection.deleteOneById(id).wasAcknowledged()
}

fun getPost(filter: Bson): Post? {
    return postCollection.findOne(filter)
}

fun getPostById(id: Id<Post>): Post? {
    return postCollection.findOneById(id)
}

// Post request functions
fun createPostRequest(request: PostRequest) {
    postRequestCollection.insertOne(request)
}

fun getPostRequests(filter: Bson): FindIterable<PostRequest> {
    return postRequestCollection.find(filter)
}

// Infractor functions
fun createInfractor(username: String, infraction: String): InsertOneResult {
    val infractions = MutableList(1) { infraction }
    return infractorCollection.insertOne(
        Infractor(
            user = username,
            banStatus = false,
            infractions = infractions
        )
    )
}

fun updateInfractor(infractor: Infractor) {
    val updateObject = BasicDBObject()
    updateObject["\$set"] = infractor
    infractorCollection.updateOne(Infractor::_id eq infractor._id, updateObject)
}

fun createOrUpdateInfractor(post: Post) {
    val infractor = infractorCollection.findOne((Infractor::user eq post.ownerId))
    if (infractor != null) {
        infractor.infractions.add(post.toString())
        updateInfractor(infractor)
    } else {
        createInfractor(post.ownerId, post.toString())
    }
}
