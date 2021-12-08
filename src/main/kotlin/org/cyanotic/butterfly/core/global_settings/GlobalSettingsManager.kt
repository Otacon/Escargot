package org.cyanotic.butterfly.core.global_settings

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import mu.KotlinLogging
import org.cyanotic.butterfly.core.file_manager.fileManager
import java.io.FileNotFoundException
import java.util.*

private val logger = KotlinLogging.logger("GlobalSettings")

object GlobalSettingsManager {

    private var globalSettings: GlobalSettings

    init {
        globalSettings = read() ?: run {
            val newGlobalSettings = GlobalSettings(
                machineGuid = UUID.randomUUID().toString(),
                accounts = emptyList()
            )
            write(newGlobalSettings)
        }
    }

    var machineGuid: String
        get() = globalSettings.machineGuid
        set(value) {
            globalSettings.machineGuid = value
            write(globalSettings)
        }

    val accounts: List<StoredAccount>
        get() = globalSettings.accounts

    fun addOrUpdateAccount(account: StoredAccount) {
        val newAccounts = globalSettings.accounts.mapNotNull {
            if (it.passport == account.passport) null else it
        } + account
        globalSettings = globalSettings.copy(accounts = newAccounts)
        write(globalSettings)
    }

    fun removeAccount(account: StoredAccount) {
        removeAccount(account.passport)
    }

    fun removeAccount(passport: String) {
        val newAccounts = globalSettings.accounts.mapNotNull {
            if (it.passport != passport) it else null
        }
        globalSettings = globalSettings.copy(accounts = newAccounts)
        write(globalSettings)
    }

    private fun read(): GlobalSettings? {
        val gson = Gson()
        logger.info { "Reading global settings..." }
        return try {
            val fileReader = fileManager.settingsFile.reader()
            val existingGlobalSettings = gson.fromJson(fileReader, GlobalSettings::class.java)
            logger.debug { existingGlobalSettings }
            fileReader.close()
            existingGlobalSettings
        } catch (e: Exception) {
            when (e) {
                is FileNotFoundException -> logger.info { "File ${fileManager.settingsFile.name} not found" }
                else -> e.printStackTrace()
            }
            null
        }
    }

    private fun write(settings: GlobalSettings): GlobalSettings {
        val gson = Gson()
        try {
            logger.info { "Writing new settings" }
            logger.debug { settings }
            val fileWriter = fileManager.settingsFile.writer()
            gson.toJson(settings, fileWriter)
            fileWriter.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return settings
    }
}

private data class GlobalSettings(

    @SerializedName("machine_guid")
    var machineGuid: String,

    @SerializedName("accounts")
    var accounts: List<StoredAccount>

)

data class StoredAccount(

    @SerializedName("passport")
    var passport: String,

    @SerializedName("password")
    var password: String,

    @SerializedName("save_username")
    var saveUsername: Boolean,

    @SerializedName("save_password")
    var savePassword: Boolean,

    @SerializedName("login_automatically")
    var loginAutomatically: Boolean,

    @SerializedName("last_login")
    var lastLogin: Long
)