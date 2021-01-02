package repositories.profile

import database.MSNDB
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import protocol.authentication.RequestMultipleSecurityTokensRequestFactory
import protocol.notification.NotificationTransportManager
import protocol.security.TicketEncoder
import protocol.soap.RequestSecurityTokenParser
import protocol.utils.SystemInfoRetrieverDesktop

class ProfileRepositoryFactory {

    fun createProfileRepository(): ProfileRepository {
        return ProfileRepository(
            createRemoteDataSource(),
            createLocalDataSource()
        )
    }

    private fun createLocalDataSource(): ProfileDataSourceLocal {
        return ProfileDataSourceLocal(MSNDB.db)
    }

    private fun createRemoteDataSource(): ProfileDataSourceRemote {
        return ProfileDataSourceRemote(
            SystemInfoRetrieverDesktop(),
            NotificationTransportManager.transport,
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().also { it.level = HttpLoggingInterceptor.Level.BODY }).build(),
            TicketEncoder(),
            RequestMultipleSecurityTokensRequestFactory(),
            RequestSecurityTokenParser()
        )
    }
}