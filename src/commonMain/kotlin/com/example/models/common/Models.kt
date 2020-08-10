package com.example.models.common

import kotlinx.serialization.Serializable

@Serializable
data class SomeData(
    val text: String,
    val number: Int,
)