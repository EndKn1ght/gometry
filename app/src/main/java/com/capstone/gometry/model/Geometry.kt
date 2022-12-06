package com.capstone.gometry.model

import java.io.Serializable

data class Geometry(
    val id: String,
    val name: String,
    val preview: Int,
    val model3dUrl: String,
    val complete: Boolean,
): Serializable
