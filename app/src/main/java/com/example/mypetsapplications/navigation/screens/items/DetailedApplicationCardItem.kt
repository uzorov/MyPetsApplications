package com.example.mypetsapplications.navigation.screens.items

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mypetsapplications.database.model.Application
import com.example.mypetsapplications.database.model.ApplicationStatus
import com.example.mypetsapplications.navigation.screens.AdvancedTextField
import com.example.mypetsapplications.navigation.screens.AnimalDataSection
import com.example.mypetsapplications.navigation.screens.MoreDataSection
import com.example.mypetsapplications.navigation.screens.UserDataSection
import com.example.mypetsapplications.spacerHeight10
import com.example.mypetsapplications.spacerHeight20
import com.example.mypetsapplications.ui.theme.SoftRed
import kotlinx.coroutines.launch


@Composable
fun DetailedApplicationCardItem(
    isAdmin: Boolean,
    applicationsUpdate: suspend () -> Unit,
    application: Application,
    modifier: Modifier,
    onDismiss: () -> Unit,
    onApplicationDeleting: suspend (application: Application) -> Boolean,
    onApplicationEditing: suspend (application: Application) -> Boolean,

    ) {

    val isConfirmOptionBoxOpened = remember { mutableStateOf(false) }

    val onConfirmOptionDismiss: () -> Unit = {
        isConfirmOptionBoxOpened.value = false
    }

    val onHittingDeletionButton: () -> Unit = {
        isConfirmOptionBoxOpened.value = true
    }

    var isUserWantToDeleteApplication by remember {
        mutableStateOf(false)
    }
    val onConfirmingDeletion: () -> Unit = {
        isUserWantToDeleteApplication = true
        isConfirmOptionBoxOpened.value = false
    }




    confirmDeletionOption(
        isConfirmOptionBoxOpened,
        onConfirmOptionDismiss,
        onConfirmingDeletion,
        application
    )
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp,
        modifier = modifier

    ) {
        Column {


            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {

                IconButton(onClick = onDismiss)
                {
                    Icon(imageVector = Icons.Outlined.Close, contentDescription = "Close the card")
                }

                Text(
                    text = "Заявка " + application.applicationId,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(all = 16.dp)
                )
            }

            Divider()
            LazyColumn(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 0.dp)
                //  .scrollable(rememberScrollState(), orientation = Orientation.Vertical)
            ) {
                item {

                    UserDataSection(user = application.client)
                    spacerHeight10()
                    AnimalDataSection(animal = application.animal)
                    spacerHeight10()
                    MoreDataSection(application)
                    spacerHeight10()
                    ProcessingStatusSection(application = application, isAdmin)

                    spacerHeight20()
                    spacerHeight20()


                }
            }


        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart)
        {

            Column() {
                Divider()

                ButtonsEditOrDeleteOrCancelSection(
                    applicationsUpdate,
                    application = application,
                    onDismiss = onDismiss,
                    onHitApplication = onHittingDeletionButton,
                    onApplicationDeleting = onApplicationDeleting,
                    onApplicationEditing = onApplicationEditing,
                    isUserWantToDeleteApplication = isUserWantToDeleteApplication,

                    ) { isUserWantToDeleteApplication = false }
            }
        }
    }
}

@Composable
fun ButtonsEditOrDeleteOrCancelSection(
    applicationsUpdate: suspend () -> Unit,
    application: Application,
    onDismiss: () -> Unit,
    onApplicationDeleting: suspend (application: Application) -> Boolean,
    onHitApplication: () -> Unit,
    onApplicationEditing: suspend (application: Application) -> Boolean,
    isUserWantToDeleteApplication: Boolean,
    isUserWantToDeleteApplicationChangeOnFalse: () -> Unit,
) {

    var isLoading by remember {
        mutableStateOf(false)
    }
    Row(
        Modifier
            .padding(end = 8.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.End
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                //.padding(16.dp)
                .fillMaxWidth()
                .background(color = MaterialTheme.colors.background)
        ) {
            IconButton(onClick = onDismiss)
            {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back"
                )
            }


            val scope = rememberCoroutineScope()

            IconButton(onClick = {
                isLoading = true
                scope.launch {
                    val result = onApplicationEditing(application)
                    if (result) {
                        onDismiss()
                        applicationsUpdate()
                    }
                    isLoading = false
                }
            })
            {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom
                ) {

                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colors.onPrimary,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit icon",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }


                }

            }


            LaunchedEffect(isUserWantToDeleteApplication) {

                if (isUserWantToDeleteApplication) {

                    Log.d("On Deleting application", "Now LaunchedEffectBeginning")
                    isLoading = true
                    val response = onApplicationDeleting(application)

                    if (response) {
                        Log.d("On Deleting application", "Response succesful $response")
                        onDismiss()
                        applicationsUpdate()
                    }
                    Log.d("On Deleting application", "Response not succesfull ( $response")
                    isLoading = false
                    isUserWantToDeleteApplicationChangeOnFalse()
                }
            }

            IconButton(onClick = onHitApplication)
            {

                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Bottom
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colors.onPrimary,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "DeleteIcon",
                            modifier = Modifier.padding(start = 8.dp), tint = SoftRed
                        )
                    }


                }

            }
        }

    }
}

