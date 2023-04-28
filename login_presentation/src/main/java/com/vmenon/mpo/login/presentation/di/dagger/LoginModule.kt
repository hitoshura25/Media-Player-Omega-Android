package com.vmenon.mpo.login.presentation.di.dagger

import com.vmenon.mpo.login.presentation.RegistrationFormValidator
import com.vmenon.mpo.system.domain.PatternMatcher
import dagger.Module
import dagger.Provides

@Module
object LoginModule {
    @Provides
    fun registrationFormValidator(patternMatcher: PatternMatcher): RegistrationFormValidator =
        RegistrationFormValidator(patternMatcher)
}