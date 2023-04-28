package com.vmenon.mpo.system.domain

interface PatternMatcher {
    fun isEmail(input: String): Boolean
}