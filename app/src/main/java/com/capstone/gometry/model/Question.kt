package com.capstone.gometry.model

data class Question(
    val image: String? = null,
    val question: String? = null,
    val options: List<String>? = null,
    val answer: String? = null,
    val geometryId: String? = null,
)