package com.projconnectapi.models.extensions

import com.projconnectapi.models.User
import com.projconnectapi.schemas.PublicUser

fun User.toPublicUser() = PublicUser(
    username = username,
    email = email,
    firstName = firstName,
    lastName = lastName,
    aboutMe = aboutMe,
    languages = languages,
    technologies = technologies,
    otherSkills = otherSkills,
    github = github,
    gitlab = gitlab
)
