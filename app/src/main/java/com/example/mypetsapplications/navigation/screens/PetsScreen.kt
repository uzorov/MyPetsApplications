package com.example.mypetsapplications.navigation.screens

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.mypetsapplications.*
import com.example.mypetsapplications.R
import com.example.mypetsapplications.database.CredentialsDataViewModel
import com.example.mypetsapplications.database.model.Animal
import com.example.mypetsapplications.database.model.Application
import com.example.mypetsapplications.database.model.User
import com.example.mypetsapplications.navigation.NavigationPaths
import com.example.mypetsapplications.navigation.SharedViewModel
import com.example.mypetsapplications.navigation.screens.items.AnimalCardItem
import com.example.mypetsapplications.navigation.screens.items.ApplicationItem
import com.example.mypetsapplications.navigation.screens.items.DetailedAnimalCardItem
import com.example.mypetsapplications.navigation.screens.items.DetailedApplicationCardItem
import com.example.mypetsapplications.ui.theme.BearsEar
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun PetsScreen(
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

    val user by sharedViewModel.currentUser.observeAsState()

    val onNavigationToFillingTheApplication: () -> Unit =
        {
            navController.navigate(NavigationPaths.FILL_THE_APPLICATION_SCREEN)
        }

    PetsScreenContent(
        onNavigationToFillingTheApplication,
        sharedViewModel,
        credentialsDataViewModel
    )
    topAppBar(user!!) {
        navController.popBackStack(NavigationPaths.AUTH_SCREEN, false)
        sharedViewModel.clearAllData()
    }


}


@Composable
fun PetsScreenContent(
    onNavigationToFillingTheApplication: () -> Unit,
    sharedViewModel: SharedViewModel,
    credentialsViewModel: CredentialsDataViewModel,
) {


    val animalsCardOpenState = remember { mutableStateOf(false) }
    val selectedAnimal by sharedViewModel.selectedAnimal.observeAsState()

    val applications by sharedViewModel.applications.observeAsState()


    val onCardTapped: (Animal, Boolean) -> Unit =
        { animal, cardOpenState ->
            animalsCardOpenState.value = cardOpenState
            sharedViewModel.selectedAnimal.value = animal
        }

    val applicationCardOpenState = remember { mutableStateOf(false) }
    val selectedApplication by sharedViewModel.selectedApplication.observeAsState()

    val onApplicationTapped: (Application, Boolean) -> Unit =
        { application, appCardOpenState ->
            applicationCardOpenState.value = appCardOpenState
            sharedViewModel.selectedApplication.value = application
        }

    //Gain the list (12 items) of animals from database and then distribute them into different sections

    val listOfHeroesAnimals: MutableList<Animal> = mutableListOf()

    val listOfTheSmallestAnimals: MutableList<Animal> = mutableListOf()

    val listOfOtherAnimals: MutableList<Animal> = mutableListOf()

    val listOfAnimals by sharedViewModel.animals.observeAsState()

    listOfAnimals?.forEachIndexed { index, animal ->
        when (index) {
            0, 1 -> listOfHeroesAnimals.add(animal)
            2, 3, 4, 5 -> listOfTheSmallestAnimals.add(animal)
            6, 7, 8, 9, 10, 11, 12 -> listOfOtherAnimals.add(animal)
        }
    }

    /*val sendApplication: suspend () -> Boolean =
        {
            Log.d("Sending application", "Now user has this fields: " + user.toString())
            Log.d("Sending application", "Now animal has this fields: " + animal.toString())
            Log.d(
                "Sending application",
                "Now application has this fields: ${applicationData.value.toString()}"
            )

            sharedViewModel.createApplicationReceive(applicationData)
            credentialsDataViewModel.sendApplication(sharedViewModel.applicationToSend.value!!)
        }*/


    val applicationsUpdate: suspend () -> Unit = {
        Log.d("updating applications", "applicationsUpdateIsWorking")
        sharedViewModel.saveAsApplications(
            credentialsViewModel.getAllApplicationsByTypeOfUser(sharedViewModel.currentUser.value!!.id)
        )
    }
    val onApplicationEditing: suspend (application: Application) -> Boolean =
        {
            Log.d("Editing application", "Application now has next field: " + it)
            credentialsViewModel.updateApplication(application = it)
        }

    val onApplicationDeleting: suspend (application: Application) -> Boolean =
        {
            Log.d("On deleting application", "OnApplicationDeleting is working $it")
            credentialsViewModel.deleteApplication(application = it)
        }





    Box(modifier = Modifier.fillMaxSize()) {


        //Shows up when user taps the application card and provides detailed info about the app
        OpenApplicationCardDetailed(
            cardOpenState = applicationCardOpenState,
            application = selectedApplication!!,
            onEditingApplication = onApplicationEditing,
            onDeletingApplication = onApplicationDeleting,
            applicationsUpdate = applicationsUpdate
        )

        //Shows up when user taps the animal card and provides detailed information about the pet
        OpenAnimalsCardDetailed(
            animalsCardOpenState,
            selectedAnimal!!,
            onNavigationToFillingTheApplication
        )


        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {

            item {
                spacerHeight20()
                spacerHeight20()
                spacerHeight20()
                appTitle()
                spacerHeight5()
                PleaseBringThemHome(applications!!, onApplicationTapped)
                spacerHeight20()
                HeroesOfTheDay(listOfHeroesAnimals, onCardTapped)
                spacerHeight20()
                TheSmallestOnesSection(listOfTheSmallestAnimals, onCardTapped)
                spacerHeight20()
                TheyWannaFindHomeSection(listOfOtherAnimals, onCardTapped)
            }
        }
    }
}


