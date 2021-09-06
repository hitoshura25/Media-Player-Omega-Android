package com.vmenon.mpo.auth.domain.biometrics

interface BiometricsManager {
    fun biometricState(): BiometricState
}