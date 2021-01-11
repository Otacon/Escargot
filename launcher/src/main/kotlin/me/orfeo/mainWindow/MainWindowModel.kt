package me.orfeo.mainWindow

import org.update4j.Configuration

data class MainWindowModel(
    val appHome: String,
    val status: String,
    val progress: Int,
    val error: String?,
    val isLaunchButtonEnabled: Boolean,
    val isUpdateButtonEnabled: Boolean,
    val isRemoveDataButtonEnabled: Boolean,
    val configuration: Configuration?
)