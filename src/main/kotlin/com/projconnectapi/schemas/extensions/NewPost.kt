package com.projconnectapi.schemas.extensions

import com.projconnectapi.models.Post
import com.projconnectapi.models.PostRequest
import com.projconnectapi.models.Review
import com.projconnectapi.schemas.NewPost
import com.projconnectapi.schemas.PublicPostRequest
import org.litote.kmongo.newId

fun NewPost.toPost(current: Post?) = Post(
    _id = current?._id ?: newId(),
    subject = subject,
    ownerId = ownerId,
    devId = devId,
    body = body,
    supporters= supporters,
    finalProductScore = Review(0F,"",""),
    isArchived = isArchived,
    tags = tags,
    course = course
)
