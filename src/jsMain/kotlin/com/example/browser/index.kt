package com.example.browser

import com.example.models.common.SomeData
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.js.onClickFunction
import react.dom.button
import react.dom.render

fun main() = with(document.getElementById("root")) {
    render(this) {
        button {
            +"Get some data"
            attrs.onClickFunction = {
                httpClient {
                    val result: SomeData = get("http://localhost:18080/data")
                    window.alert("Text: ${result.text}, number: ${result.number}")
                }
            }
        }
    }
}


val httpClient = HttpClient() {
    install(JsonFeature) { serializer = KotlinxSerializer() }
}

fun httpClient(handle: suspend HttpClient.() -> Unit) {
    GlobalScope.launch { httpClient.handle() }
}
