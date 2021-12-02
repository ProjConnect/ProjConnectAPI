package com.projconnectapi.models.extensions

import com.projconnectapi.models.Post
import com.projconnectapi.models.PostRequest
import com.projconnectapi.models.PostWithRequests

fun Post.addRequests(req: MutableList<PostRequest>) = PostWithRequests(
    _id = _id,
    subject = subject,
    ownerId = ownerId,
    devId = devId,
    deadline = deadline,
    body = body,
    supporters = supporters,
    finalProductScore = finalProductScore,
    isArchived = isArchived,
    tags = tags,
    course = course,
    reported = reported,
    image = image,
    requests = req,
)
