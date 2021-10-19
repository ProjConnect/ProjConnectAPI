package com.projconnectapi.schemas.extensions

import com.projconnectapi.models.User
import com.projconnectapi.schemas.PublicUser
import org.litote.kmongo.newId

fun PublicUser.toUser(current: User?) = User(
    _id = current?._id ?: newId(),
    username = username,
    email = email,
    isModerator = current?.isModerator ?: false,
    firstName = firstName,
    lastName = lastName,
    aboutMe = aboutMe,
    score = current?.score ?: 0.0f,
    languages = languages,
    history = current?.history,
    technologies = technologies,
    otherSkills = otherSkills,
    github = github,
    gitlab = gitlab
)
