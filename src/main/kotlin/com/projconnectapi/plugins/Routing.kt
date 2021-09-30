package com.projconnectapi.plugins

import com.projconnectapi.routes.oauthRoute
import com.projconnectapi.routes.postsRoute
import com.projconnectapi.routes.userRoute
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.routing.get
import io.ktor.routing.routing

fun Application.configureRouting() {

    routing {
        get("/status") {
            call.response.status(HttpStatusCode.OK)
        }
        oauthRoute()
        postsRoute()
        userRoute()
    }
}
