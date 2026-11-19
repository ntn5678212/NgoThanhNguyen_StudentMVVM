package com.example.studentmvvm.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.studentmvvm.model.Student
import kotlinx.coroutines.flow.Flow

@Dao
abstract class StudentDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertStudent(student: Student): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertAll(students: List<Student>)

    @Update
    abstract suspend fun updateStudent(student: Student): Int

    @Delete
    abstract suspend fun deleteStudent(student: Student)

    @Query("DELETE FROM students_table WHERE id = :id")
    abstract suspend fun deleteStudentById(id: Int)

    @Query("SELECT * FROM students_table ORDER BY id DESC")
    abstract fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students_table WHERE student_code = :code LIMIT 1")
    abstract suspend fun getStudentByCode(code: String): Student?

    @Transaction
    open suspend fun importNewStudents(students: List<Student>): Int {
        var inserted = 0

        for (student in students) {
            if (getStudentByCode(student.studentCode) == null) {
                insertStudent(student.copy(id = 0))
                inserted++
            }
        }

        return inserted
    }
}