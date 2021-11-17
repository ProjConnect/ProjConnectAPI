package com.projconnectapi.schemas

data class PublicPostRequest(
    val post: String,
    val devId: String,
    val description: String
)