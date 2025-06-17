package com.estholon.firebaseauthentication.data.mapper

import com.estholon.firebaseauthentication.data.dtos.UserDto
import com.estholon.firebaseauthentication.domain.models.UserModel

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