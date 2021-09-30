package com.projconnectapi

import com.projconnectapi.plugins.configureOAuth
import com.projconnectapi.plugins.configureRouting
import com.projconnectapi.plugins.configureSessions
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({
            configureOAuth()
            configureRouting()
            configureSessions()
        }) {
            handleRequest(HttpMethod.Get, "/status").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }
}
