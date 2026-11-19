package com.example.studentmvvm.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentmvvm.model.Student
import com.example.studentmvvm.ui.adapter.StudentAdapter
import com.example.studentmvvm.viewmodel.StudentViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ute.studentmvvm.R
import com.ute.studentmvvm.databinding.FragmentStudentListBinding
import java.util.Locale

class StudentListFragment : Fragment() {

    private var _binding: FragmentStudentListBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: StudentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val studentAdapter = StudentAdapter(
            onStudentClick = ::showStudentDetail,
            onDeleteClick = ::confirmDelete
        )

        binding.rvStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStudents.adapter = studentAdapter

        binding.edtSearch.setText(viewModel.keyword.value.orEmpty())
        binding.edtSearch.doAfterTextChanged { text ->
            viewModel.keyword.value = text?.toString().orEmpty()
        }

        viewModel.visibleStudents.observe(viewLifecycleOwner) { students ->
            studentAdapter.submitList(students)
            binding.tvStudentCountBadge.text = "${students.size} kết quả"
            binding.layoutEmpty.isVisible = students.isEmpty()
            binding.layoutEmpty.text = if (viewModel.keyword.value.isNullOrBlank()) {
                "Chưa có sinh viên. Nhấn + để thêm."
            } else {
                "Không tìm thấy sinh viên phù hợp."
            }
        }

        viewModel.allStudents.observe(viewLifecycleOwner) { students ->
            val gpa = if (students.isEmpty()) 0.0 else students.map { it.gpa }.average()
            binding.tvStats.text = String.format(
                Locale.US,
                "%d sinh viên • GPA TB %.2f",
                students.size,
                gpa
            )
        }

        viewModel.statusMessage.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.syncing.observe(viewLifecycleOwner) { syncing ->
            binding.progress.isVisible = syncing
            binding.toolbar.menu.findItem(R.id.action_sync)?.isEnabled = !syncing
        }

        binding.fabAdd.setOnClickListener { openForm() }

        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_sync) {
                viewModel.syncFromCloud()
                true
            } else {
                false
            }
        }
    }

    private fun openForm(student: Student? = null) {
        val manager = childFragmentManager
        val tag = "STUDENT_FORM"
        if (manager.findFragmentByTag(tag) != null) return

        AddStudentDialogFragment.newInstance(student).showNow(manager, tag)
    }

    private fun showStudentDetail(student: Student) {
        val detail = """
            Họ tên: ${student.fullName}
            MSSV: ${student.studentCode}
            Email: ${student.email}
            GPA: ${student.gpa}
            Ngành: ${student.major}
        """.trimIndent()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Thông tin sinh viên")
            .setMessage(detail)
            .setNeutralButton("Chỉnh sửa") { _, _ -> openForm(student) }
            .setPositiveButton("Đóng", null)
            .show()
    }

    private fun confirmDelete(student: Student) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Xóa sinh viên ${student.fullName} khỏi Room?")
            .setNegativeButton("Hủy", null)
            .setPositiveButton("Xóa") { _, _ -> viewModel.delete(student) }
            .show()
    }

    override fun onDestroyView() {
        binding.rvStudents.adapter = null
        _binding = null
        super.onDestroyView()
    }
}