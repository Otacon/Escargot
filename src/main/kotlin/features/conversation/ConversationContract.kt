package features.conversation

interface ConversationContract {

    interface View {
        fun setWindowTitle(title: String)
        fun setHistory(messages: List<ConversationMessageModel>)
        fun clearMessageInput()
    }

    interface Presenter {
        fun start(recipient: String)
        fun onSendMessage(message: String)
    }

}