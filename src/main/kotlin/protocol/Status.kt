package protocol

enum class Status {
    ONLINE,
    AWAY,
    BE_RIGHT_BACK,
    IDLE,
    OUT_TO_LUNCH,
    ON_THE_PHONE,
    BUSY,
    OFFLINE,
    HIDDEN
}

fun String.asStatus(): Status{
    return when(this){
        "NLN" -> Status.ONLINE
        "AWY" -> Status.AWAY
        "BRB" -> Status.BE_RIGHT_BACK
        "IDL" -> Status.IDLE
        "LUN" -> Status.OUT_TO_LUNCH
        "PHN" -> Status.ON_THE_PHONE
        "BSY" -> Status.BUSY
        "FLN" -> Status.OFFLINE
        "HDN" -> Status.HIDDEN
        else -> Status.OFFLINE
    }
}

fun Status.asString() : String{
    return when(this){
        Status.ONLINE -> "NLN"
        Status.AWAY -> "AWY"
        Status.BE_RIGHT_BACK -> "BRB"
        Status.IDLE -> "IDL"
        Status.OUT_TO_LUNCH -> "LUN"
        Status.ON_THE_PHONE -> "PHN"
        Status.BUSY -> "BSY"
        Status.OFFLINE -> "FLN"
        Status.HIDDEN -> "HDN"
    }
}