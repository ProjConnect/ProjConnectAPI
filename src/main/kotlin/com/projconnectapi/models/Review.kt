package com.projconnectapi.models

import kotlinx.serialization.Serializable

@Serializable
data class Review(val grade: Float,
                  val subject: String,
                  val body: String)