package me.orfeo

import me.orfeo.mainWindow.MainWindowView
import javax.swing.UIManager


fun main() {
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch (e: Exception){

    }
    MainWindowView().show()
}

