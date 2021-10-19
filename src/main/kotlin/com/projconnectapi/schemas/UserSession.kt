package com.projconnectapi.schemas

data class UserSession(
    val accessToken: String,
    val idToken: String,
)
