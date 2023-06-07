package com.example.mypetsapplications.navigation.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.mypetsapplications.database.CredentialsDataViewModel
import com.example.mypetsapplications.database.model.Application
import com.example.mypetsapplications.database.model.ApplicationStatus
import com.example.mypetsapplications.database.model.Employee
import com.example.mypetsapplications.navigation.NavigationPaths
import com.example.mypetsapplications.navigation.SharedViewModel
import com.example.mypetsapplications.navigation.screens.items.ApplicationItem
import com.example.mypetsapplications.spacerHeight10
import com.example.mypetsapplications.spacerHeight20
import com.example.mypetsapplications.spacerHeight5
import com.example.mypetsapplications.ui.theme.BearsEar
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.cancel

@Composable
fun AdminScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    credentialsViewModel: CredentialsDataViewModel
) {

    val systemUiController = rememberSystemUiController()
    // Use SideEffect to update the system UI bar colors
    SideEffect {
        // Set the status bar color to magenta
        systemUiController.setStatusBarColor(
            color = BearsEar,
            darkIcons = false
        )
    }

    AdminScreenContent(navController, sharedViewModel, credentialsViewModel)
}

@Composable
fun AdminScreenContent(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    credentialsViewModel: CredentialsDataViewModel
) {

    val applicationCardOpenState = remember { mutableStateOf(false) }
    val selectedApplication by sharedViewModel.selectedApplication.observeAsState()

    val onApplicationTapped: (Application, Boolean) -> Unit =
        { application, appCardOpenState ->
            applicationCardOpenState.value = appCardOpenState
            sharedViewModel.selectedApplication.value = application
            Log.d("Sending Admin applications", "Chosen application: $application")

        }

    //getAllApplications
    val applications by sharedViewModel.applications.observeAsState()

    val needToProcessApplications: SnapshotStateList<Application> = remember {
        mutableStateListOf()
    }
    val processedApplications: SnapshotStateList<Application> = remember {
        mutableStateListOf()
    }

    val applicationsUpdate: suspend () -> Unit = {
        Log.d("updating applications", "applicationsUpdateIsWorking")
        sharedViewModel.saveAsApplications(
            credentialsViewModel.getAllApplicationsByTypeOfUser(sharedViewModel.currentEmployee.value!!.id, true)
        )
    }
    val onApplicationEditing: suspend (application: Application) -> Boolean =
        {
                Log.d("Sending Admin applications", "This what I get when asked for update: $it")

            credentialsViewModel.updateApplication(application = it)
        }

    val onApplicationDeleting: suspend (application: Application) -> Boolean =
        {
            Log.d("On deleting application", "OnApplicationDeleting is working $it")
            credentialsViewModel.deleteApplication(application = it)
        }


    LaunchedEffect(applications) {
        needToProcessApplications.clear()
        processedApplications.clear()
        applications?.forEach { application ->
            when (application.applicationStatus) {
                ApplicationStatus.SEND -> {
                    Log.d("Sending Admin applications", "This one not processed application: $application")
                    needToProcessApplications.add(application)
                }

                ApplicationStatus.PROCEED -> {
                    processedApplications.add(application)
                }

                ApplicationStatus.DECLINED -> {
                    processedApplications.add(application)
                }
            }
        }
    }

    //Shows up when user taps the application card and provides detailed info about the app
    OpenApplicationCardDetailed(
        cardOpenState = applicationCardOpenState,
        application = selectedApplication!!,
        onEditingApplication = onApplicationEditing,
        onDeletingApplication = onApplicationDeleting,
        applicationsUpdate = applicationsUpdate,
        isAdmin = true
    )


    LazyColumn(contentPadding = PaddingValues(8.dp))
    {
        item {
            spacerHeight20()
            spacerHeight20()
            spacerHeight20()
            AdminApplicationsSection(
                applications = needToProcessApplications,
                onApplicationTapped = onApplicationTapped,
                textForSection = "Новые заявки"
            )
            spacerHeight5()
            AdminApplicationsSection(
                applications = processedApplications,
                onApplicationTapped = onApplicationTapped,
                textForSection = "Обработанные заявки"
            )
        }
    }

    val employee by sharedViewModel.currentEmployee.observeAsState()
    topAppBar(employee = employee!!,
        onExitingAccount = {
            navController.popBackStack(NavigationPaths.AUTH_SCREEN, false)
            sharedViewModel.clearAllData()
        })
}

@Composable
fun AdminApplicationsSection(
    applications: List<Application>,
    onApplicationTapped: (Application, Boolean) -> Unit,
    textForSection : String
) {
    val itemSize: Dp = (LocalConfiguration.current.screenWidthDp.dp) - 10.dp

    Text(
        modifier = Modifier.padding(16.dp),
        text = textForSection,
        style = MaterialTheme.typography.h2,

        textAlign = TextAlign.Start,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )

    Column() {
        applications.forEach { application ->
            ApplicationItem(
                application = application, modifier = Modifier
                    .width(itemSize)
                    .padding(8.dp)
                    .clickable {
                        onApplicationTapped(application, true)
                    }
            )
        }

    }
}

@Composable
private fun topAppBar(
    employee: Employee,
    onExitingAccount: () -> Unit,
) {
    TopAppBar(
        modifier = Modifier.height(70.dp),
        backgroundColor = MaterialTheme.colors.background
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,

            ) {

            IconButton(onClick = onExitingAccount) {
                Icon(
                    Icons.Outlined.ExitToApp,
                    "Exit the account",
                    modifier = Modifier
                        .rotate(180f)

                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Text(
                    text = employee.name,
                    modifier = Modifier.padding(end = 5.dp),
                    style = MaterialTheme.typography.subtitle1,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light
                )

                Box(modifier = Modifier.clip(CircleShape)) {
                    //User picture
                    Icon(Icons.Outlined.Person, "Person Picture", modifier = Modifier.size(30.dp))
                }
            }
        }
    }
}
