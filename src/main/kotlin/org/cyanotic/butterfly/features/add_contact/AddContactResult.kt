package org.cyanotic.butterfly.features.add_contact

sealed class AddContactResult {
    object Added: AddContactResult()
    object Canceled: AddContactResult()
}