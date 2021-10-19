package com.projconnectapi.schemas

data class PublicUser(
    val username: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val aboutMe: String?,
    val languages: List<String>?,
    val technologies: List<String>?,
    val otherSkills: List<String>?,
    val github: String?,
    val gitlab: String?
)
