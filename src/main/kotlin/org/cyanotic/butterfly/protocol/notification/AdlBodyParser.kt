package org.cyanotic.butterfly.protocol.notification

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister

class AdlBodyParser {

    fun parse(body: String): AdlBodyData {
        val serializer: Serializer = Persister()
        return serializer.read(AdlBodyData::class.java, body)
    }
}

//RESPONSE
@Root(strict = false)
data class AdlBodyData(

    @field:Element(name = "d")
    @param:Element(name = "d")
    val data: AdlBodyDataD

)

data class AdlBodyDataD(

    @field:Element(name = "c")
    @param:Element(name = "c")
    val contact: AdlBodyDataC,

    @field:Attribute(name = "n")
    @param:Attribute(name = "n")
    val emailDomain: String

)

data class AdlBodyDataC(

    @field:Attribute(name = "n")
    @param:Attribute(name = "n")
    val name: String,

    @field:Attribute(name = "t")
    @param:Attribute(name = "t")
    val type: Int,

    @field:Attribute(name = "l")
    @param:Attribute(name = "l")
    val list: Int,

    @field:Attribute(name = "f")
    @param:Attribute(name = "f")
    val displayName: String

)