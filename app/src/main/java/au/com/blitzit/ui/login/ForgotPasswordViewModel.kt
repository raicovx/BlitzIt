package au.com.blitzit.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.blitzit.auth.AuthRegistration
import com.amplifyframework.auth.result.step.AuthNextResetPasswordStep
import com.amplifyframework.auth.result.step.AuthResetPasswordStep

enum class ResetPasswordStatus
{
    Awaiting,
    Attempting,
    EnterConfirmation,
    AttemptingConfirmation,
    Success,
    Rejected,
    ConfirmationFailed
}

class ForgotPasswordViewModel: ViewModel()
{
    val resetPasswordStatus = MutableLiveData<ResetPasswordStatus>()

    init {
        resetPasswordStatus.postValue(ResetPasswordStatus.Awaiting)
    }

    suspend fun attemptResetPassword(email: String)
    {
        resetPasswordStatus.postValue(ResetPasswordStatus.Attempting)
        resetPassword(email)
    }

    private suspend fun resetPassword(email: String)
    {
        val result = AuthRegistration.attemptResetPassword(email)
        if(result != null && result.nextStep.resetPasswordStep == AuthResetPasswordStep.CONFIRM_RESET_PASSWORD_WITH_CODE)
        {
            resetPasswordStatus.postValue(ResetPasswordStatus.EnterConfirmation)
        }
        else
        {
            resetPasswordStatus.postValue(ResetPasswordStatus.Rejected)
        }
    }

    suspend fun attemptConfirmResetPassword(password: String, confirmationCode: String)
    {
        resetPasswordStatus.postValue(ResetPasswordStatus.AttemptingConfirmation)
        confirmResetPassword(password, confirmationCode)
    }

    private suspend fun confirmResetPassword(password: String, confirmationCode: String)
    {
        if(AuthRegistration.confirmPasswordReset(password, confirmationCode))
            resetPasswordStatus.postValue(ResetPasswordStatus.Success)
        else
            resetPasswordStatus.postValue(ResetPasswordStatus.ConfirmationFailed)
    }
}