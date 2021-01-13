package org.cyanotic.butterfly.features.loginLoading

sealed class LoginResult {
    data class Success(
        val token: String
    ): LoginResult()

    object Failed : LoginResult()
    object Canceled: LoginResult()
}