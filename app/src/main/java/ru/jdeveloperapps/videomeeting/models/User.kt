package ru.jdeveloperapps.videomeeting.models

import java.io.Serializable

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val token: String
) : Serializable