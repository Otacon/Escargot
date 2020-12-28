package protocol.utils

interface SystemInfoRetriever {

    fun getSystemInfo(): SystemInfo
}

class SystemInfoRetrieverDesktop : SystemInfoRetriever {

    override fun getSystemInfo(): SystemInfo {
        val architecture = when (System.getProperty("os.arch")) {
            "amd64" -> Arch.AMD64
            else -> Arch.I386
        }
        val osType = when (System.getProperty("os.name")) {
            "Windows" -> OSType.WINNT
            "MacOs" -> OSType.MACOSX
            else -> OSType.LINUX
        }
        val osVersion = System.getProperty("os.version")
        return SystemInfo(architecture, LocaleId.UnitedKingdom, osType, osVersion)
    }

}

data class SystemInfo(
    val arch: Arch,
    val locale: LocaleId,
    val osType: OSType,
    val osVersion: String
)