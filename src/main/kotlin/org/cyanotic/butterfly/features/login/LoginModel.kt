package org.cyanotic.butterfly.features.login

import org.cyanotic.butterfly.protocol.Status

data class LoginModel(
    val username: String,
    val password: String,
    val rememberProfile: Boolean,
    val rememberPassword: Boolean,
    val accessAutomatically: Boolean,
    val isLoginEnabled: Boolean,
    val loginStatus: Status
)