package org.cyanotic.butterfly.features.friend_request

interface FriendRequestContract {

    interface View {

        fun close()

    }

    interface Presenter {

        fun onCreate()
        fun onIgnoreClicked()
        fun onAcceptClicked()

    }

}