/**

 * Composable function for the recommendation section of the Home Screen UI.
 * @param recommendationList The list of recommended recipes. */
@Composable
fun TheSmallestOnesSection(
    listOfTheSmallestAnimals: List<Animal>,
    onCardTapped: (Animal, Boolean) -> Unit,
) {

    //Width of the card
    val itemSize: Dp = (LocalConfiguration.current.screenWidthDp.dp / 2) + 23.dp
    val itemHeightSize: Dp = (LocalConfiguration.current.screenHeightDp.dp / 2) + 23.dp


    Column {
        Text(
            modifier = Modifier.padding(start = 15.dp),
            text = "Самые маленькие в приюте",
            style = MaterialTheme.typography.h2,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        spacerHeight10()
        LazyRow(
            contentPadding = PaddingValues(horizontal = 7.dp),
        ) {

            items(listOfTheSmallestAnimals.size)
            { index ->
                AnimalCardItem(
                    animal = listOfTheSmallestAnimals[index], modifier = Modifier
                        .width(itemSize)
                        .height(itemHeightSize)
                        .padding(8.dp)
                        .clickable {
                            onCardTapped(listOfTheSmallestAnimals[index], true)
                        }
                )
            }
        }
    }
}

/**

 * Composable function for the Best Recipes section of the Home Screen UI.
 * @param bestRecipesList The list of best recipes. */
@Composable
fun HeroesOfTheDay(
    animalsForHeroesOfTheDaySection: List<Animal>,
    onCardTapped: (Animal, Boolean) -> Unit,
) {

    //Width of the card
    val itemSize: Dp = (LocalConfiguration.current.screenWidthDp.dp / 2) - 15.dp
    val itemHeightSize: Dp = (LocalConfiguration.current.screenHeightDp.dp / 2) + 23.dp


    Text(
        modifier = Modifier.padding(start = 15.dp),
        text = "Герои дня",
        style = MaterialTheme.typography.h2,

        textAlign = TextAlign.Start,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )
    spacerHeight10()

    Row(
        modifier = Modifier.padding(horizontal = 15.dp)
    ) {


        animalsForHeroesOfTheDaySection.forEach { animal ->
            AnimalCardItem(
                animal = animal, modifier = Modifier
                    .width(itemSize)
                    .height(itemHeightSize)
                    .padding(8.dp)
                    .clickable {
                        onCardTapped(animal, true)
                    }
            )
        }
        //bestRecipesList.forEachIndexed { index, item ->
        //BestRecipeItem(
        //bestRecipe = bestRecipesList[index],
        //modifier = Modifier
        //.weight(1f)
        //.padding(end = 7.5.dp)
        //.aspectRatio(1f)
        //.clip(RoundedCornerShape(10.dp))
        //.background(item.background_color)
        //.wrapContentHeight()
        //.clickable
        // {
        //      selectedRecipe.value = bestRecipesList[index]
        //        recipeOpenState.value = true
        //      }, item.textColor

        //)
        // }
    }
}


@Composable
fun TheyWannaFindHomeSection(
    listOfAnimals: MutableList<Animal>,
    onCardTapped: (Animal, Boolean) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val state = rememberLazyListState()
    state.disableScrolling(scope)
    Column(
        modifier = Modifier.padding(horizontal = 7.5.dp)
    ) {
        Text(
            text = "Они хотят найти дом",
            style = MaterialTheme.typography.h2,
            textAlign = TextAlign.Start,
            fontSize = 24.sp,
            modifier = Modifier.padding(start = 7.5.dp),
            fontWeight = FontWeight.Bold,
        )
        spacerHeight10()
        val itemSize: Dp = (LocalConfiguration.current.screenWidthDp.dp / 2) - 10.dp
        val itemHeightSize: Dp = (LocalConfiguration.current.screenHeightDp.dp / 2) + 23.dp

        StaggeredVerticalGrid(maxColumnWidth = 250.dp) {

            listOfAnimals.forEach { animal ->
                AnimalCardItem(
                    animal = animal, modifier = Modifier
                        .width(itemSize)
                        .height(itemHeightSize)
                        .padding(8.dp)
                        .clickable {
                            onCardTapped(animal, true)
                        }
                )
            }

            //      masterChiefsRecipes.forEachIndexed { index, _ ->
            //        MasterChiefItem(
            //          masterChiefRecipe = masterChiefsRecipes[index],
            //        itemSize,
            //      Modifier
            //        .padding(horizontal = 7.5.dp, vertical = 7.5.dp)
            //      .clickable {
            //        selectedRecipe.value = masterChiefsRecipes[index]
            //      recipeOpenState.value = true
            //}
            // )
            // }
        }
    }
}


/**

 * Extension function for [LazyListState] that disables scrolling.
 * @param scope The coroutine scope to launch the scroll operation with. */
fun LazyListState.disableScrolling(scope: CoroutineScope) {
    scope.launch {
        scroll(scrollPriority = MutatePriority.PreventUserInput) {
            awaitCancellation()
        }
    }
}

/**

 * Composable function for the "Discover New Recipes" section of the Home Screen UI. */
@Composable
fun PleaseBringThemHome(
    applications: List<Application>,
    onApplicationTapped: (Application, Boolean) -> Unit,
) {

    val textSubtitleRes = stringResource(R.string.text_subtitle_bring_them_home)
    val textSubtitleResAlt = stringResource(R.string.text_subtitle_bth_alt)

    val textTitle =
        remember { mutableStateOf(if (applications.isEmpty()) "Знакомьтесь - питомцы" else "Ваши заявки") }
    val textSubtitle =
        remember {

            mutableStateOf(
                if (applications.isEmpty()) textSubtitleRes else textSubtitleResAlt
            )
        }

    Column(modifier = Modifier.fillMaxWidth()) {


        Column(
            modifier = Modifier.padding(horizontal = 15.dp)
        ) {
            Text(
                text = textTitle.value,
                style = MaterialTheme.typography.h1,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            )
            Text(
                text = textSubtitle.value,
                style = MaterialTheme.typography.body1,
                color = Gray,
                fontSize = 18.sp
            )
        }
        if (applications.isNotEmpty()) {
            spacerHeight10()
            ApplicationsSection(applications, onApplicationTapped)
        }
    }
}

@Composable
fun ApplicationsSection(
    applications: List<Application>,
    onApplicationTapped: (Application, Boolean) -> Unit,
) {
    val itemSize: Dp = (LocalConfiguration.current.screenWidthDp.dp) - 10.dp

    LazyRow() {

        item {
            applications.forEach { application ->
                ApplicationItem(
                    application = application,
                    modifier = Modifier
                        .width(itemSize)
                        .padding(8.dp)
                        .clickable {
                            onApplicationTapped(application, true)
                        },

                    )

            }
        }
    }
}

/**

 * Composable function for the app title section of the Home Screen UI. */
/*
@Composable
fun appTitle() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    )
    {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Icon",
            modifier = Modifier.size(150.dp).clip(CircleShape)
        )
    }
}*/


/*
@Composable
fun appTitle() {
    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Icon",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .graphicsLayer(rotationZ = angle)
        )
    }
}
*/


@Composable
fun appTitle() {
    var playAnimation by remember { mutableStateOf(false) }
    val angle by animateFloatAsState(
        targetValue = if (playAnimation) -15f else 0f,
        animationSpec = tween(durationMillis = 500, easing = LinearEasing)
    )

    LaunchedEffect(Unit) {
        repeat(3) {
            playAnimation = !playAnimation
            delay(500)
        }
        playAnimation = !playAnimation
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Icon",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .graphicsLayer(rotationZ = angle)
        )
    }
}

