package com.example

import com.example.models.common.SomeData
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class BackendVerticle : CoroutineVerticle() {
    private val log: Logger = LoggerFactory.getLogger(javaClass.name)
    private val server: HttpServer by lazy { vertx.createHttpServer()!! }
    private val router: Router by lazy { Router.router(vertx)!! }

    override suspend fun start() {
        vertx
            .createHttpServer()
            .requestHandler {
                router.apply {
                    route().handler(StaticHandler.create("../../../distributions"))
                    route(HttpMethod.GET, "/data").handler { routingContext ->
                        log.info("Data endpoint hit")
                        routingContext.response().apply {
                         //   isChunked = true
                            putHeader("content-type", "application/json")
                            val data = Json.encodeToString(SomeData("The answer is:", 42))
                            end(data)
                        }
                        log.info("Data served")
                    }
                }.accept(it)
            }
            .listen(18080, "localhost") { result ->
                if (result.succeeded()) {
                    log.info("Backend server is UP")
                } else {
                    log.error("Backend server is failed to start, shutting down", result.cause())
                    vertx.undeploy(deploymentID)
                }
            }
    }

    override suspend fun stop() {
        server.close {
            if (it.succeeded()) {
                log.info("Backend server is down")
            } else {
                log.info("Backend server shutdown failed")
            }
        }
    }
}
