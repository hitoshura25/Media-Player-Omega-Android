package com.vmenon.mpo.login_feature.viewmodel

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.vmenon.mpo.common.domain.ContentEvent
import com.vmenon.mpo.common.domain.toContentEvent
import com.vmenon.mpo.auth.domain.AuthService
import com.vmenon.mpo.auth.domain.biometrics.BiometricState
import com.vmenon.mpo.auth.domain.biometrics.BiometricsManager
import com.vmenon.mpo.auth.domain.biometrics.PromptReason
import com.vmenon.mpo.auth.framework.CryptographyManager
import com.vmenon.mpo.login.domain.LoginService
import com.vmenon.mpo.login_feature.RegistrationFormValidator
import com.vmenon.mpo.login_feature.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel : ViewModel() {
    @Inject
    lateinit var authService: AuthService

    @Inject
    lateinit var loginService: LoginService

    @Inject
    lateinit var biometricsManager: BiometricsManager

    private val cryptographyManager = CryptographyManager()

    private var biometricEnrollmentContract: ActivityResultLauncher<Intent>? = null

    val registration by lazy {
        val registrationObservable = RegistrationObservable()
        registrationObservable.addOnPropertyChangedCallback(validator)
        registrationObservable
    }

    private val validator = RegistrationFormValidator()
    private val loginStateFromUI = MutableLiveData<ContentEvent<AccountState>>()
    private val biometricStateChanged = MutableLiveData<Unit>()

    private val loginState by lazy {
        MediatorLiveData<ContentEvent<AccountState>>().apply {
            addSource(authService.authenticated().asLiveData()) { authenticated ->
                postState(authenticated, this)
            }
            addSource(loginStateFromUI) { value ->
                postValue(value)
            }
            addSource(biometricStateChanged) {
                postState(authService.isAuthenticated(), this)
            }
        }
    }

    fun onCreate(fragment: Fragment) {
        biometricEnrollmentContract = fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                biometricStateChanged.postValue(Unit)
            }
        }
    }

    fun registrationValid(): LiveData<RegistrationValid> = validator.registrationValid()

    fun loginState(): LiveData<ContentEvent<AccountState>> = loginState

    fun registerClicked() {
        loginStateFromUI.postValue(RegisterState.toContentEvent())
    }

    fun loginClicked(fragment: Fragment) {
        viewModelScope.launch(Dispatchers.IO) {
            loginStateFromUI.postValue(LoadingState.toContentEvent())
            authService.startAuthentication(fragment.requireActivity())
        }
    }

    fun logoutClicked(fragment: Fragment) {
        viewModelScope.launch(Dispatchers.IO) {
            loginStateFromUI.postValue(LoadingState.toContentEvent())
            authService.logout(fragment.requireActivity())
        }
    }

    fun performRegistration(fragment: Fragment) {
        viewModelScope.launch(Dispatchers.IO) {
            loginStateFromUI.postValue(LoadingState.toContentEvent())
            loginService.registerUser(
                registration.getFirstName(),
                registration.getLastName(),
                registration.getEmail(),
                registration.getPassword()
            )
            authService.startAuthentication(fragment.requireActivity())
        }
    }

    fun userDoesNotWantBiometrics() {
        viewModelScope.launch(Dispatchers.IO) {
            loginService.setEnrolledInBiometrics(false)
            loginService.userDeclinedBiometricsEnrollment()
        }
    }

    fun userWantsToEnrollInBiometrics(fragment: Fragment) {
        viewModelScope.launch {
            biometricsManager.requestBiometricPrompt(PromptReason.ENROLLMENT)
        }
        /*val activity = fragment.requireActivity() as AppCompatActivity
        when (biometricsManager.biometricState()) {
            BiometricState.SUCCESS -> {
                val secretKeyName = "biometric_sample_encryption_key"
                val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
                val biometricPrompt =
                    BiometricPromptUtils.createBiometricPrompt(
                        activity,
                        ::encryptAndStoreServerToken
                    )
                val promptInfo = BiometricPromptUtils.createPromptInfo(activity)
                biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
            }
            BiometricState.REQUIRES_ENROLLMENT -> promptToEnrollInBiometrics()
            BiometricState.NOT_SUPPORTED -> {

            }
        }*/
    }

    fun loginWithBiometrics(fragment: Fragment) {
        /*val activity = fragment.requireActivity() as AppCompatActivity
        val secretKeyName = "biometric_sample_encryption_key"
        val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
        val biometricPrompt =
            BiometricPromptUtils.createBiometricPrompt(
                activity,
                ::decryptServerTokenFromStorage
            )
        val promptInfo = BiometricPromptUtils.createPromptInfo(activity)
        biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
         */
    }

    @Suppress("DEPRECATION")
    private fun promptToEnrollInBiometrics() {
        val enrollIntent = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_WEAK
                    )
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                Intent(Settings.ACTION_FINGERPRINT_ENROLL)
            }
            else -> {
                Intent(Settings.ACTION_SECURITY_SETTINGS)
            }
        }
        biometricEnrollmentContract?.launch(enrollIntent)
    }

    private fun encryptAndStoreServerToken(authResult: BiometricPrompt.AuthenticationResult) {
        /*authResult.cryptoObject?.cipher?.apply {
            SampleAppUser.fakeToken?.let { token ->
                Log.d(TAG, "The token from server is $token")
                val encryptedServerTokenWrapper = cryptographyManager.encryptData(token, this)
                cryptographyManager.persistCiphertextWrapperToSharedPrefs(
                    encryptedServerTokenWrapper,
                    applicationContext,
                    SHARED_PREFS_FILENAME,
                    Context.MODE_PRIVATE,
                    CIPHERTEXT_WRAPPER
                )
            }
        }*/
    }

    private fun decryptServerTokenFromStorage(authResult: BiometricPrompt.AuthenticationResult) {
        /*ciphertextWrapper?.let { textWrapper ->
            authResult.cryptoObject?.cipher?.let {
                val plaintext =
                    cryptographyManager.decryptData(textWrapper.ciphertext, it)
                SampleAppUser.fakeToken = plaintext
                // Now that you have the token, you can query server for everything else
                // the only reason we call this fakeToken is because we didn't really get it from
                // the server. In your case, you will have gotten it from the server the first time
                // and therefore, it's a real token.

            }
        }*/
    }

    private fun postState(
        authenticated: Boolean,
        mutableLiveData: MutableLiveData<ContentEvent<AccountState>>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            mutableLiveData.postValue(LoadingState.toContentEvent())
            if (authenticated) {
                loginService.getUser().onSuccess { user ->
                    mutableLiveData.postValue(
                        LoggedInState(
                            user,
                            shouldPromptToEnrollInBiometrics()
                        ).toContentEvent()
                    )
                }
            } else {
                mutableLiveData.postValue(
                    LoginState(canUseBiometrics()).toContentEvent()
                )
            }
        }
    }

    private suspend fun canUseBiometrics() = if (!loginService.isEnrolledInBiometrics()) {
        when (biometricsManager.biometricState()) {
            BiometricState.SUCCESS -> true
            else -> false
        }
    } else false

    private suspend fun shouldPromptToEnrollInBiometrics() =
        if (!loginService.didUserDeclineBiometricsEnrollment() &&
            !loginService.isEnrolledInBiometrics()
        ) {
            when (biometricsManager.biometricState()) {
                BiometricState.SUCCESS,
                BiometricState.REQUIRES_ENROLLMENT -> true
                else -> false
            }
        } else false
}