package protocol.soap

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.junit.Test


internal class RequestSecurityTokenParserTest{

    @Test
    fun parseResult(){
        val result = RequestSecurityTokenParser().parse(SAMPLE_RESPONSE)
        assertEquals("secret", result!!.secret)
        assertEquals("nonce", result.nonce)

    }
}


val SAMPLE_RESPONSE = """
<?xml version="1.0" encoding="utf-8"?>
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
	<S:Header>
		<psf:pp xmlns:psf="http://schemas.microsoft.com/Passport/SoapServices/SOAPFault">
			<psf:serverVersion>1</psf:serverVersion>
			<psf:PUID>05995AFE7D5ADDB9</psf:PUID>
			<psf:configVersion>16.000.26889.00</psf:configVersion>
			<psf:uiVersion>3.100.2179.0</psf:uiVersion>
			<psf:mobileConfigVersion>16.000.26208.0</psf:mobileConfigVersion>
			<psf:authstate>0x48803</psf:authstate>
			<psf:reqstatus>0x0</psf:reqstatus>
			<psf:serverInfo Path="Live1" RollingUpgradeState="ExclusiveNew" LocVersion="0" ServerTime="2020-11-29T13:31:23Z">XYZPPLOGN1A23 2017.10.03.19.00.04</psf:serverInfo>
			<psf:cookies/>
			<psf:browserCookies>
				<psf:browserCookie Name="MH" URL="http://www.msn.com">MSFT; path=/; domain=.msn.com; expires=Wed, 30-Dec-2037 16:00:00 GMT</psf:browserCookie>
				<psf:browserCookie Name="MHW" URL="http://www.msn.com">; path=/; domain=.msn.com; expires=Thu, 30-Oct-1980 16:00:00 GMT</psf:browserCookie>
				<psf:browserCookie Name="MH" URL="http://www.live.com">MSFT; path=/; domain=.live.com; expires=Wed, 30-Dec-2037 16:00:00 GMT</psf:browserCookie>
				<psf:browserCookie Name="MHW" URL="http://www.live.com">; path=/; domain=.live.com; expires=Thu, 30-Oct-1980 16:00:00 GMT</psf:browserCookie>
			</psf:browserCookies>
			<psf:credProperties>
				<psf:credProperty Name="MainBrandID">MSFT</psf:credProperty>
				<psf:credProperty Name="BrandIDList"></psf:credProperty>
				<psf:credProperty Name="IsWinLiveUser">true</psf:credProperty>
				<psf:credProperty Name="CID">99ae0c117d5addb9</psf:credProperty>
				<psf:credProperty Name="AuthMembername">orfeo18@hotmail.it</psf:credProperty>
				<psf:credProperty Name="Country">US</psf:credProperty>
				<psf:credProperty Name="Language">1033</psf:credProperty>
				<psf:credProperty Name="FirstName">John</psf:credProperty>
				<psf:credProperty Name="LastName">Doe</psf:credProperty>
				<psf:credProperty Name="ChildFlags">00000001</psf:credProperty>
				<psf:credProperty Name="Flags">40100643</psf:credProperty>
				<psf:credProperty Name="FlagsV2">00000000</psf:credProperty>
				<psf:credProperty Name="IP">127.0.0.1</psf:credProperty>
				<psf:credProperty Name="FamilyID">0000000000000000</psf:credProperty>
				<psf:credProperty Name="AssociatedForStrongAuth">0</psf:credProperty>
			</psf:credProperties>
			<psf:extProperties>
				<psf:extProperty Name="ANON" Expiry="Wed, 30-Dec-2037 16:00:00 GMT" Domains="bing.com;atdmt.com" IgnoreRememberMe="false">A=2AD1B6380CC38C61A2E95994FFFFFFFF&amp;E=1456&amp;W=1</psf:extProperty>
				<psf:extProperty Name="NAP" Expiry="Wed, 30-Dec-2037 16:00:00 GMT" Domains="bing.com;atdmt.com" IgnoreRememberMe="false">V=1.9&amp;E=13fc&amp;C=tq1sGI5NyECr4nbob0bsqOGQx85gOAzYs8FuhJP5L22WfJl-67MNNQ&amp;W=1</psf:extProperty>
				<psf:extProperty Name="LastUsedCredType">1</psf:extProperty>
				<psf:extProperty Name="WebCredType">1</psf:extProperty>
				<psf:extProperty Name="CID">99ae0c117d5addb9</psf:extProperty>
			</psf:extProperties>
			<psf:response/>
		</psf:pp>
	</S:Header>
	<S:Body>
		<wst:RequestSecurityTokenResponseCollection xmlns:S="http://schemas.xmlsoap.org/soap/envelope/" xmlns:wst="http://schemas.xmlsoap.org/ws/2004/04/trust" xmlns:wsse="http://schemas.xmlsoap.org/ws/2003/06/secext" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" xmlns:saml="urn:oasis:names:tc:SAML:1.0:assertion" xmlns:wsp="http://schemas.xmlsoap.org/ws/2002/12/policy" xmlns:psf="http://schemas.microsoft.com/Passport/SoapServices/SOAPFault">
			<wst:RequestSecurityTokenResponse>
				<wst:TokenType>urn:passport:legacy</wst:TokenType>
				<wsp:AppliesTo xmlns:wsa="http://schemas.xmlsoap.org/ws/2004/03/addressing">
					<wsa:EndpointReference>
						<wsa:Address>http://Passport.NET/tb</wsa:Address>
					</wsa:EndpointReference>
				</wsp:AppliesTo>
				<wst:LifeTime>
					<wsu:Created>2020-11-29T13:31:23Z</wsu:Created>
					<wsu:Expires>2020-11-30T13:31:23Z</wsu:Expires>
				</wst:LifeTime>
				<wst:RequestedSecurityToken>
					<EncryptedData xmlns="http://www.w3.org/2001/04/xmlenc#" Id="BinaryDAToken0" Type="http://www.w3.org/2001/04/xmlenc#Element">
						<EncryptionMethod Algorithm="http://www.w3.org/2001/04/xmlenc#tripledes-cbc"></EncryptionMethod>
						<ds:KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
							<ds:KeyName>http://Passport.NET/STS</ds:KeyName>
						</ds:KeyInfo>
						<CipherData>
							<CipherValue>Cap26AQZrSyMm2SwwTyJKyqLR9/S+vQWQsaBc5Mv7PwtQDMzup/udOOMMvSu99R284pmiD3IepBXrEMLK5rLrXAf2A6vrP6vYuGA45GCqQdoxusHZcjt9P2B8WyCTVT2cM8jtGqGIfRlU/4WzOLxNrDJwDfOsmilduGAGZfvRPW7/jyXXrnGK7/PWkymX4YDD+ygJfMrPAfvAprvw/HVE6tutKVc9cViTVYy8oHjosQlb8MKn3vKDW1O2ZWQUc47JPl7DkjQaanfNBGe6CL7K1nr6Z/jy7Ay7MjV+KQehmvphSEmCzLrpB4WWn2PdpdTrOcDj+aJfWHeGL4sIPwEKgrKnTQg9QD8CCsm5wew9P/br39OuIfsC6/PFBEHmVThqj0aMxYLRD4K2GoRay6Ab7NftoIP5dnFnclfRxETAoNpTPE2F5Q669QySrdXxBpBSk8GLmdCDMlhiyzSiByrhFQaZRcH8n9i+i289otYuJQ7xPyP19KwT4CRyOiIlh3DSdlBfurMwihQGxN2spU7P4MwckrDKeOyYQhvNm/XWId/oXBqpHbo2yRPiOwL9p1J4AxA4RaJuh77vyhn2lFQaxPDqZd5A8RJjpb2NE2N3UncKLW7GAangdoLbRDMqt51VMZ0la+b/moL61fKvFXinKRHc7PybrG3MWzgXxO/VMKAuXOsB9XnOgl2A524cgiwyg==</CipherValue>
						</CipherData>
					</EncryptedData>
				</wst:RequestedSecurityToken>
				<wst:RequestedTokenReference>
					<wsse:KeyIdentifier ValueType="urn:passport"></wsse:KeyIdentifier>
					<wsse:Reference URI="#BinaryDAToken0"></wsse:Reference>
				</wst:RequestedTokenReference>
				<wst:RequestedProofToken>
					<wst:BinarySecret>tgoPVK67sU36fQKlGLMgWgTXp7oiaQgE</wst:BinarySecret>
				</wst:RequestedProofToken>
			</wst:RequestSecurityTokenResponse>
			<wst:RequestSecurityTokenResponse>
				<wst:TokenType>urn:passport:compact</wst:TokenType>
				<wsp:AppliesTo xmlns:wsa="http://schemas.xmlsoap.org/ws/2004/03/addressing">
					<wsa:EndpointReference>
						<wsa:Address>messengerclear.live.com</wsa:Address>
				</wsa:EndpointReference>
				</wsp:AppliesTo>
				<wst:LifeTime>
					<wsu:Created>2020-11-29T13:31:23Z</wsu:Created>
					<wsu:Expires>2020-11-30T13:31:23Z</wsu:Expires>
				</wst:LifeTime>
				<wst:RequestedSecurityToken>
					<wsse:BinarySecurityToken Id="Compact1">t=nonce&amp;p=</wsse:BinarySecurityToken>
				</wst:RequestedSecurityToken>
				<wst:RequestedTokenReference>
					<wsse:KeyIdentifier ValueType="urn:passport:compact"></wsse:KeyIdentifier>
					<wsse:Reference URI="#Compact1"></wsse:Reference>
				</wst:RequestedTokenReference><wst:RequestedProofToken>
					<wst:BinarySecret>secret</wst:BinarySecret>
				</wst:RequestedProofToken></wst:RequestSecurityTokenResponse>
		</wst:RequestSecurityTokenResponseCollection>
	</S:Body>
</S:Envelope>
""".trimIndent()