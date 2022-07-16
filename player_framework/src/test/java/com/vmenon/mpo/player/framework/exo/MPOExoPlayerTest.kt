package com.vmenon.mpo.player.framework.exo

import android.content.Context
import android.os.Handler
import android.view.SurfaceHolder
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.vmenon.mpo.player.framework.MPOPlayer
import com.vmenon.mpo.system.domain.Logger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import java.util.concurrent.Executor

class MPOExoPlayerTest {
    private val exoPlayerBuilder: SimpleExoPlayer.Builder = mock()
    private val applicationContext: Context = mock {

    }
    private val context: Context = mock {
        on { applicationContext }.thenReturn(applicationContext)
    }
    private val exoPlayer: SimpleExoPlayer = mock()
    private val mainThreadHandler: Handler = mock()
    private val executor: Executor = mock()
    private val logger: Logger = mock()
    private val mediaPlayerListener: MPOPlayer.MediaPlayerListener = mock()

    private val mpoExoPlayer = MPOExoPlayer(
        context = context,
        mainThreadHandler = mainThreadHandler,
        executor = executor,
        exoPlayerBuilder = exoPlayerBuilder,
        logger = logger,
    )

    @Test
    fun isPlayingReturnsFalseIfExoPlayerNull() {
        assertFalse(mpoExoPlayer.isPlaying)
    }

    @Test
    fun isPlayingReturnsExpoPlayerStateIfNotNull() {
        mpoExoPlayer.exoPlayer = exoPlayer

        whenever(exoPlayer.playWhenReady).thenReturn(true)
        assertTrue(mpoExoPlayer.isPlaying)

        whenever(exoPlayer.playWhenReady).thenReturn(false)
        assertFalse(mpoExoPlayer.isPlaying)
    }

    @Test
    fun playCallsExoPlayerPlayWhenReadyIfNotNull() {
        mpoExoPlayer.exoPlayer = exoPlayer
        mpoExoPlayer.play()
        verify(exoPlayer).playWhenReady = true
    }

    @Test
    fun playDoesNothingIfExoPlayerNull() {
        mpoExoPlayer.play()
        verifyNoInteractions(exoPlayer)
    }

    @Test
    fun pauseCallsSetsExoPlayerPlayWhenReadyToFalseAndSetsPositionIfNotNull() {
        mpoExoPlayer.exoPlayer = exoPlayer
        whenever(exoPlayer.playWhenReady).thenReturn(true)
        whenever(exoPlayer.currentPosition).thenReturn(100L)

        mpoExoPlayer.pause()
        verify(exoPlayer).playWhenReady = false
        assertEquals(100L, mpoExoPlayer.getCurrentPosition())
    }

    @Test
    fun pauseDoesNothingIfExoPlayerIsNotPlaying() {
        mpoExoPlayer.exoPlayer = exoPlayer
        whenever(exoPlayer.playWhenReady).thenReturn(false)
        mpoExoPlayer.pause()
        verify(exoPlayer, times(0)).playWhenReady = false
    }

    @Test
    fun pauseDoesNothingIfExoPlayerNull() {
        mpoExoPlayer.pause()
        verifyNoInteractions(exoPlayer)
    }

    @Test
    fun stopCallsExoPlayerStopAndSetsPositionIfNotNull() {
        mpoExoPlayer.exoPlayer = exoPlayer
        whenever(exoPlayer.playWhenReady).thenReturn(true)
        whenever(exoPlayer.currentPosition).thenReturn(100L)

        mpoExoPlayer.stop()
        verify(exoPlayer).stop()
        assertEquals(100L, mpoExoPlayer.getCurrentPosition())
    }

    @Test
    fun stopDoesNothingIfExoPlayerIsNotPlaying() {
        mpoExoPlayer.exoPlayer = exoPlayer
        whenever(exoPlayer.playWhenReady).thenReturn(false)
        mpoExoPlayer.stop()
        verify(exoPlayer, times(0)).stop()
    }

    @Test
    fun stopDoesNothingIfExoPlayerNull() {
        mpoExoPlayer.stop()
        verifyNoInteractions(exoPlayer)
    }

    @Test
    fun seekToSetsSeekRequestedAndCallsExoPlayerSeekToIfExoPlayerNotNull() {
        mpoExoPlayer.exoPlayer = exoPlayer
        mpoExoPlayer.seekTo(100L)
        verify(exoPlayer).seekTo(100L)
        assertTrue(mpoExoPlayer.seekRequested)
    }

    @Test
    fun seekToSetsPositionIfExoPlayerNull() {
        mpoExoPlayer.seekTo(100L)
        verifyNoInteractions(exoPlayer)
        assertEquals(100L, mpoExoPlayer.getCurrentPosition())
    }

    @Test
    fun setVolumeDoesNothingIfExoPlayerNull() {
        mpoExoPlayer.setVolume(10f)
        verifyNoInteractions(exoPlayer)
    }

