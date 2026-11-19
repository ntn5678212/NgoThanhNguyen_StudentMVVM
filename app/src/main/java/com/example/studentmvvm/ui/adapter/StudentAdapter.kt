package com.example.studentmvvm.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ute.studentmvvm.databinding.ItemStudentRoomBinding
import com.example.studentmvvm.model.Student
import java.util.Locale

class StudentAdapter(
    private val onStudentClick: (Student) -> Unit,
    private val onDeleteClick: (Student) -> Unit
) : ListAdapter<Student, StudentAdapter.StudentViewHolder>(StudentDiffCallback()) {

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentRoomBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StudentViewHolder(
        private val binding: ItemStudentRoomBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(student: Student) = with(binding) {
            tvFullName.text = student.fullName
            tvStudentCode.text = "MSSV: ${student.studentCode}"
            tvEmail.text = student.email
            tvMajor.text = student.major
            tvGpa.text = String.format(Locale.US, "GPA: %.2f / 4", student.gpa)
            tvAvatar.text = student.fullName.trim().take(1).uppercase(Locale.ROOT)

            root.setOnClickListener { onStudentClick(student) }
            btnEdit.setOnClickListener { onStudentClick(student) }
            btnDelete.setOnClickListener { onDeleteClick(student) }
        }
    }

    private class StudentDiffCallback : DiffUtil.ItemCallback<Student>() {

        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean =
            oldItem == newItem
    }
}