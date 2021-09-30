package com.projconnectapi.clients

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature

val oauthClient = HttpClient(CIO) {
    install(JsonFeature) {
        serializer = JacksonSerializer()
    }
}
