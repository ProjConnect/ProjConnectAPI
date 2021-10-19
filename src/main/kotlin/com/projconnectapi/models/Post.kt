package com.projconnectapi.models

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id

data class Post(
    @BsonId val _id: Id<Post>,
    val subject: String,
    val ownerId: String,
    val devId: List<String>,
    val body: String,
    val supporters: List<String>,
    val finalProductScore: Review,
    val isArchived: Boolean,
    val tags: List<String>,
    val course: String,
)
