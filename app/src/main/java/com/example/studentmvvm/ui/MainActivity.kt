package com.example.studentmvvm.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.studentmvvm.data.AppDatabase
import com.example.studentmvvm.repository.StudentRepository
import com.example.studentmvvm.viewmodel.StudentViewModelFactory
import com.ute.studentmvvm.R

class MainActivity : AppCompatActivity() {

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory by lazy {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = StudentRepository(database.studentDao())
        StudentViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, StudentListFragment())
                .commit()
        }
    }
}