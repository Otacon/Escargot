package org.cyanotic.butterfly.features.friend_request

interface FriendRequestContract {

    interface View {

        fun closeWithReject()
        fun closeWithAccept()
        fun setMessage(message: String)

    }

    interface Presenter {

        fun onCreate(passport: String)
        fun onIgnoreClicked()
        fun onAcceptClicked()

    }

}

