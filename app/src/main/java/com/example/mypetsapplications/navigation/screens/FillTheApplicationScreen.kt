package com.example.mypetsapplications.navigation.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.mypetsapplications.R
import com.example.mypetsapplications.database.CredentialsDataViewModel
import com.example.mypetsapplications.database.model.Animal
import com.example.mypetsapplications.database.model.Application
import com.example.mypetsapplications.database.model.ApplicationReceive
import com.example.mypetsapplications.database.model.User
import com.example.mypetsapplications.navigation.NavigationPaths
import com.example.mypetsapplications.navigation.SharedViewModel
import com.example.mypetsapplications.spacerHeight10
import com.example.mypetsapplications.spacerHeight5
import com.example.mypetsapplications.ui.theme.BearsEar
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun FillTheApplicationScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    credentialsDataViewModel: CredentialsDataViewModel,
) {

    val systemUiController = rememberSystemUiController()
    // Use SideEffect to update the system UI bar colors
    SideEffect {
        // Set the status bar color to magenta
        systemUiController.setStatusBarColor(
            color = BearsEar,
        )
    }

    FillTheApplicationScreenContent(
        navController = navController,
        sharedViewModel = sharedViewModel,
        credentialsDataViewModel = credentialsDataViewModel,


        )
}

@Composable
fun FillTheApplicationScreenContent(
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
    credentialsDataViewModel: CredentialsDataViewModel,
) {

    val user by sharedViewModel.currentUser.observeAsState()
    val animal by sharedViewModel.selectedAnimal.observeAsState()
    val applicationData = remember {
        mutableStateOf(Application())
    }
    val applicationToSend by sharedViewModel.applicationToSend.observeAsState()


    val sendApplication: suspend () -> Boolean =
        {
            Log.d("Sending application", "Now user has this fields: " + user.toString())
            Log.d("Sending application", "Now animal has this fields: " + animal.toString())
            Log.d(
                "Sending application",
                "Now application has this fields: ${applicationData.value.toString()}"
            )

            sharedViewModel.createApplicationReceive(applicationData)
            credentialsDataViewModel.sendApplication(sharedViewModel.applicationToSend.value!!)
        }

    val navigateToPetsScreenAndUpdateAppsSection: suspend () -> Unit =
        {
            val applications =
                credentialsDataViewModel.getAllApplicationsByTypeOfUser(sharedViewModel.currentUser.value!!.id)
            sharedViewModel.saveAsApplications(applications)
            navController.popBackStack(NavigationPaths.PETS_SCREEN, false)
        }


    Column() {


        topAppBar() {
            navController.navigate(NavigationPaths.PETS_SCREEN)
        }

        LazyColumn(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {
            item {
                FillTheApplicationTextSection()
                spacerHeight5()
                UserDataSection(user = user!!)
                spacerHeight5()
                AnimalDataSection(animal = animal!!)
                spacerHeight5()
                MoreDataSection(applicationData.value)
                spacerHeight5()
                SendButton(
                    sendApplication = sendApplication,
                    application = applicationToSend!!,
                    lifecycleScope = sharedViewModel.viewModelScope,
                    navigateToPetsScreenAndUpdateAppsSection = navigateToPetsScreenAndUpdateAppsSection
                )
                spacerHeight10()


            }
        }
    }
}




@Composable
private fun topAppBar(GoToThePetsScreen: () -> Unit) {
    TopAppBar(
        modifier = Modifier.height(70.dp),
        backgroundColor = MaterialTheme.colors.background
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {


            IconButton(onClick = GoToThePetsScreen) {
                Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "Arrow Back")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Text(
                    text = "Подать заявку",
                    modifier = Modifier.padding(end = 5.dp),
                    style = MaterialTheme.typography.subtitle1,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light
                )

                Box(modifier = Modifier.clip(CircleShape)) {
                    //User picture
                    Icon(
                        Icons.Outlined.Create,
                        "Application Picture",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }


    }
}

@Composable
fun FillTheApplicationTextSection() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Заполните заявку",
            style = MaterialTheme.typography.h1,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
        )
        Text(
            text = stringResource(R.string.requirements),
            style = MaterialTheme.typography.body1,
            color = Color.Gray,
            fontSize = 18.sp
        )
    }
}


