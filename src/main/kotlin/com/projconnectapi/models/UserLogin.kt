package com.projconnectapi.models

import kotlinx.serialization.Serializable

@Serializable
data class UserLogin (val username: String,
                      val email: String,
                      val password: String,
                      val isModerator: Boolean,
                      val publicData: UserPublicData,
                      val userid:String)