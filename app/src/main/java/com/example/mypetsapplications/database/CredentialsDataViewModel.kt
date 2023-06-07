package com.example.mypetsapplications.database

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mypetsapplications.database.data.ApiService
import com.example.mypetsapplications.database.model.Animal
import com.example.mypetsapplications.database.model.Application
import com.example.mypetsapplications.database.model.ApplicationIdReceive
import com.example.mypetsapplications.database.model.ApplicationReceive
import com.example.mypetsapplications.database.model.Employee
import com.example.mypetsapplications.database.model.EmployeeReceive
import com.example.mypetsapplications.database.model.FullApplicationWithIdReceive
import com.example.mypetsapplications.database.model.RegistrationCredentials
import com.example.mypetsapplications.database.model.User
import com.example.mypetsapplications.database.model.UserReceive
import com.example.mypetsapplications.database.model.UserToken
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException

const val SERVER_URL = "http://192.168.0.190:8080/"


class CredentialsDataViewModel() : ViewModel() {

    val user = mutableStateOf<User?>(null)


    private lateinit var retrofit: Retrofit
    private lateinit var apiService: ApiService


    init {
        try {
            retrofit = Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            apiService = retrofit.create(ApiService::class.java)
        } catch (e: ConnectException) {
            e.printStackTrace()
        }
    }

    suspend fun sendApplication(applicationReceive: ApplicationReceive): Boolean {
        val response = apiService.addApplication(applicationReceive)
        return response.isSuccessful
    }

    suspend fun getAllAnimals(): List<Animal>? {
        // GET request
        val response = apiService.fetchAllAnimals()
        if (response.isSuccessful) {
            val animals = response.body()
            Log.d("ANIMALS", response.body().toString())
            return animals
        }
        return null
    }

    suspend fun getAllApplicationsByTypeOfUser(userId: Int, isAdmin: Boolean = false): List<Application>? {

        val response: Response<List<Application>> = if (isAdmin) {
            apiService.fetchAppsByEmployeeId(id = ApplicationIdReceive(userId))
        } else {
            apiService.fetchAppsByUserId(id = ApplicationIdReceive(userId))
        }

        return if (response.isSuccessful) {
            Log.d("Getting applications", "getAllAppl: I GOT HIM! " + response.body())
            response.body()

        } else {
            Log.d("Getting applications", "ERROR! " + response.message())
            null
        }
    }

    suspend fun getUserByPhone(phone: String): User? {
        val response = apiService.fetchUser(UserReceive(phoneNumber = phone))
        return if (response.isSuccessful) {
            response.body()
        } else null
    }

    suspend fun getEmployeeByPhone(phone: String): Employee? {
        val response = apiService.fetchEmployee(
            EmployeeReceive(phoneNumber = phone)
        )
        return if (response.isSuccessful) {
            response.body()
        } else null
    }

    suspend fun register(registrationCredentials: RegistrationCredentials): UserToken? {
        val response = apiService.register(registrationCredentials)
        return if (response.isSuccessful) {
            Log.d("LOGIN", response.body().toString())
            val body = response.body()

            if (!body!!.isAdmin) {
                // As soon as registration was successful get the User
                user.value = getUserByPhone(registrationCredentials.phoneNumber)
                // Get users applications
                //getAllApplications()
            } else {
                // TODO (Implement admin logic)
            }

            body
        } else null
    }

    suspend fun updateApplication(application: Application): Boolean {
        val applicationToUpdate = application.convertToApplicationForUpdate()

        val response = apiService.updateApplication(applicationToUpdate)
        return response.isSuccessful
    }


    suspend fun deleteApplication(application: Application): Boolean {
        val applicationToDelete = application.applicationId

        val response = apiService.deleteApplication(ApplicationIdReceive(id = applicationToDelete))

        return response.isSuccessful
    }


    suspend fun getOneAnimal() {
        // POST request
        val newAnimal = Animal(name = "Lion", type = "Mammal")
        val response = apiService.addAnimal(newAnimal)
        if (response.isSuccessful) {
            val addedAnimal = response.body()
        }
    }
}

private fun Application.convertToApplicationForUpdate(): FullApplicationWithIdReceive {
    return FullApplicationWithIdReceive(
        id = this.applicationId,
        client = this.client.id,
        animal = this.animal.id,
        hosingConditions = this.hosingConditions,
        aboutTheClient = this.aboutTheClient,
        usersCity = this.usersCity,
        employee = this.employee!!.id,
        applicationStatus = this.applicationStatus,
        employeeComment = this.employeeComment
    )
}



