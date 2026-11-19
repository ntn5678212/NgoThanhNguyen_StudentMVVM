package com.example.studentmvvm.util

import android.util.Patterns

object StudentValidator {

    private val codeRegex = Regex("""^[A-Z0-9]{5,15}$""")
    private val nameRegex = Regex("""^[\p{L}\p{M}]+(?:\.?[ '\-][\p{L}\p{M}]+)*\.?$""")

    fun codeError(value: String): String? = when {
        value.isBlank() -> "Vui lòng nhập mã sinh viên"
        !codeRegex.matches(value) -> "Mã gồm 5–15 chữ A–Z hoặc số"
        else -> null
    }

    fun nameError(value: String): String? = when {
        value.length !in 2..80 -> "Họ tên gồm 2–80 ký tự"
        !nameRegex.matches(value) -> "Họ tên không chứa số hoặc ký tự lạ"
        else -> null
    }

    fun emailError(value: String): String? = when {
        value.isBlank() -> "Vui lòng nhập email"
        value.length > 100 || !Patterns.EMAIL_ADDRESS.matcher(value).matches() ->
            "Email chưa đúng định dạng"
        else -> null
    }

    fun majorError(value: String): String? =
        if (value.length in 2..80) null else "Ngành học gồm 2–80 ký tự"

    fun parseGpa(value: String): Double? =
        value.replace(',', '.').toDoubleOrNull()

    fun gpaError(value: String): String? {
        val number = parseGpa(value)

        return when {
            value.isBlank() -> "Vui lòng nhập GPA"
            number == null || !number.isFinite() -> "GPA phải là số hợp lệ"
            number !in 0.0..4.0 -> "GPA phải từ 0 đến 4"
            else -> null
        }
    }
}