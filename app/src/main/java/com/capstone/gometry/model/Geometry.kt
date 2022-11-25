package com.capstone.gometry.model

import java.io.Serializable

data class Geometry(
    val id: String,
    val name: String,
    val colorScheme: String,
    val preview: Int,
): Serializable
