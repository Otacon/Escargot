package features.contactList

class ContactListPresenter(
    private val view: ContactListContract.View
) : ContactListContract.Presenter {

    var model = ContactListModel(profilePicture = "", nickname = "", status = "")

    override fun start() {
        model = model.copy(
            profilePicture = "https://scontent.flhr2-2.fna.fbcdn.net/v/t1.0-9/72196114_121124492622480_4683215129624444928_o.png?_nc_cat=111&ccb=2&_nc_sid=09cbfe&_nc_ohc=SefS3xA8pegAX_y_Cfv&_nc_ht=scontent.flhr2-2.fna&oh=984d0cb929d525cdabd183b6ec35733d&oe=5FEBFE36",
            nickname = "Cyanotic",
            status = "WLM is still alive!!!"
        )
        updateUI()
    }

    private fun updateUI() {
        view.setProfilePicture(model.profilePicture)
        view.setNickname(model.nickname)
        view.setStatus(model.status)
    }


}