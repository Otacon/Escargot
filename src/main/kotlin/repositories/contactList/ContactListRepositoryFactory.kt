package repositories.contactList

import database.MSNDB
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import protocol.notification.NotificationTransportManager
import repositories.profile.ProfileDataSourceLocal

class ContactListRepositoryFactory {

    fun createContactListRepository(): ContactListRepository {

        return ContactListRepository(
            createRemoteDataSource(),
            createLocalDataSource(),
            createProfileLocalDataSource()
        )
    }

    private fun createProfileLocalDataSource(): ProfileDataSourceLocal {
        return ProfileDataSourceLocal(MSNDB.db)
    }

    private fun createLocalDataSource(): ContactListDataSourceLocal {
        return ContactListDataSourceLocal(MSNDB.db)
    }

    private fun createRemoteDataSource(): ContactListDataSourceRemote {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().also { it.level = HttpLoggingInterceptor.Level.BODY })
            .build()
        return ContactListDataSourceRemote(okHttpClient, NotificationTransportManager.transport)
    }


}