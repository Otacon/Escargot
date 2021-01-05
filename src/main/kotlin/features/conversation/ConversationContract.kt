package features.conversation

interface ConversationContract {

    interface View {
        fun setWindowTitle(title: String)
        fun setHistory(messages: List<ConversationMessageModel>)
        fun clearMessageInput()
        fun playNotification()
    }

    interface Presenter {
        fun start()
        fun onSendMessage(message: String)
        fun onDestroy()
    }

}