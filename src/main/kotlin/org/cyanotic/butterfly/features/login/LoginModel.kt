package org.cyanotic.butterfly.features.login

data class LoginModel(
    val username: String,
    val password: String,
    val rememberProfile: Boolean,
    val rememberPassword: Boolean,
    val accessAutomatically: Boolean,
    val isLoginEnabled: Boolean,
)