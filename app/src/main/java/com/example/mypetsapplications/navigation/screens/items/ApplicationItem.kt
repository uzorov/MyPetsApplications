package com.example.mypetsapplications.navigation.screens.items

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.mypetsapplications.R
import com.example.mypetsapplications.database.model.Application
import com.example.mypetsapplications.database.model.ApplicationStatus
import com.example.mypetsapplications.ui.theme.SoftGreen
import com.example.mypetsapplications.ui.theme.SoftRed
import com.example.mypetsapplications.ui.theme.SoftYellow



@Composable
fun ApplicationItem(application: Application, modifier: Modifier) {



    val statusColor: MutableState<Color> = remember {
        mutableStateOf(
            when (application.applicationStatus) {
                ApplicationStatus.NOT_SEND -> SoftRed
                ApplicationStatus.SEND -> SoftYellow
                ApplicationStatus.PROCEED -> SoftGreen
                ApplicationStatus.DECLINED -> SoftRed
                else -> Color.Transparent
            }
        )
    }

    LaunchedEffect(application) {
        // Update the statusColor value based on the current application status
        statusColor.value = when (application.applicationStatus) {
            ApplicationStatus.NOT_SEND -> SoftRed
            ApplicationStatus.SEND -> SoftYellow
            ApplicationStatus.PROCEED -> SoftGreen
            ApplicationStatus.DECLINED -> SoftRed
            else -> Color.Transparent
        }
    }


    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp,
        modifier = modifier

    ) {
        Column() {

            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {

                val painter: Painter = rememberImagePainter(
                    data = application.animal.picture,
                    builder = {
                        //size()
                        placeholder(R.drawable.loading_placeholder)
                        error(R.drawable.error_)
                        crossfade(true)
                    }
                )

                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(shape = RoundedCornerShape(16.dp))
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Номер заявки: " + application.applicationId,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Text(
                    text = "Питомец: " + application.animal.name + " (" + application.animal.type + ") ",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Заявитель: " + application.client.name,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                if (application.employee != null) {
                    Text(
                        text = "Исполнитель: " + application.employee!!.name,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
            }

            Divider()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .height(50.dp)
                    .padding(0.dp)
                    .background(color = statusColor.value)
            )

        }


    }
}