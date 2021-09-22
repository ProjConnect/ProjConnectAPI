package com.projconnectapi.dbactions

import org.litote.kmongo.KMongo

val client = KMongo.createClient(System.getenv("MONGO_URI") ?: "")
val database = client.getDatabase("projconnect")
