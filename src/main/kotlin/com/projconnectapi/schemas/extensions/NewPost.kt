package com.projconnectapi.schemas.extensions

import com.projconnectapi.models.Post
import com.projconnectapi.models.Review
import com.projconnectapi.schemas.NewPost
import org.litote.kmongo.newId

fun NewPost.toPost(current: Post?) = Post(
    _id = current?._id ?: newId(),
    subject = subject,
    ownerId = ownerId,
    deadline = deadline,
    devId = devId,
    body = body,
    supporters = supporters,
    finalProductScore = Review(0F, "", ""),
    isArchived = isArchived,
    tags = tags,
    course = course,
    image = image
)
