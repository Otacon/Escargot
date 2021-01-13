package org.cyanotic.butterfly.database

import com.squareup.sqldelight.ColumnAdapter

class StatusAdapter : ColumnAdapter<StatusEntity, String> {
    override fun decode(databaseValue: String): StatusEntity {
        return when (databaseValue) {
            "NLN" -> StatusEntity.ONLINE
            "BSY" -> StatusEntity.BUSY
            "IDL" -> StatusEntity.IDLE
            "BRB" -> StatusEntity.BE_RIGHT_BACK
            "AWY" -> StatusEntity.AWAY
            "PHN" -> StatusEntity.ON_THE_PHONE
            "LUN" -> StatusEntity.OUT_TO_LUNCH
            "HDN" -> StatusEntity.HIDDEN
            else -> StatusEntity.OFFLINE
        }
    }

    override fun encode(value: StatusEntity): String {
        return when (value) {
            StatusEntity.ONLINE -> "NLN"
            StatusEntity.BUSY -> "BSY"
            StatusEntity.IDLE -> "IDL"
            StatusEntity.BE_RIGHT_BACK -> "BRB"
            StatusEntity.AWAY -> "AWY"
            StatusEntity.ON_THE_PHONE -> "PHN"
            StatusEntity.OUT_TO_LUNCH -> "LUN"
            StatusEntity.HIDDEN -> "HDN"
            StatusEntity.OFFLINE -> "FLN"
        }
    }


}