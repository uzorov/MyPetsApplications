package com.example.mypetsapplications.navigation.screens.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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

@Preview(showBackground = true)
@Composable
fun AnimalCardItemPreview() {
    AnimalCardItem(animal = Animal(), modifier = Modifier.width(100.dp))
}

@Composable
fun AnimalCardItem(animal: Animal, modifier: Modifier) {

    val painter: Painter = rememberImagePainter(
        data = animal.picture,
        builder = {
            //size()
            placeholder(R.drawable.loading_placeholder)
            error(R.drawable.error_)
            crossfade(true)
        }
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp,
        modifier = modifier

    ) {

        Column(modifier = Modifier.fillMaxWidth()) {


            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .align(Alignment.CenterHorizontally)
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {


                Text(
                    text = animal.name,
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Text(
                    text = animal.life_story,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(bottom = 16.dp),
                    maxLines = 5
                )
            }
        }
    }
}