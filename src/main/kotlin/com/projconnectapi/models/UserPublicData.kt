package com.projconnectapi.models

import kotlinx.serialization.Serializable

@Serializable
data class UserPublicData(val name: String,
                          val contact: String,
                          val aboutMe: String,
                          val score: Float,
                          val history: MutableList<String>)