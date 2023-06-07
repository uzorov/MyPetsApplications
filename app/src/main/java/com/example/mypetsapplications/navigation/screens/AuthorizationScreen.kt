package com.example.mypetsapplications.navigation.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.mypetsapplications.database.CredentialsDataViewModel
import com.example.mypetsapplications.database.model.RegistrationCredentials
import com.example.mypetsapplications.navigation.NavigationPaths
import com.example.mypetsapplications.navigation.SharedViewModel
import com.example.mypetsapplications.ui.theme.BearsEar
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AuthorizationScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    credentialsDataViewModel: CredentialsDataViewModel,
) {

    val systemUiController = rememberSystemUiController()
    // Use SideEffect to update the system UI bar colors
    SideEffect {
        // Set the status bar color to magenta
        systemUiController.setStatusBarColor(
            color = BearsEar
        )
    }

    var phoneNumber by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }

    var isPasswordCorrect by remember { mutableStateOf(true) }
    var isPasswordValid by remember { mutableStateOf(true) }
    var isNumberValid by remember { mutableStateOf(true) }

    var isLoading by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Авторизуйтесь для возможности просмотра питомцев приюта и подачи заявок на адопцию ",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        )
        TextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("Имя (можно пропустить)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = {
                if (!isNumberValid)
                    Text("Некорректный номер телефона")
                else
                    Text("Номер телефона")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next
            ),
            isError = !isNumberValid,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        TextField(

            value = userPassword,
            onValueChange = { userPassword = it },
            label = {
                if (!isPasswordCorrect)
                    Text("Неверный пароль")
                else if (!isPasswordValid)
                    Text("Ненадёжный пароль")
                else
                    Text("Пароль")
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
            ),
            isError = !(isPasswordCorrect && isPasswordValid),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Button(
            //  colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colors.secondaryVariant),
            onClick = {
                isLoading = true

                val registrationCredentials =
                    RegistrationCredentials(
                        name = userName,
                        phoneNumber = phoneNumber,
                        password = userPassword,
                        email = "",
                    )

                Log.d("MAKING_A_REQUEST", "About to launch in scope")
                credentialsDataViewModel.viewModelScope.launch {
                    val response = credentialsDataViewModel.register(registrationCredentials)
                    Log.d("MAKING_A_REQUEST", response.toString())
                    if (response?.token != "" && response != null) {

                        isPasswordCorrect = response.isPasswordCorrect
                        isNumberValid = response.isPhoneNumberValid
                        isPasswordValid = response.isPasswordValid

                        Log.d("MAKING_A_REQUEST", isPasswordCorrect.toString())
                        if (response.isAdmin && isPasswordCorrect && isNumberValid) {

                            val employee = credentialsDataViewModel.getEmployeeByPhone(phoneNumber)
                            sharedViewModel.saveAsCurrentEmployee(employee)

                            val applications = credentialsDataViewModel.getAllApplicationsByTypeOfUser(employee!!.id, true)
                            sharedViewModel.saveAsApplications(applications)


                            credentialsDataViewModel.viewModelScope.launch {
                                while (sharedViewModel.currentEmployee.value != null) {
                                    // Call the checkApplicationStatus function
                                     sharedViewModel.saveAsApplications(credentialsDataViewModel.getAllApplicationsByTypeOfUser(employee.id, true)
                                     )

                                    // Wait for a specified interval before calling the function again
                                    delay(1000L)

                                    Log.d("Update application status", "Requests was sent! $employee")
                                }
                            }

                            navController.navigate(NavigationPaths.ADMIN_SCREEN)
                        }
                        else if (isPasswordCorrect && isNumberValid && isPasswordValid) {

                            var userOrEmployeeId = -1
                            //If user is admin

                            //If register went well get user info
                            val user = credentialsDataViewModel.getUserByPhone(phoneNumber)
                            sharedViewModel.saveAsCurrentUser(user)
                            Log.d("Getting applications", user.toString())

                            //Then get user applications
                            val applications = credentialsDataViewModel.getAllApplicationsByTypeOfUser(user!!.id)
                            sharedViewModel.saveAsApplications(applications)


                            //And all animals :( Maybe I`ll make it more accurate, but I`m really pressed for time right now
                            val animals = credentialsDataViewModel.getAllAnimals()
                            sharedViewModel.saveAsAnimals(animals)


                            credentialsDataViewModel.viewModelScope.launch {
                                while (sharedViewModel.currentUser.value != null) {
                                    // Call the checkApplicationStatus function
                                    sharedViewModel.saveAsApplications(
                                        credentialsDataViewModel.getAllApplicationsByTypeOfUser(user.id)
                                    )

                                    // Wait for a specified interval before calling the function again
                                    delay(1000L)

                                    Log.d("Update application status", "Requests was sent! $user")
                                }
                            }

                            navController.navigate(NavigationPaths.PETS_SCREEN)
                        }
                        isLoading = false
                    } else {
                        isLoading = false
                        if (response != null) {
                            isNumberValid = response.isPhoneNumberValid
                            isPasswordValid = response.isPasswordValid
                            isPasswordCorrect = response.isPasswordCorrect
                        }
                    }
                }


                //TODO(Login user)
                //navController.navigate(NavigationPaths.PETS_SCREEN)
            },
            enabled = phoneNumber.isNotEmpty() && userPassword.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
            } else {
                Text("Войти")
            }

        }
    }
}