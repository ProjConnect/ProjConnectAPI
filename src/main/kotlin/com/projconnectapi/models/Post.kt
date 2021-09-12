package com.projconnectapi.models

import kotlinx.serialization.Serializable

@Serializable
data class Post (val subject: String,
                 val ownerId: String,
                 val devId: String,
                 val body: String,
                 val postId: String,
                 val supporters: List<String>,
                 val finalProductScore: Review,
                 val isArchived: Boolean,
                 val tags: MutableList<String>)

val postStorage = mutableListOf<Post>(
    Post("ProjConnect",
        "projconnect-team",
        "projconnect-team",
        "divulgar projetos",
        "123abc",
        mutableListOf<String>(),
        Review(-1.0f, "", ""),
        false,
        mutableListOf<String>("meta-project", "ktor", "react")),

    Post("Dummy",
        "unknown-team",
        "?",
        "lalala lelele lilili",
        "one-two-san-shi",
        mutableListOf<String>(),
        Review(-1.0f, "", ""),
        false,
        mutableListOf<String>("unknown", "unknown2"))
)