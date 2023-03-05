package com.vmenon.mpo.system.framework

import androidx.core.util.PatternsCompat
import com.vmenon.mpo.system.domain.PatternMatcher

class AndroidPatternMatcher : PatternMatcher {
    override fun isEmail(input: String): Boolean =
        PatternsCompat.EMAIL_ADDRESS.matcher(input).matches()
}