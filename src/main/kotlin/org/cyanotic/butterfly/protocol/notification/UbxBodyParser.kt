package org.cyanotic.butterfly.protocol.notification

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister

class UbxBodyParser {

    fun parse(body: String): UbxBodyData {
        return if (body.isBlank()) {
            UbxBodyData()
        } else {
            val serializer: Serializer = Persister()
            serializer.read(UbxBodyData::class.java, body)
        }
    }
}

//RESPONSE
@Root(strict = false)
data class UbxBodyData(
    @field:Element(name = "PSM", required = false)
    @param:Element(name = "PSM", required = false)
    val personalMessage: String? = null,

    @field:Element(name = "CurrentMedia", required = false)
    @param:Element(name = "CurrentMedia", required = false)
    val currentMedia: String? = null,

    @field:Element(name = "DDP", required = false)
    @param:Element(name = "DDP", required = false)
    val ddp: String? = null,

    @field:Element(name = "SignatureSound", required = false)
    @param:Element(name = "SignatureSound", required = false)
    val signatureSound: String? = null,

    @field:Element(name = "Scene", required = false)
    @param:Element(name = "Scene", required = false)
    val scene: String? = null,

    @field:Element(name = "ColorScheme", required = false)
    @param:Element(name = "ColorScheme", required = false)
    val colorScheme: String? = null

)