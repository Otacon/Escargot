package features.contactList

interface ContactListContract {

    interface View {

        fun setProfilePicture(picture: String)
        fun setNickname(text: String)
        fun setStatus(text: String)

    }

    interface Presenter {

        fun start()

    }

}