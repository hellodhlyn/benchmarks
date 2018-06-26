package com.lynlab.benchmark.fcm.thread

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import java.io.FileInputStream
import java.util.concurrent.Executors

data class Payload(
        val message: String,
        val token: String
)


val mapper = ObjectMapper().registerKotlinModule()

val serviceAccount = FileInputStream("../service-account.json")
val options = FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build()!!

val app = FirebaseApp.initializeApp(options)!!
val messaging = FirebaseMessaging.getInstance(app)


fun send(idx: Int): Int {
    if (idx % 20 == 0) {
        println(idx)
    }

    // Deserialize json payload.
    val raw = "{\"message\":\"Message Body\",\"token\":\"ExampleToken\"}"
    val payload = mapper.readValue<Payload>(raw)

    val message = Message.builder()
            .putData("key", payload.message)
            .setToken(payload.token)
            .build()

    // Request FCM message API.
    try {
        messaging.send(message, true)
    } catch (e: FirebaseMessagingException) {
        // pass
    }

    return idx
}

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()

    val executor = Executors.newFixedThreadPool(8)
    IntRange(1, 500).map { idx ->
        executor.execute { send(idx) }
    }
    executor.shutdown()

    while (!executor.isTerminated) {}

    println("Elapsed Time : ${System.currentTimeMillis() - startTime} ms")
}
