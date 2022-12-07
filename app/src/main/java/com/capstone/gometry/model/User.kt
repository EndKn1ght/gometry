package com.capstone.gometry.model

data class User(
    val id: String? = null,
    val displayName: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
    val point: Int? = null,
    val achievements: List<String>? = null,
    val geometries: List<String>? = null,
)
