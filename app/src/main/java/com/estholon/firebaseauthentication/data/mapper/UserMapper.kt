package com.estholon.firebaseauthentication.data.mapper

import com.estholon.firebaseauthentication.data.dtos.UserDto
import com.estholon.firebaseauthentication.domain.models.UserModel
import com.google.firebase.auth.FirebaseUser

class UserMapper {

    fun userDtoToDomain(dto: UserDto): UserModel {
        return UserModel(
            uid = dto.uid,
            email = dto.email,
            displayName = dto.displayName,
            phoneNumber = dto.phoneNumber
        )
    }

    fun userDomainToDto(model: UserModel): UserDto {
        return UserDto(
            uid = model.uid,
            email = model.email,
            displayName = model.displayName,
            phoneNumber = model.phoneNumber
        )
    }
}

fun FirebaseUser.toUserDto() = UserDto(
    uid = uid,
    email = email,
    displayName = displayName,
    phoneNumber = phoneNumber
)