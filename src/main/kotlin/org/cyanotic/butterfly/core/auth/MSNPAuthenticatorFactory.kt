package org.cyanotic.butterfly.core.auth

import org.cyanotic.butterfly.core.utils.httpClient
import org.cyanotic.butterfly.protocol.authentication.RequestMultipleSecurityTokensRequestFactory
import org.cyanotic.butterfly.protocol.notification.NotificationTransport
import org.cyanotic.butterfly.protocol.security.TicketEncoder
import org.cyanotic.butterfly.protocol.soap.RequestSecurityTokenParser
import org.cyanotic.butterfly.protocol.utils.SystemInfoRetrieverDesktop

class MSNPAuthenticatorFactory {

    fun createAuthenticator(trasport: NotificationTransport): MSNPAuthenticator {
        return MSNPAuthenticator(
            SystemInfoRetrieverDesktop(),
            trasport,
            httpClient,
            TicketEncoder(),
            RequestMultipleSecurityTokensRequestFactory(),
            RequestSecurityTokenParser()
        )
    }
}