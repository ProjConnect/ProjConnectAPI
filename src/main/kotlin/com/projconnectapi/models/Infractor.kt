package com.projconnectapi.models

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId

data class Infractor(
    @BsonId val _id: Id<Infractor> = newId(),
    val user: String,
    var banStatus: Boolean = false,
    var infractions: MutableList<String>,
)
