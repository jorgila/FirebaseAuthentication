package com.estholon.firebaseauthentication.data.mapper

import com.estholon.firebaseauthentication.data.dtos.UserDto
import com.estholon.firebaseauthentication.domain.models.UserModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UserMapperTest {

    private lateinit var userMapper: UserMapper

    @Before
    fun setup(){
        userMapper = UserMapper()
    }

    @Test
    fun mapUserDtoToUserModelSuccessfully(){

        // Given
        val userDto = UserDto(
            uid = "user123",
            email = "test@example.com",
            displayName = "John Doe",
            phoneNumber = "+1234567890"
        )

        // When
        val userModel = userMapper.userDtoToDomain(userDto)

        // Then
        assertEquals("user123", userModel.uid)
        assertEquals("test@example.com", userModel.email)
        assertEquals("John Doe", userModel.displayName)
        assertEquals("+1234567890", userModel.phoneNumber)

    }

    @Test
    fun mapUserModelToUserDtoSuccessfully(){
        // Given
        val userModel = UserModel(
            uid = "model456",
            email = "model@test.com",
            displayName = "Jane Smith",
            phoneNumber = "+0987654321"
        )

        // When
        val userDto = userMapper.userDomainToDto(userModel)

        // Then
        assertEquals("model456", userDto.uid)
        assertEquals("model@test.com", userDto.email)
        assertEquals("Jane Smith", userDto.displayName)
        assertEquals("+0987654321", userDto.phoneNumber)
    }

    @Test
    fun handleNullEmailAndPhoneUserDtoToUserModel(){
        // Given
        val userDto = UserDto(
            uid = "user789",
            email = null,
            displayName = "Anonymous User",
            phoneNumber = null
        )

        // When
        val userModel = userMapper.userDtoToDomain(userDto)

        // Then
        assertEquals("user789", userModel.uid)
        assertNull(userModel.email)
        assertEquals("Anonymous User", userModel.displayName)
        assertNull(userModel.phoneNumber)
    }

    @Test
    fun handleNullDisplayNameUserModelToUser(){
        // Given
        val userModel = UserModel(
            uid = "model999",
            email = "user@domain.com",
            displayName = null,
            phoneNumber = "+1111111111"
        )

        // When
        val userDto = userMapper.userDomainToDto(userModel)

        // Then
        assertEquals("model999", userDto.uid)
        assertEquals("user@domain.com", userDto.email)
        assertNull(userDto.displayName)
        assertEquals("+1111111111", userDto.phoneNumber)
    }

    @Test
    fun handleNullAllOptionalFields(){
        // Given
        val userDto = UserDto(
            uid = "minimal",
            email = null,
            displayName = null,
            phoneNumber = null
        )

        // When
        val userModel = userMapper.userDtoToDomain(userDto)
        val mappedBackDto = userMapper.userDomainToDto(userModel)

        // Then
        assertEquals("minimal", userModel.uid)
        assertNull(userModel.email)
        assertNull(userModel.displayName)
        assertNull(userModel.phoneNumber)

        assertEquals(userDto.uid, mappedBackDto.uid)
        assertEquals(userDto.email, mappedBackDto.email)
        assertEquals(userDto.displayName, mappedBackDto.displayName)
        assertEquals(userDto.phoneNumber, mappedBackDto.phoneNumber)
    }

    @Test
    fun maintainDataIntegrityInRoundTripMapping(){
        //Given
        val originalDto = UserDto(
            uid = "roundtrip",
            email = "roundtrip@example.com",
            displayName = "Round Trip User",
            phoneNumber = "+34123456789"
        )
        //When
        val model = userMapper.userDtoToDomain(originalDto)
        val finalDto = userMapper.userDomainToDto(model)
        //Then
        assertEquals(originalDto.uid,finalDto.uid)
        assertEquals(originalDto.email,finalDto.email)
        assertEquals(originalDto.displayName,finalDto.displayName)
        assertEquals(originalDto.phoneNumber,finalDto.phoneNumber)
    }

}