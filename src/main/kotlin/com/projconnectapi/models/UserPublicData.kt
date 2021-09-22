package com.projconnectapi.models

data class UserPublicData(
    val name: String,
    val contact: String,
    val aboutMe: String,
    val score: Float,
    val history: MutableList<String>
)
