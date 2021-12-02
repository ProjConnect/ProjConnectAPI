package com.projconnectapi.models

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId

data class User(
    @BsonId val _id: Id<User> = newId(),
    val username: String,
    val email: String,
    var isModerator: Boolean,
    var banned: Boolean = false,
    val firstName: String?,
    val lastName: String?,
    val aboutMe: String?,
    val score: Float?,
    val history: List<Id<Post>>?,
    val languages: List<String>?,
    val technologies: List<String>?,
    val otherSkills: List<String>?,
    val github: String?,
    val gitlab: String?,
    val avatar: Int?
)
