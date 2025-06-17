package com.estholon.firebaseauthentication.data.dtos

data class UserDto(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val phoneNumber: String?
)