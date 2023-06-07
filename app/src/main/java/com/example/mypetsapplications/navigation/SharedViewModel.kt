package com.example.mypetsapplications.navigation

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mypetsapplications.database.model.Animal
import com.example.mypetsapplications.database.model.Application
import com.example.mypetsapplications.database.model.ApplicationReceive
import com.example.mypetsapplications.database.model.ApplicationStatus
import com.example.mypetsapplications.database.model.Employee
import com.example.mypetsapplications.database.model.User


class SharedViewModel() : ViewModel() {


    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User>
        get() = _currentUser

    private val _currentEmployee = MutableLiveData<Employee>()
    val currentEmployee: LiveData<Employee>
        get() = _currentEmployee

    private val _animals = MutableLiveData<List<Animal>>()
    val animals: LiveData<List<Animal>>
        get() = _animals

    //Application, that contains all data for sending
      private val _applicationToSend: MutableLiveData<ApplicationReceive> = MutableLiveData<ApplicationReceive>()
      val applicationToSend: LiveData<ApplicationReceive>
          get() = _applicationToSend



    private val _applications = MutableLiveData<List<Application>>()
    val applications: LiveData<List<Application>>
        get() = _applications


    val selectedAnimal = MutableLiveData<Animal>()
    val selectedApplication = MutableLiveData<Application>()

    init {
        _applications.value = listOf()

        _applicationToSend.value = ApplicationReceive(
            -1,
            -1,
            "",
            "",
            usersCity = "",
            employee = null,
            applicationStatus = ApplicationStatus.NOT_SEND,
            employeeComment = ""
        )
        Log.d("Getting user", "Init in first time, here current user: ${_currentUser.value}")
        selectedAnimal.value = Animal()
        selectedApplication.value = Application(client = User(id = 4))
    }

    fun saveAsCurrentUser(user: User?) {
        _currentUser.value = user
        Log.d("Getting user", "SAVEaScurrent: I GOT HIM! " + _currentUser.value)
    }

    fun saveAsApplications(applications: List<Application>?) {
        _applications.value = applications ?: listOf()
        Log.d("Getting applications", "SAVEaScurrent: I GOT HIM! ${applications.toString()}")


    }

    fun saveAsAnimals(animals: List<Animal>?) {
        _animals.value = animals
    }

    fun createApplicationReceive(applicationData: MutableState<Application>) {

        _applicationToSend.value = ApplicationReceive(
            client = _currentUser.value!!.id,
            animal = selectedAnimal.value!!.id,
            hosingConditions = applicationData.value.hosingConditions,
            aboutTheClient = applicationData.value.aboutTheClient,
            usersCity = applicationData.value.usersCity,
            employee = null,
            applicationStatus = ApplicationStatus.NOT_SEND,
            employeeComment = null
        )

        Log.d("Sending application", "Here is created to send application" + _applicationToSend.value.toString())
    }

    fun saveAsCurrentEmployee(employee: Employee?) {
_currentEmployee.value = employee
    }

    fun clearAllData() {
        _currentUser.value = null
        _currentEmployee.value = null
        _animals.value = mutableListOf()
        _applications.value = mutableListOf()
    }


}