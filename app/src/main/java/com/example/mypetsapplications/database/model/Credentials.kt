package com.example.mypetsapplications.database.model

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationCredentials(
    val name: String,
    val phoneNumber: String,
    val password: String,
    val email: String
)

@Serializable
data class LoginCredentials(
    val login: String,   //phone number
    val password: String,
)

@Serializable
data class UserToken(
    val token: String,
    val isAdmin: Boolean,
    val isPasswordCorrect: Boolean,
    val isPhoneNumberValid: Boolean,
    val isPasswordValid: Boolean
    )

@Serializable
data class UserReceive (
    val phoneNumber: String
)
