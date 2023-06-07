package com.example.mypetsapplications.database.model

import kotlinx.serialization.Serializable

@Serializable
data class Animal(
    val id: Int = -1,
    var name: String = "Jack",
    var age: Int = 2,
    var type: String = "Cat",
    val life_story: String = "He was a funny kitten and liked milk. Jack was always curious and loved to explore his surroundings. One day, he decided to go on an adventure to find the best milk in the world.\n" +
            "\n" +
            "Jack packed his little backpack with some treats and toys and set off on his journey. He walked through fields and forests, over hills and mountains, and across rivers and streams. Along the way, he met many other animals who were also on their own adventures.\n" +
            "\n" +
            "One day, Jack came across a farm where he could smell the sweet scent of fresh milk. He followed his nose and found a big barn where cows were being milked. Jack watched in amazement as the milk flowed from the cows into big buckets.\n" +
            "\n" +
            "The farmer noticed Jack and smiled. \"Hello there little kitten,\" he said. \"Are you looking for some milk?\"\n" +
            "\n" +
            "Jack meowed happily and nodded his head. The farmer poured some fresh milk into a bowl and gave it to Jack. It was the best milk he had ever tasted!\n" +
            "\n" +
            "From then on, Jack visited the farm every day to drink the delicious milk. He made friends with the cows and the other animals on the farm, and they all had many adventures together.\n" +
            "\n" +
            "Years passed and Jack grew into a big, strong cat. But he never forgot his adventure to find the best milk in the world, and he always remembered the kind farmer who had given him his first taste of it.\n" +
            "\n" +
            "And so, Jack lived happily ever after, surrounded by friends and always enjoying a bowl of fresh milk.",
    val picture: String = ""
)

