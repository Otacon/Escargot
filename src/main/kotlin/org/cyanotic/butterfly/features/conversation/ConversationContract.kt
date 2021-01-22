package org.cyanotic.butterfly.features.conversation

interface ConversationContract {

    interface View {
        fun setWindowTitle(title: String)
        fun setHistory(messages: List<ConversationMessageModel>)
        fun playNotification()
        fun setNickname(nickname: String)
        fun setPersonalMessage(personalMessage: String)
        fun setMessageText(messageText: String)
        fun setSendButtonEnabled(sendEnabled: Boolean)
        fun setFooterText(text: String)
    }

    interface Presenter {
        fun onCreate(recipient: String)
        fun onDestroy()
        fun onMessageChanged(message: String)
        fun onSendClicked()
        fun onEnterPressed()
        fun onNudgeClicked()
    }

}