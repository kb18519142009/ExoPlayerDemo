package com.example.exoplayerdemo.exoplayer.controller;

import android.util.Log;
import android.view.Surface;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

/**
 * 播放器事件的默认实现
 * Created by kang on 2018/3/10.
 */
public class PlayerEventController implements SimpleExoPlayer.VideoListener,
        VideoRendererEventListener,
        Player.EventListener {

    private static final String TAG = "PlayerEventController";
    private static final boolean SHOW_LOG = true;

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        if (SHOW_LOG) {
            Log.e(TAG, "onPlaybackParametersChanged: ");
        }
    }

    @Override
    public void onVideoEnabled(DecoderCounters decoderCounters) {
        if (SHOW_LOG) {
            Log.e(TAG, "onVideoEnabled: ");
        }
    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
        if (SHOW_LOG) {
            Log.e(TAG, "onVideoDecoderInitialized: ");
        }
    }

    @Override
    public void onVideoInputFormatChanged(Format format) {
        if (SHOW_LOG) {
            Log.e(TAG, "onVideoInputFormatChanged: ");
        }
    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {
        if (SHOW_LOG) {
            Log.e(TAG, "onDroppedFrames: ");
        }
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        if (SHOW_LOG) {
            Log.e(TAG, "onVideoSizeChanged: ");
        }
    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {
        if (SHOW_LOG) {
            Log.e(TAG, "onRenderedFirstFrame: surface");
        }
    }

    @Override
    public void onRenderedFirstFrame() {
        if (SHOW_LOG) {
            Log.e(TAG, "onRenderedFirstFrame: ");
        }
    }

    @Override
    public void onVideoDisabled(DecoderCounters decoderCounters) {
        if (SHOW_LOG) {
            Log.e(TAG, "onVideoDisabled: ");
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        if (SHOW_LOG) {
            Log.e(TAG, "onTimelineChanged: ");
        }
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
        if (SHOW_LOG) {
            Log.e(TAG, "onTracksChanged: ");
        }
    }

    /**
     * Called when the player starts or stops loading the source.
     *
     * @param isLoading Whether the source is currently being loaded.
     */
    @Override
    public void onLoadingChanged(boolean isLoading) {
        if (SHOW_LOG) {
            Log.e(TAG, "onLoadingChanged: " + isLoading);
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (SHOW_LOG) {
            String state = "";
            switch (playbackState) {
                case Player.STATE_IDLE:
                    state = "STATE_IDLE";
                    break;
                case Player.STATE_BUFFERING:
                    state = "STATE_BUFFERING";
                    break;
                case Player.STATE_READY:
                    state = "STATE_READY";
                    break;
                case Player.STATE_ENDED:
                    state = "STATE_ENDED";
                    break;
            }
            Log.e(TAG, "onPlayerStateChanged: state : " + state + ", playWhenReady : " + playWhenReady);
        }
    }

    @Override
    public void onRepeatModeChanged(int i) {
        Log.e(TAG, "onRepeatModeChanged: " + i);
    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {
        Log.e(TAG, "onPlayerError: ", e);
    }

    @Override
    public void onPositionDiscontinuity() {
        if (SHOW_LOG) {
            Log.e(TAG, "onPositionDiscontinuity: reason : ");
        }
    }
}
