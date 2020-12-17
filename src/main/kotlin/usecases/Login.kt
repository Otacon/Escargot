package usecases

import kotlinx.coroutines.delay
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@ExperimentalTime
class Login {

    suspend operator fun invoke(username: String, password: String): LoginResult {
        delay(3.toDuration(DurationUnit.SECONDS))
        return if(username.equals("error",true)){
            LoginResult.Success
        } else {
            LoginResult.Failure
        }
    }
}

sealed class LoginResult {
    object Success : LoginResult()
    object Failure : LoginResult()
}