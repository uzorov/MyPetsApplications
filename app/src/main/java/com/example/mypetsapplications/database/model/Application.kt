package com.example.mypetsapplications.database.model

import kotlinx.serialization.Serializable

object ApplicationStatus {
    const val NOT_SEND = "Отправлена"
    const val SEND = "В обработке"
    const val PROCEED = "Принята"
    const val DECLINED = "Отклонена"
}

@Serializable
data class Application(
    var applicationId: Int = 0,
    var client: User = User(),
    var animal: Animal = Animal(),
    var hosingConditions: String = "",
    var aboutTheClient: String = "",
    var usersCity: String = "",
    var employee: Employee? = null,
    var applicationStatus: String = ApplicationStatus.PROCEED,
    var employeeComment: String? = ""
)

@Serializable
data class ApplicationIdReceive(
    val id: Int,
)

@Serializable
data class ApplicationReceive(
    val client: Int,
    val animal: Int,
    val hosingConditions: String = "",
    val aboutTheClient: String = "",
    val usersCity: String = "",
    val employee: Int? = null,
    val applicationStatus: String = ApplicationStatus.PROCEED,
    val employeeComment: String? = null
)

@Serializable
data class ApplicationGetResponse(
    val code: Int,
)

@Serializable
data class FullApplicationWithIdReceive(
    val id: Int,
    val client: Int,
    val animal: Int,
    val hosingConditions: String = "",
    val aboutTheClient: String = "",
    val usersCity: String = "",
    val employee: Int? = null,
    val applicationStatus: String = ApplicationStatus.PROCEED,
    val employeeComment: String? = null
)
