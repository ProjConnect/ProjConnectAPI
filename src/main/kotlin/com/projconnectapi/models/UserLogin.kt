package com.projconnectapi.models

import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id

data class UserLogin(
    @BsonId val _id: Id<UserLogin>,
    val username: String,
    val email: String,
    val password: String,
    val isModerator: Boolean,
    val publicData: UserPublicData,
    val userid: String
)