@Composable
private fun topAppBar(user: User, onExitingAccount: () -> Unit) {
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
                    text = user.name,
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

@Composable
fun OpenAnimalsCardDetailed(
    cardOpenState: MutableState<Boolean>,
    animal: Animal,
    onNavigationToFillingTheApplication: () -> Unit,
) {

    val itemSize: Dp = (LocalConfiguration.current.screenWidthDp.dp)

    if (cardOpenState.value and !animal.equals(null)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            Dialog(onDismissRequest = { cardOpenState.value = false }) {
                DetailedAnimalCardItem(
                    animal = animal, modifier = Modifier
                        .padding(8.dp)
                        .width(itemSize),
                    onNavigationToFillingTheApplication = onNavigationToFillingTheApplication
                ) {
                    cardOpenState.value = false
                }
            }
        }
    }
}


@Composable
fun OpenApplicationCardDetailed(
    cardOpenState: MutableState<Boolean>,
    application: Application,
    onEditingApplication: suspend (application: Application) -> Boolean,
    onDeletingApplication: suspend (application: Application) -> Boolean,
    applicationsUpdate: suspend () -> Unit,
    isAdmin: Boolean = false,
) {

    val itemSize: Dp = (LocalConfiguration.current.screenWidthDp.dp)

    if (cardOpenState.value and !application.equals(null)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            Dialog(onDismissRequest = { cardOpenState.value = false }) {
                DetailedApplicationCardItem(
                    isAdmin,
                    applicationsUpdate,
                    application = application,
                    modifier = Modifier
                        .padding(8.dp)
                        .width(itemSize),
                    onDismiss = { cardOpenState.value = false },
                    onApplicationEditing = onEditingApplication,
                    onApplicationDeleting = onDeletingApplication,

                    )
            }
        }
    }
}



