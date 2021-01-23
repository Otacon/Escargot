package org.cyanotic.butterfly.features.conversation

interface ConversationContract {

    interface View {
        fun setWindowTitle(title: String)
        fun setHistory(messages: List<ConversationMessageModel>)
        fun playNotification()
        fun setNickname(nickname: String)
        fun setPersonalMessage(personalMessage: String)
        fun setMessageText(messageText: String)
        fun setFooterText(text: String)
        fun shake()
    }

    interface Presenter {
        fun onCreate(recipient: String)
        fun onDestroy()
        fun onMessageChanged(message: String)
        fun onEnterPressed()
        fun onNudgeClicked()
    }

}