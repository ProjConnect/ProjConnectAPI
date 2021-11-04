package com.projconnectapi.models

data class NewPost(
    val subject: String,
    val ownerId: String,
    val devId: MutableList<String>,
    val body: String,
    val supporters: List<String>,
    val isArchived: Boolean,
    val tags: List<String>,
    val course: String,
)
