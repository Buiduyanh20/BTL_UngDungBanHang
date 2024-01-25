package com.example.ungdungbanhang.util

import android.util.Patterns

// kiem tra email dau vao
fun validateEmail(email: String): RegisterValidation{
    if(email.isEmpty())
        return  RegisterValidation.Failed("Không thể để trống Email !")
    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Email bạn không đúng định dạng !")

    return RegisterValidation.Success
}

fun validateEmailAndPasswordLogin(email: String,password: String): RegisterValidation{
    if(email.isEmpty())
        return  RegisterValidation.Failed("Bạn chưa điền Email !")
    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Email bạn không đúng định dạng !")
    if(password.isEmpty())
        return RegisterValidation.Failed("Bạn chưa điền mật khẩu !")
    return RegisterValidation.Success
}

// kiem tra password
fun validatePassword(password: String): RegisterValidation{
    // do dai cua mat khau
    val minLength = 10;
    // phai co 1 chu hoa
    val hasUppercase = Regex("[A-Z]").containsMatchIn(password)
    // mat khau it nhat phai co mot so
    val hasDigit = Regex("\\d").containsMatchIn(password)
    // mat khau phai chua it nhat mot ky tu dac biet
    val hasSpecialChar = Regex("[!@#\$%^&*()_+\\-=\\[\\]{};':\",.<>?~\\\\]").containsMatchIn(password)

    if(password.isEmpty())
        return RegisterValidation.Failed("Không thể để trống mật khẩu!")
    if(password.length < minLength)
        return RegisterValidation.Failed("Mật khẩu phải chứa 10 ký tự.")
    if(!hasUppercase)
        return RegisterValidation.Failed("Mật khẩu phải chứa ít nhất một chữ hoa.")
    if(!hasDigit)
        return  RegisterValidation.Failed("Mật khẩu phải chứa ít nhất một chữ số.")
    if(!hasSpecialChar)
        return RegisterValidation.Failed("Mật khẩu phải chứa ít nhất một ký tự đặc biệt.")
    return RegisterValidation.Success
}