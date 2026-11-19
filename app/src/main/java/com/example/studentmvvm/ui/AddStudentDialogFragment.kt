package com.example.studentmvvm.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.studentmvvm.model.Student
import com.example.studentmvvm.util.StudentValidator
import com.example.studentmvvm.viewmodel.StudentViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ute.studentmvvm.databinding.DialogAddStudentBinding
import java.util.Locale
import java.util.UUID

class AddStudentDialogFragment : DialogFragment() {

    private var _binding: DialogAddStudentBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: StudentViewModel by activityViewModels()

    private var requestId = ""
    private val editingId get() = arguments?.getInt("id") ?: 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestId = savedInstanceState?.getString("requestId") ?: UUID.randomUUID().toString()

        viewModel.saveResult.observe(this) { result ->
            if (result.requestId == requestId) {
                if (result.error == null) {
                    dismiss()
                } else {
                    _binding?.tvError?.apply {
                        text = result.error
                        isVisible = true
                    }
                }
            }
        }

        viewModel.saving.observe(this) { busy ->
            updateSaving(busy)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext())
        _binding = DialogAddStudentBinding.inflate(LayoutInflater.from(builder.context))

        fun restoredText(key: String, fallback: String = ""): String =
            savedInstanceState?.getString(key) ?: arguments?.getString(key) ?: fallback

        binding.etFullName.setText(restoredText("name"))
        binding.etStudentCode.setText(restoredText("code"))
        binding.etEmail.setText(restoredText("email"))
        binding.etGpa.setText(restoredText("gpa"))
        binding.etMajor.setText(restoredText("major", "Công Nghệ Thông Tin"))

        return builder
            .setTitle(if (editingId == 0) "Thêm sinh viên mới" else "Cập nhật sinh viên")
            .setView(binding.root)
            .setNegativeButton("Hủy", null)
            .setPositiveButton("Lưu vào Room", null)
            .create()
    }

    override fun onStart() {
        super.onStart()

        val alert = requireDialog() as AlertDialog
        alert.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener { save() }
        updateSaving(viewModel.saving.value == true)
    }

    private fun save() = with(binding) {
        val name = etFullName.text.toString().trim()
        val code = etStudentCode.text.toString().trim().uppercase(Locale.ROOT)
        val email = etEmail.text.toString().trim().lowercase(Locale.ROOT)
        val gpaText = etGpa.text.toString().trim()
        val major = etMajor.text.toString().trim()

        tilFullName.error = StudentValidator.nameError(name)
        tilStudentCode.error = StudentValidator.codeError(code)
        tilEmail.error = StudentValidator.emailError(email)
        tilGpa.error = StudentValidator.gpaError(gpaText)
        tilMajor.error = StudentValidator.majorError(major)

        val fields = listOf(tilFullName, tilStudentCode, tilEmail, tilGpa, tilMajor)
        if (fields.any { it.error != null }) return@with

        val gpa = StudentValidator.parseGpa(gpaText) ?: return@with
        tvError.isVisible = false

        val student = Student(editingId, name, code, email, gpa, major)
        if (editingId == 0) {
            viewModel.insert(student, requestId)
        } else {
            viewModel.update(student, requestId)
        }
    }

    private fun updateSaving(busy: Boolean) {
        val ui = _binding ?: return
        isCancelable = !busy

        val alert = dialog as? AlertDialog
        alert?.getButton(DialogInterface.BUTTON_POSITIVE)?.isEnabled = !busy
        alert?.getButton(DialogInterface.BUTTON_NEGATIVE)?.isEnabled = !busy

        listOf(ui.etFullName, ui.etStudentCode, ui.etEmail, ui.etGpa, ui.etMajor)
            .forEach { field -> field.isEnabled = !busy }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("requestId", requestId)

        _binding?.let { ui ->
            outState.putString("name", ui.etFullName.text.toString())
            outState.putString("code", ui.etStudentCode.text.toString())
            outState.putString("email", ui.etEmail.text.toString())
            outState.putString("gpa", ui.etGpa.text.toString())
            outState.putString("major", ui.etMajor.text.toString())
        }

        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(student: Student? = null): AddStudentDialogFragment {
            return AddStudentDialogFragment().apply {
                arguments = Bundle().apply {
                    student?.let {
                        putInt("id", it.id)
                        putString("name", it.fullName)
                        putString("code", it.studentCode)
                        putString("email", it.email)
                        putString("gpa", it.gpa.toString())
                        putString("major", it.major)
                    }
                }
            }
        }
    }
}