package com.example.studentmvvm.network

import com.example.studentmvvm.model.Student
import java.util.Locale

data class StudentDto(
    val id: Int,
    val name: String,
    val email: String
)

fun StudentDto.toEntity(): Student = Student(
    fullName = name.trim(),
    studentCode = "API" + id.toString().padStart(4, '0'),
    email = email.trim().lowercase(Locale.ROOT),
    gpa = 0.0,
    major = "Dữ liệu API mẫu"
)