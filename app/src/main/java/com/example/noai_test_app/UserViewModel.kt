package com.example.noai_test_app

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.noai_test_app.data.AppDatabase
import com.example.noai_test_app.data.RetrofitClient
import com.example.noai_test_app.data.User
import com.example.noai_test_app.data.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()

    private val _usersList = mutableStateListOf<User>()
    val usersList: List<User> get() = _usersList

    var offlineMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchUsers()
    }

    fun fetchUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Fetch users using Retrofit
                val fetchedUsers = RetrofitClient.apiService.getUsers()

                // Cache in Room DB
                userDao.insertAll(fetchedUsers.map { UserEntity.fromUser(it) })

                withContext(Dispatchers.Main) {
                    _usersList.clear()
                    _usersList.addAll(fetchedUsers)
                    offlineMessage = null
                }
            } catch (e: Exception) {
                // Offline or error -> Load from cache
                val cachedEntities = userDao.getAllUsersSync()
                withContext(Dispatchers.Main) {
                    _usersList.clear()
                    _usersList.addAll(cachedEntities.map { it.toUser() })
                    offlineMessage = "⚠️ ไม่มีอินเทอร์เน็ต - กำลังแสดงข้อมูลแคชล่าสุดในเครื่อง"
                }
            }
        }
    }
}
