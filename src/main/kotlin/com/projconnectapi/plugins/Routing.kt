package com.projconnectapi.plugins

import com.projconnectapi.routes.oauthRoute
import com.projconnectapi.routes.postsRoute
import com.projconnectapi.routes.userRoute
import com.projconnectapi.schemas.UserSession
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.sessions.get
import io.ktor.sessions.sessions

fun Application.configureRouting() {

    routing {
        get("/status") {
            val userSession: UserSession? = call.sessions.get<UserSession>()
            if (userSession != null) {
                call.respondText("logged", status = HttpStatusCode.OK)
            } else {
                call.respondText("not logged", status = HttpStatusCode.OK)
            }
        }
        oauthRoute()
        postsRoute()
        userRoute()
    }
}
