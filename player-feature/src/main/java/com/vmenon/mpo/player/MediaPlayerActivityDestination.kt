package com.vmenon.mpo.player

import com.vmenon.mpo.navigation.framework.ActivityDestination
import com.vmenon.mpo.player.domain.PlayerNavigationDestination
import com.vmenon.mpo.player.view.activity.MediaPlayerActivity

class MediaPlayerActivityDestination(
    override val activityClass: Class<*> = MediaPlayerActivity::class.java
) : PlayerNavigationDestination, ActivityDestination