    @Test
    fun setVolumeCallsExoPlayerSetVolumeIfNotNull() {
        mpoExoPlayer.exoPlayer = exoPlayer
        mpoExoPlayer.setVolume(10f)
        verify(exoPlayer).volume = 10f
    }

    @Test
    fun setDisplaySetsSurfaceHolderFieldButNotOnExoPlayerIfNull() {
        val surfaceHolder: SurfaceHolder = mock()
        mpoExoPlayer.setDisplay(surfaceHolder)
        assertEquals(surfaceHolder, mpoExoPlayer.surfaceHolder)
        verifyNoInteractions(exoPlayer)
    }

    @Test
    fun setDisplaySetsSurfaceHolderFieldAndOnExoPlayerIfNotNull() {
        val surfaceHolder: SurfaceHolder = mock()
        mpoExoPlayer.exoPlayer = exoPlayer
        mpoExoPlayer.setDisplay(surfaceHolder)
        assertEquals(surfaceHolder, mpoExoPlayer.surfaceHolder)
        verify(exoPlayer).setVideoSurfaceHolder(surfaceHolder)
    }

    @Test
    fun cleanupReleasesExoPlayerIfNotNull() {
        mpoExoPlayer.exoPlayer = exoPlayer
        mpoExoPlayer.cleanup()
        verify(exoPlayer).release()
        verify(exoPlayer).removeListener(mpoExoPlayer.eventListener)
    }

    @Test
    fun cleanupDoesNothingIfExoPlayerIsNull() {
        mpoExoPlayer.cleanup()
        verifyNoInteractions(exoPlayer)
    }

    @Test
    fun eventListenerLogsIfError() {
        val playbackError: PlaybackException = mock()
        mpoExoPlayer.eventListener.onPlayerError(playbackError)
        verify(logger).println("ExoPlayer error", playbackError)
    }

    @Test
    fun eventListenerCallsOnMediaFinishedIfEndedAndListenerNotNull() {
        mpoExoPlayer.setListener(mediaPlayerListener)
        mpoExoPlayer.eventListener.onPlaybackStateChanged(Player.STATE_ENDED)
        verify(mediaPlayerListener).onMediaFinished()
    }

    @Test
    fun eventListenerDoesNothingIfEndedAndListenerIsNull() {
        mpoExoPlayer.eventListener.onPlaybackStateChanged(Player.STATE_ENDED)
        verifyNoInteractions(mediaPlayerListener)
    }

    @Test
    fun eventListenerCallsOnMediaSeekFinishedAndClearsSeekRequestedIfReadyAndListenerNotNullAndSeekRequested() {
        mpoExoPlayer.setListener(mediaPlayerListener)
        mpoExoPlayer.seekRequested = true
        mpoExoPlayer.eventListener.onPlaybackStateChanged(Player.STATE_READY)
        verify(mediaPlayerListener).onMediaSeekFinished()
        assertFalse(mpoExoPlayer.seekRequested)
    }

    @Test
    fun eventListenerOnlyClearsSeekRequestedIfReadyAndListenerIsNullAndSeekRequested() {
        mpoExoPlayer.seekRequested = true
        mpoExoPlayer.eventListener.onPlaybackStateChanged(Player.STATE_READY)
        verifyNoInteractions(mediaPlayerListener)
        assertFalse(mpoExoPlayer.seekRequested)
    }

    @Test
    fun eventListenerDoesNotClearSeekRequestedIfReadyAndListenerIsNullAndSeekNotRequested() {
        mpoExoPlayer.seekRequested = false
        mpoExoPlayer.eventListener.onPlaybackStateChanged(Player.STATE_READY)
        verifyNoInteractions(mediaPlayerListener)
        assertFalse(mpoExoPlayer.seekRequested)
    }

    @Test
    fun eventListenerCallsOnMediaPreparedAndClearsPrepareRequestedIfReadyAndListenerNotNullAndPrepareRequested() {
        mpoExoPlayer.setListener(mediaPlayerListener)
        mpoExoPlayer.prepareRequested = true
        mpoExoPlayer.eventListener.onPlaybackStateChanged(Player.STATE_READY)
        verify(mediaPlayerListener).onMediaPrepared()
        assertFalse(mpoExoPlayer.prepareRequested)
    }

    @Test
    fun eventListenerOnlyClearsPrepareRequestedIfReadyAndListenerIsNullAndPrepareRequested() {
        mpoExoPlayer.prepareRequested = true
        mpoExoPlayer.eventListener.onPlaybackStateChanged(Player.STATE_READY)
        verifyNoInteractions(mediaPlayerListener)
        assertFalse(mpoExoPlayer.prepareRequested)
    }

    @Test
    fun eventListenerDoesNotClearPrepareRequestedIfReadyAndListenerIsNullAndPrepareNotRequested() {
        mpoExoPlayer.prepareRequested = false
        mpoExoPlayer.eventListener.onPlaybackStateChanged(Player.STATE_READY)
        verifyNoInteractions(mediaPlayerListener)
        assertFalse(mpoExoPlayer.prepareRequested)
    }
}