@Composable
fun ProcessingStatusSection(application: Application, isAdmin: Boolean) {

    Column(modifier = Modifier.padding(16.dp)) {


        Text(
            text = "Обработка заявки",
            style = MaterialTheme.typography.h1,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
        )


        //If employee sees that that than
        if (isAdmin) {

            AdvancedTextField(
                nameOfTheField = "Ваш комментарий",
                valueOfTheField = if (application.employeeComment != null) application.employeeComment!! else "",
                onValueChanges = { application.employeeComment = it })

            EmployeeChoiceMenu(application = application)

        } else {
            if (application.employee != null) {
                AdvancedTextField(
                    nameOfTheField = "Исполнитель", valueOfTheField = application.employee!!.name,
                    readOnly = true
                )

                AdvancedTextField(
                    nameOfTheField = "Должность", valueOfTheField = application.employee!!.position,
                    readOnly = true
                )

                AdvancedTextField(
                    nameOfTheField = "Контактный номер",
                    valueOfTheField = application.employee!!.phone_number,
                    readOnly = true
                )

                AdvancedTextField(
                    nameOfTheField = "Почта", valueOfTheField = application.employee!!.mail,
                    readOnly = true
                )
            }


            AdvancedTextField(
                nameOfTheField = "Статус заявки",
                valueOfTheField = application.applicationStatus,
                readOnly = true
            )

            if (application.applicationStatus == ApplicationStatus.PROCEED || application.applicationStatus == ApplicationStatus.DECLINED) {

                AdvancedTextField(
                    nameOfTheField = "Комментарий сотрудника",
                    valueOfTheField = if (application.employee != null) application.employeeComment!! else "",
                    readOnly = true
                )

            }

        }
    }
}

@Composable
fun confirmDeletionOption(
    confirmOptionOpenedState: MutableState<Boolean>,
    onConfirmOptionDismiss: () -> Unit,
    onConfirmingDeletion: () -> Unit,
    application: Application,
) {
    if (confirmOptionOpenedState.value) {
        Dialog(onDismissRequest = onConfirmOptionDismiss) {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .height(180.dp)
                    .background(MaterialTheme.colors.background)
                    .clip(RoundedCornerShape(16.dp))
            ) {

                Column(
                    verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(text = "Вы уверены, что хотите удалить заявку?")

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                    ) {

                        TextButton(onClick = onConfirmOptionDismiss) {
                            Text(text = "Отмена")
                        }


                        TextButton(onClick = onConfirmingDeletion) {
                            Text(text = "Подтвердить")
                        }
                    }
                }
            }
        }

    }
}


@Composable
fun EmployeeChoiceMenu(application: Application) {
    // A state variable to control the visibility of the menu
    var expanded by remember { mutableStateOf(false) }
    // The list of options for the menu
    val options = listOf(ApplicationStatus.PROCEED, ApplicationStatus.DECLINED)
    // A button to open or close the menu

    Box() {
        Button(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(application.applicationStatus)
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Expand menu"
            )
        }

        Box() {
            // The menu itself
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                // Loop through the options and create a menu item for each one
                options.forEach { option ->
                    DropdownMenuItem(onClick = {
                        // Update the selected option and close the menu
                        application.applicationStatus = option
                        expanded = false
                    }) {
                        Text(option)
                    }
                }
            }
        }
    }
}