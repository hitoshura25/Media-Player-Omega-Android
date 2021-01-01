package com.vmenon.mpo.common.domain

sealed class ResultState<out T>
data class SuccessState<out T>(val result: T) : ResultState<T>()
object LoadingState : ResultState<Nothing>()
object ErrorState : ResultState<Nothing>()
object EmptyState : ResultState<Nothing>()