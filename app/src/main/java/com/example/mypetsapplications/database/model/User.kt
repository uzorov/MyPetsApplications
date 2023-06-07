package com.example.mypetsapplications.database.model

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int = 0,
    var name: String = "Узоров Кирилл",
    var mail: String = "uzorov@mail.ru",
    var phone: String = "89109867534"
)


