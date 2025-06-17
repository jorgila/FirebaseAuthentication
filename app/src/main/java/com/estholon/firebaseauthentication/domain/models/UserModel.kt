package com.estholon.firebaseauthentication.domain.models

data class UserModel(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val phoneNumber: String?
)