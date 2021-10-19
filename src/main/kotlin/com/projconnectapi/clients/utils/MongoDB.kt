package com.projconnectapi.clients.utils

import com.mongodb.BasicDBObject
import com.projconnectapi.clients.postCollection
import com.projconnectapi.clients.userCollection
import com.projconnectapi.models.Post
import com.projconnectapi.models.User
import org.bson.conversions.Bson
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

fun getUserById(id: Bson): User? {
    return userCollection.findOneById(id)
}

// Post functions
fun createPost(post: Post) {
    postCollection.insertOne(post)
}

fun updatePost(post: Post) {
    val updateObject = BasicDBObject()
    updateObject["\$set"] = post
    postCollection.updateOne(Post::_id eq post._id, updateObject)
}

fun getPost(filter: Bson): Post? {
    return postCollection.findOne(filter)
}

fun getPostById(id: Bson): Post? {
    return postCollection.findOneById(id)
}