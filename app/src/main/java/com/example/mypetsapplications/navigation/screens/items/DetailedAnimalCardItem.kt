package com.example.mypetsapplications.navigation.screens.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.mypetsapplications.R
import com.example.mypetsapplications.database.model.Animal
import com.example.mypetsapplications.spacerHeight20

@Preview(showBackground = true)
@Composable
fun DetailedAnimalCardItemPreview() {
    AnimalCardItem(animal = Animal(), modifier = Modifier.width(100.dp))
}

@Composable
fun DetailedAnimalCardItem(
    animal: Animal,
    modifier: Modifier,
    onNavigationToFillingTheApplication: () -> Unit,
    onDismiss: () -> Unit,
) {
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
                    text = animal.name,
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(all = 8.dp)
                )
            }

            Divider()
            LazyColumn(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 0.dp)
                //  .scrollable(rememberScrollState(), orientation = Orientation.Vertical)
            ) {
                item {

                    val painter: Painter = rememberImagePainter(
                        data = animal.picture,
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
                            .height(200.dp)
                            .clip(shape = RoundedCornerShape(16.dp))
                            .padding(vertical = 8.dp)
                            ,
                        contentScale = ContentScale.Crop

                    )

                    Text(
                        text = animal.name,
                        style = MaterialTheme.typography.h5,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Row(Modifier.padding(horizontal = 8.dp)) {
                        Text(
                            text = animal.name, style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
                        )
                        Text(
                            text = animal.age.toString(), style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
                        )
                        Text(
                            text = animal.type, style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }



                    Text(
                        text = animal.life_story,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )


                    spacerHeight20()
                    spacerHeight20()


                }
            }


        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart)
        {

            Column() {
                Divider()

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

                    TextButton(onClick = onNavigationToFillingTheApplication) //Goes to fill the application
                    {
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(text = "Подать заявку")
                            Icon(
                                imageVector = Icons.Outlined.Create,
                                contentDescription = "List",
                                modifier = Modifier.padding(start = 8.dp)
                            )


                        }

                    }
                }
            }
        }
    }
}