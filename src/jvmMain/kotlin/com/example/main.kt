package com.example

import io.vertx.core.Vertx
import io.vertx.kotlin.core.deployVerticleAwait
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


fun main() {
    with(Vertx.vertx()) {
        GlobalScope.launch(dispatcher()) {
            deployVerticleAwait(BackendVerticle::class.qualifiedName!!)
        }
    }
}
