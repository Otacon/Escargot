package me.orfeo.mainWindow

import java.net.URI

interface MainWindowContract {

    interface View {
        fun setStatus(status: String)
        fun setProgress(progress: Int)
        fun setError(error: String?)
        fun showError(show: Boolean)
        fun setLaunchButtonEnabled(enabled: Boolean)
        fun setUpdateButtonEnabled(enabled: Boolean)
        fun setRemoveDataButtonEnabled(enabled: Boolean)
        fun close()
        fun openFileManager(appHome: URI)
    }

    interface Presenter{
        fun onCreate()
        fun onLaunchClicked()
        fun onUpdateClicked()
        fun onOpenFilesClicked()
        fun onWindowFocussed()
    }
}