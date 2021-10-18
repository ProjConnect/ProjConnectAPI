package com.projconnectapi.models

data class User(
    val username: String,
    val email: String,
    val isModerator: Boolean,
    val name: String,
    val contact: String,
    val aboutMe: String,
    var score: Float,
    var history: List<String>,
    val technologies: List<String>,
    val otherSkills: List<String>,
    val github: String,
    val gitlab: String
)
