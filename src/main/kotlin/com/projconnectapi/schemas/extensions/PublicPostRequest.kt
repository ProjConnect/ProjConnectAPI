package com.projconnectapi.schemas.extensions

import com.projconnectapi.models.PostRequest
import com.projconnectapi.schemas.PublicPostRequest
import org.litote.kmongo.newId

fun PublicPostRequest.toPostRequest(current: PostRequest?) = PostRequest(
    _id = current?._id ?: newId(),
    post = post,
    devId = devId,
    description = description
)
