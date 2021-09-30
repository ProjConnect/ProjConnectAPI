package com.projconnectapi.clients

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer

val oauthClient = HttpClient(CIO) {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
}
