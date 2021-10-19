package com.projconnectapi.clients

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.projconnectapi.schemas.UserSession
import java.util.Collections.singletonList

val tokenVerifier: GoogleIdTokenVerifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory())
    .setAudience(singletonList(System.getenv("GOOGLE_CLIENT_ID")))
    .build()

fun safeTokenVerification(session: UserSession?): GoogleIdToken.Payload? {
    return if (session != null) {
        tokenVerifier.verify(session.idToken)?.payload
    } else {
        null
    }
}
