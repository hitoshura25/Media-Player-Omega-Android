package com.vmenon.mpo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.vmenon.mpo.navigation.domain.NavigationController
import com.vmenon.mpo.navigation.domain.NavigationLocation
import javax.inject.Inject

class HomeViewModel : ViewModel() {
    @Inject
    lateinit var navigationController: NavigationController

    val currentLocation = liveData<NavigationLocation<*>> {
        emitSource(navigationController.currentLocation.asLiveData())
    }
}