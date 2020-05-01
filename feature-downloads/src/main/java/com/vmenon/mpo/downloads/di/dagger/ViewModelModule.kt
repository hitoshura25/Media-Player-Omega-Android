package com.vmenon.mpo.downloads.di.dagger

import androidx.lifecycle.ViewModel
import com.vmenon.mpo.downloads.viewmodel.DownloadsViewModel
import com.vmenon.mpo.viewmodel.di.dagger.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(DownloadsViewModel::class)
    abstract fun bindDownloadsViewModel(viewModel: DownloadsViewModel): ViewModel
}