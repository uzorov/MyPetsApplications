package com.example.mypetsapplications.database.data

import android.media.session.MediaSession
import com.example.mypetsapplications.database.model.Animal
import com.example.mypetsapplications.database.model.Application
import com.example.mypetsapplications.database.model.ApplicationGetResponse
import com.example.mypetsapplications.database.model.ApplicationIdReceive
import com.example.mypetsapplications.database.model.ApplicationReceive
import com.example.mypetsapplications.database.model.Employee
import com.example.mypetsapplications.database.model.EmployeeReceive
import com.example.mypetsapplications.database.model.FullApplicationWithIdReceive
import com.example.mypetsapplications.database.model.LoginCredentials
import com.example.mypetsapplications.database.model.RegistrationCredentials
import com.example.mypetsapplications.database.model.User
import com.example.mypetsapplications.database.model.UserReceive
import com.example.mypetsapplications.database.model.UserToken
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("fetch_all_animals")
    suspend fun fetchAllAnimals(): Response<List<Animal>>

    @POST("add_animal")
    suspend fun addAnimal(@Body animal: Animal): Response<Animal>

    @POST("fetch_apps_by_user_id")
    suspend fun fetchAppsByUserId(@Body id: ApplicationIdReceive): Response<List<Application>>

    @POST("fetch_apps_by_employee_id")
    suspend fun fetchAppsByEmployeeId(@Body id: ApplicationIdReceive): Response<List<Application>>

    @POST("add_application")
    suspend fun addApplication(@Body application: ApplicationReceive): Response<ApplicationGetResponse>

    @POST("delete_application")
    suspend fun deleteApplication(@Body applicationId: ApplicationIdReceive): Response<Number>

    @POST("update_application")
    suspend fun updateApplication(@Body application: FullApplicationWithIdReceive): Response<Number>

    @POST("fetch_employee")
    suspend fun fetchEmployee(@Body employeePhone: EmployeeReceive): Response<Employee>

    @GET("fetch_employees_numbers")
    suspend fun fetchEmployeeNumbers(): Response<List<String>>

    @POST("login")
    suspend fun login(@Body credentials: LoginCredentials): Response<UserToken>

    @POST("registration")
    suspend fun register(@Body credentials: RegistrationCredentials): Response<UserToken>

    @POST("fetch_user")
    suspend fun fetchUser(@Body phoneNumber: UserReceive): Response<User>
}
