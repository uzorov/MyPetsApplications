package com.example.mypetsapplications.database.model

import kotlinx.serialization.Serializable

@Serializable
data class Employee(
    val id: Int = 0,
    val name: String = "",
    val position: String = "",
    val phone_number: String = "",
    val mail: String = "",
    val applications: MutableList<Application> = mutableListOf<Application>()
)

@Serializable
data class EmployeeReceive(
    val phoneNumber: String,
)
