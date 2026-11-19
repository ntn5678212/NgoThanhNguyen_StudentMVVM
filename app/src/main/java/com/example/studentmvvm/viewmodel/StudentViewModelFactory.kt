package com.example.studentmvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studentmvvm.repository.StudentRepository

class StudentViewModelFactory(
    private val repository: StudentRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass == StudentViewModel::class.java) {
            return modelClass.cast(StudentViewModel(repository))!!
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}