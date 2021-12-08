package org.cyanotic.butterfly.features.custom_views

import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode


internal class EditableLabel : Label() {
    private val textField: TextField = TextField()

    private var backup = ""


    init {
        this.setOnMouseClicked { e ->
            if (e.clickCount == 1) {
                textField.text = this.text.also { backup = it }
                this.graphic = textField
                this.text = ""
                textField.requestFocus()
            }
        }
        textField.focusedProperty().addListener { _, _, focussed ->
            if (!focussed) {
                toLabel()
            }
        }
        textField.setOnKeyReleased { e ->
            if (e.code == KeyCode.ENTER) {
                toLabel()
            } else if (e.code == KeyCode.ESCAPE) {
                textField.text = backup
                toLabel()
            }
        }
    }

    private fun toLabel() {
        this.graphic = null
        this.text = textField.text
    }
}