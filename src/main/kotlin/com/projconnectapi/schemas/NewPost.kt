package com.projconnectapi.schemas

data class NewPost(
    val subject: String,
    val ownerId: String,
    val devId: MutableList<String>,
    val deadline: String?,
    val body: String,
    val supporters: List<String>,
    val isArchived: Boolean,
    val tags: List<String>,
    val course: String,
    val image: Int,
)