@Composable
fun UserDataSection(user: User) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Заявитель",
            style = MaterialTheme.typography.h1,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
        )

        AdvancedTextField(
            nameOfTheField = "ФИО", valueOfTheField = user.name,
            keyboardOptions_ = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
            ),
            onValueChanges =  { user.name = it},
            readOnly = true
        )
        AdvancedTextField(
            nameOfTheField = "Номер", valueOfTheField = user.phone,
            keyboardOptions_ = KeyboardOptions(
                keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next
            ),
            onValueChanges =  { user.phone = it},
            readOnly = true
        )
     /*   AdvancedTextField(
            nameOfTheField = "Почта",
            valueOfTheField = user.mail,
            keyboardOptions_ = KeyboardOptions(
                keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
            ),

           onValueChanges =  { user.mail = it}
        )*/
    }
}


@Composable
fun AnimalDataSection(animal: Animal) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Питомец",
            style = MaterialTheme.typography.h1,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
        )

        val itemHeightSize: Dp = (LocalConfiguration.current.screenHeightDp.dp / 4)
        val itemWidthSize: Dp = (LocalConfiguration.current.screenWidthDp.dp) - 40.dp

        val painter: Painter = rememberImagePainter(
            data = animal.picture,
            builder = {
                size(itemWidthSize.value.toInt(), itemHeightSize.value.toInt())
                placeholder(R.drawable.loading_placeholder)
                error(R.drawable.error_)
                crossfade(true)
            }
        )


        Image(
            painter = painter,
            contentDescription = "Animal Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = itemWidthSize, height = itemHeightSize)
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .clip(
                    RoundedCornerShape(8.dp)
                )

        )

        AdvancedTextField(
            nameOfTheField = "Имя",
            valueOfTheField = animal.name,
            readOnly = true,
            onValueChanges = { animal.name = it })
        AdvancedTextField(
            nameOfTheField = "Тип",
            valueOfTheField = animal.type,
            readOnly = true,
            onValueChanges = { animal.type = it })
        AdvancedTextField(
            nameOfTheField = "Возраст",
            valueOfTheField = animal.age.toString(),
            readOnly = true,
            onValueChanges = { animal.age = it.toInt() }
        )
    }
}

@Composable
fun MoreDataSection(application: Application = Application()) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Дополнительно",
            style = MaterialTheme.typography.h1,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
        )

        AdvancedTextField(
            nameOfTheField = "Город",
            valueOfTheField = application.usersCity,
            keyboardOptions_ = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
            ),

            onValueChanges = { application.usersCity = it }

        )
        AdvancedTextField(
            nameOfTheField = "Жилищные условия", valueOfTheField = application.hosingConditions,
            keyboardOptions_ = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
            ),
            onValueChanges = { application.hosingConditions = it }
        )
        AdvancedTextField(
            nameOfTheField = "О себе", valueOfTheField = application.aboutTheClient,
            keyboardOptions_ = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
            ),
            onValueChanges = { application.aboutTheClient = it }
        )
    }
}

@Composable
fun AdvancedTextField(
    nameOfTheField: String,
    valueOfTheField: String,
    readOnly: Boolean = false,
    namesShouldBeVisible: Boolean = true,
    keyboardOptions_: KeyboardOptions = KeyboardOptions.Default,
    onValueChanges: (String) -> Unit = {},
) {
    var fieldValue by remember { mutableStateOf(valueOfTheField) }
    val finalNameOfTheField = if (namesShouldBeVisible) "$nameOfTheField:" else ""

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = finalNameOfTheField,
            style = MaterialTheme.typography.h4,
            fontSize = 18.sp,
            modifier = Modifier
                .padding(bottom = 8.dp)

        )


        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            readOnly = readOnly,
            value = fieldValue,
            onValueChange = {it ->
                fieldValue = it
                onValueChanges(it)},
            textStyle = MaterialTheme.typography.body1,
            keyboardOptions = keyboardOptions_,

            )
    }
}

@Composable
fun SendButton(
    sendApplication: suspend () -> Boolean,
    application: ApplicationReceive,
    lifecycleScope: CoroutineScope,
    navigateToPetsScreenAndUpdateAppsSection: suspend () -> Unit,
) {
    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    Row(
        Modifier
            .padding(end = 8.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.End
    ) {
        OutlinedButton(
            onClick = {

                isLoading = true
                lifecycleScope.launch {
                    val result = sendApplication()

                    if (!result) {
                        isLoading = false
                        isError = true
                    } else {
                        navigateToPetsScreenAndUpdateAppsSection()
                    }

                }


            },
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colors.onPrimary,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                } else if (isError) {
                    Text(text = "Ошибка", modifier = Modifier.padding(end = 8.dp))
                    Icon(imageVector = Icons.Outlined.Warning, contentDescription = "Error icon")

                } else {
                    Text(text = "Отправить", modifier = Modifier.padding(end = 8.dp))
                    Icon(imageVector = Icons.Outlined.Send, contentDescription = "Send icon")

                }

            }
        }
    }
}
