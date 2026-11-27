package com.example.noai_test_app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String
) {
    fun toUser(): User {
        return User(id, name, username, email, phone, website)
    }

    companion object {
        fun fromUser(user: User): UserEntity {
            return UserEntity(user.id, user.name, user.username, user.email, user.phone, user.website)
        }
    }
}
