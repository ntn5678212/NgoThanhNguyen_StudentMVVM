package com.example.studentmvvm.repository

import com.example.studentmvvm.data.StudentDao
import com.example.studentmvvm.model.Student
import com.example.studentmvvm.network.RetrofitClient
import com.example.studentmvvm.network.StudentApiService
import com.example.studentmvvm.network.toEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class StudentRepository(
    private val studentDao: StudentDao,
    private val apiService: StudentApiService = RetrofitClient.apiService
) {

    val allStudents: Flow<List<Student>> = studentDao.getAllStudents()
    private val writeMutex = Mutex()

    suspend fun insert(student: Student): Long = writeMutex.withLock {
        require(studentDao.getStudentByCode(student.studentCode) == null) {
            "Mã sinh viên đã tồn tại"
        }

        studentDao.insertStudent(student)
    }

    suspend fun update(student: Student) = writeMutex.withLock {
        val existingStudent = studentDao.getStudentByCode(student.studentCode)

        require(existingStudent == null || existingStudent.id == student.id) {
            "Mã sinh viên đã tồn tại"
        }

        check(studentDao.updateStudent(student) == 1) {
            "Sinh viên không còn tồn tại"
        }
    }

    suspend fun delete(student: Student) = writeMutex.withLock {
        studentDao.deleteStudent(student)
    }

    suspend fun syncFromCloud(): Result<Int> {
        return try {
            val students = apiService.getStudents().map { it.toEntity() }
            val insertedCount = writeMutex.withLock {
                studentDao.importNewStudents(students)
            }

            Result.success(insertedCount)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Result.failure(error)
        }
    }
}