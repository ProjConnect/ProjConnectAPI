package com.projconnectapi.clients

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import java.util.Collections.singletonList

val tokenVerifier: GoogleIdTokenVerifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory())
    .setAudience(singletonList(System.getenv("GOOGLE_CLIENT_ID")))
    .build()
