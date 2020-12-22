package features.contactList

import core.TokenHolder
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import usecases.ChangeStatus
import usecases.GetContacts
import usecases.GetContactsResult
import usecases.Status
import kotlin.coroutines.CoroutineContext

class ContactListPresenter(
    private val view: ContactListContract.View,
    private val changeStatus: ChangeStatus,
    private val getContacts: GetContacts
) : ContactListContract.Presenter, CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    var model = ContactListModel(profilePicture = "", nickname = "", status = "", contacts = emptyList())

    override fun start() {
        model = model.copy(
            profilePicture = "https://scontent.flhr2-2.fna.fbcdn.net/v/t1.0-9/72196114_121124492622480_4683215129624444928_o.png?_nc_cat=111&ccb=2&_nc_sid=09cbfe&_nc_ohc=SefS3xA8pegAX_y_Cfv&_nc_ht=scontent.flhr2-2.fna&oh=984d0cb929d525cdabd183b6ec35733d&oe=5FEBFE36",
            nickname = "Cyanotic",
            status = "WLM is still alive!!!"
        )
        launch(Dispatchers.IO) {
            delay(3000)
            changeStatus(Status.ONLINE)
            model = when (val contactResponse = getContacts(TokenHolder.token)) {
                is GetContactsResult.Success -> {
                    val contacts = contactResponse.contacts.map { ContactModel(it.nickname, it.email) }
                    model.copy(contacts = contacts)
                }
                GetContactsResult.Failure -> {
                    val contacts = emptyList<ContactModel>()
                    model.copy(contacts = contacts)
                }
            }
            updateUI()
        }
    }

    override fun onContactClick(contactId: String) {
        view.openConversation(contactId)
    }

    private fun updateUI() = launch(Dispatchers.JavaFx) {
        view.setProfilePicture(model.profilePicture)
        view.setNickname(model.nickname)
        view.setStatus(model.status)
        view.setContacts(model.contacts)
    }


}