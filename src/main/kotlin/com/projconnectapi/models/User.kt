package com.projconnectapi.models

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id

data class User(
    @BsonId val _id: Id<User>,
    val username: String,
    val email: String,
    val isModerator: Boolean,
    val name: String,
    val contact: String,
    val aboutMe: String,
    val score: Float,
    val history: MutableList<String>,
    val technologies: List<String>,
    val otherSkills: List<String>,
    val github: String,
    val gitlab: String,
)
