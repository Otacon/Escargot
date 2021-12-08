package org.cyanotic.butterfly.features

import org.cyanotic.butterfly.protocol.Status

data class Account(
    val passport: String,
    val password: String,
    val lastLogin: Long,
    val rememberPassport: Boolean,
    val rememberPassword: Boolean,
    val loginAutomatically: Boolean
)

data class Contact(
    val passport: String,
    val nickname: String,
    val status: Status,
    val personalMessage: String
)