package com.projconnectapi.models

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id

data class PostWithRequests(
    @BsonId val _id: Id<Post>,
    val subject: String,
    val ownerId: String,
    val devId: MutableList<String>,
    val deadline: String?,
    val body: String,
    val supporters: List<String>,
    val finalProductScore: Review,
    val isArchived: Boolean,
    val tags: List<String>,
    val course: String,
    var reported: Boolean = false,
    var image: Int = 0,
    var requests: MutableList<PostRequest>
)
