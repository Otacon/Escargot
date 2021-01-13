package org.cyanotic.butterfly.features.loginLoading

data class LoginLoadingModel(
    val username: String,
    val password: String,
    val text: String,
    val okVisible: Boolean,
    val cancelVisible: Boolean,
    val retryVisible: Boolean,
    val progressVisible: Boolean
)