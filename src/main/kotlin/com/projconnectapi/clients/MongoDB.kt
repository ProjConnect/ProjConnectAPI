package com.projconnectapi.clients

import org.litote.kmongo.KMongo

val mongoClient = KMongo.createClient(System.getenv("MONGO_URI") ?: "")
val database = mongoClient.getDatabase(System.getenv("DB_NAME") ?: "")
