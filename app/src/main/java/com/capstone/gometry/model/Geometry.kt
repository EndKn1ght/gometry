package com.capstone.gometry.model

import java.io.Serializable

data class Geometry(
    val id: String,
    val name: String,
    val preview: Int,
    val image: Int,
    val theory: String,
    val model3dUrl: String,
    val passed: Boolean,
    val locked: Boolean,
): Serializable
