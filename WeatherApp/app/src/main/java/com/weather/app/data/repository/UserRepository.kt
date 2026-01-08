package com.weather.app.data.repository

import com.weather.app.data.local.dao.UserDao
import com.weather.app.data.local.entity.UserEntity
import com.weather.app.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepository(private val userDao: UserDao) {

    suspend fun register(username: String, email: String, password: String): Resource<Long> {
        // Check if user exists
        if (userDao.getUserByUsername(username) != null) {
            return Resource.Error("Username already exists")
        }
        if (userDao.getUserByEmail(email) != null) {
            return Resource.Error("Email already registered")
        }

        // Create new user
        val user = UserEntity(
            username = username,
            email = email,
            passwordHash = password // TODO: Hash this in production!
        )

        return try {
            val id = userDao.insertUser(user)
            Resource.Success(id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Registration failed")
        }
    }

    suspend fun login(usernameOrEmail: String, password: String): Resource<UserEntity> {
        val user = if (usernameOrEmail.contains("@")) {
            userDao.getUserByEmail(usernameOrEmail)
        } else {
            userDao.getUserByUsername(usernameOrEmail)
        }

        if (user == null) {
            return Resource.Error("User not found")
        }

        if (user.passwordHash != password) {
            return Resource.Error("Invalid credentials")
        }

        return Resource.Success(user)
    }
}
