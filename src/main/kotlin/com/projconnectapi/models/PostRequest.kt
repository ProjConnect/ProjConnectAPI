package com.projconnectapi.models

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id

data class PostRequest (
    @BsonId val _id: Id<PostRequest>,
    val post: String,
    val devId: String,
    val description: String
)