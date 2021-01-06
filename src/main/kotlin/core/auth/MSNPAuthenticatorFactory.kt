package core.auth

import core.MSNPAuthenticator
import core.utils.httpClient
import protocol.authentication.RequestMultipleSecurityTokensRequestFactory
import protocol.notification.NotificationTransportManager
import protocol.security.TicketEncoder
import protocol.soap.RequestSecurityTokenParser
import protocol.utils.SystemInfoRetrieverDesktop

class MSNPAuthenticatorFactory {

    fun createAuthenticator(): MSNPAuthenticator {
        return MSNPAuthenticator(
            SystemInfoRetrieverDesktop(),
            NotificationTransportManager.transport,
            httpClient,
            TicketEncoder(),
            RequestMultipleSecurityTokensRequestFactory(),
            RequestSecurityTokenParser()
        )
    }
}