package com.projconnectapi.clients

import com.projconnectapi.models.Post
import com.projconnectapi.models.PostRequest
import com.projconnectapi.models.User
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

val mongoClient = KMongo.createClient(System.getenv("MONGO_URI") ?: "")
val database = mongoClient.getDatabase(System.getenv("DB_NAME") ?: "")

val userCollection = database.getCollection<User>()
val postCollection = database.getCollection<Post>()
val postRequestCollection = database.getCollection<PostRequest>()
