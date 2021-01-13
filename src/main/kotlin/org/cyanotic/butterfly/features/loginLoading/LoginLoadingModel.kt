package org.cyanotic.butterfly.features.loginLoading

import org.cyanotic.butterfly.protocol.Status

data class LoginLoadingModel(
    val username: String,
    val password: String,
    val status: Status,
    val text: String,
    val okVisible: Boolean,
    val cancelVisible: Boolean,
    val retryVisible: Boolean,
    val progressVisible: Boolean
)