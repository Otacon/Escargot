package org.cyanotic.butterfly.core.auth

import org.cyanotic.butterfly.core.utils.httpClient
import org.cyanotic.butterfly.protocol.authentication.RequestMultipleSecurityTokensRequestFactory
import org.cyanotic.butterfly.protocol.notification.NotificationTransportManager
import org.cyanotic.butterfly.protocol.security.TicketEncoder
import org.cyanotic.butterfly.protocol.soap.RequestSecurityTokenParser
import org.cyanotic.butterfly.protocol.utils.SystemInfoRetrieverDesktop

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