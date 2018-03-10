package com.example.exoplayerdemo.exoplayer.controller;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

/**
 * 播放器的基础实现,不牵扯播放器视图
 * Created by kelseo on 2017/10/27.
 */
public abstract class PlayerBaseController extends PlayerEventController {

    public static final String TAG = "PlayerBaseController";

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    protected Context mContext;

    //ExoPlayer相关成员变量
    SimpleExoPlayer mPlayer;
    private DataSource.Factory mMediaDataSourceFactory;
    private ExtractorsFactory mExtractorsFactory;

    private boolean inErrorState;
    private long resumePosition = C.INDEX_UNSET;

    private Handler mHandler = new Handler();

    private Runnable mProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (mPlayer != null && mPlayer.getPlaybackState() != Player.STATE_ENDED) {
                float progress = mPlayer.getCurrentPosition() * 1.0f / mPlayer.getDuration();
                if (progress > 0 && progress <= 1) {
                    onProgressChanged(progress);
                }
                mHandler.postDelayed(mProgressRunnable, 1000);
            }
        }
    };

    public PlayerBaseController(Context context) {
        mContext = context;
    }

    public void releasePlayer() {
        //释放播放器
        if (mPlayer != null) {
            clearResumePosition();
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    public boolean isInErrorState() {
        return inErrorState;
    }

    /**
     * 切换清晰度URL
     */
    void switchSharpness(boolean autoStart, boolean repeat, String... playUrls) {
        updateResumePosition();
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
        preparePlayer(autoStart, repeat, playUrls);
    }

    /**
     * 初始化播放器
     */
    void preparePlayer(boolean autoStart, boolean repeat, String... playUrls) {
        Log.e(TAG, "preparePlayer: autoStart : " + autoStart + ", resumePosition : " + resumePosition);
        if (mMediaDataSourceFactory == null) {
            mMediaDataSourceFactory = buildDataSourceFactory(mContext, false);
        }

        if (mExtractorsFactory == null) {
            mExtractorsFactory = new DefaultExtractorsFactory();
        }

        if (mPlayer == null) {
            mPlayer = createNewPlayer(mContext);
            mPlayer.addListener(this);
            mPlayer.addVideoListener(this);
            mPlayer.setVideoDebugListener(this);
        }

        mPlayer.prepare(createMediaSource(playUrls));
        mPlayer.setPlayWhenReady(autoStart);

        if (repeat) {
            mPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
        }

        inErrorState = false;
        if (resumePosition != C.POSITION_UNSET) {
            mPlayer.seekTo(resumePosition);
        } else {
            long savedPosition = getSavedPosition();
            if (savedPosition != C.POSITION_UNSET) {
                mPlayer.seekTo(savedPosition);
            }
        }

        mHandler.removeCallbacks(mProgressRunnable);
        mHandler.post(mProgressRunnable);
    }

    /**
     * 用户拖动了播放器的位置
     */
    @Override
    public void onPositionDiscontinuity() {
        super.onPositionDiscontinuity();

        if (inErrorState) {
            // This will only occur if the user has performed a seek whilst in the error state. Update
            // the resume position so that if the user then retries, playback will resume from the
            // position to which they seeked.
            updateResumePosition();
        }

        //如果播放器暂停时用户拖动了位置,则开始自动播放
        if (mPlayer != null && !mPlayer.getPlayWhenReady()) {
            mPlayer.setPlayWhenReady(true);
        }
    }

    /**
     * 暂停播放
     */
    public void pausePlayer() {
        if (mPlayer != null) {
            mPlayer.setPlayWhenReady(false);
        }
    }

    /**
     * 继续播放
     */
    public void resumePlayer() {
        if (mPlayer != null) {
            mPlayer.setPlayWhenReady(true);
        }
    }

    /**
     * 播放器是否正在播放
     */
    public boolean isPlaying() {
        return mPlayer != null && mPlayer.getPlayWhenReady()
                && mPlayer.getPlaybackState() != Player.STATE_ENDED
                && mPlayer.getPlaybackState() != Player.STATE_IDLE;
    }

    /**
     * 是否播放结束
     */
    public boolean isPlayEnd() {
        return mPlayer != null && mPlayer.getPlaybackState() == Player.STATE_ENDED;
    }

    /**
     * 播放器是否暂停了播放
     */
    public boolean isPausing() {
        if (mPlayer != null) {
            return !mPlayer.getPlayWhenReady();
        }
        return false;
    }

    private MediaSource createMediaSource(String... playUrls) {
        MediaSource[] ma = new MediaSource[playUrls.length];
        int i = 0;
        for (String url : playUrls) {
            ma[i++] = new ExtractorMediaSource(Uri.parse(url),
                    mMediaDataSourceFactory, mExtractorsFactory, null, null);
        }

        return new ConcatenatingMediaSource(ma);
    }

    private SimpleExoPlayer createNewPlayer(Context context) {
        TrackSelection.Factory adaptiveTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
        return ExoPlayerFactory.newSimpleInstance(context, trackSelector);
    }

    private DataSource.Factory buildDataSourceFactory(Context context, boolean useBandwidthMeter) {
        return buildDataSourceFactory(context, useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    private DataSource.Factory buildDataSourceFactory(Context context, DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(context, bandwidthMeter,
                buildHttpDataSourceFactory(context, bandwidthMeter));
    }

    private HttpDataSource.Factory buildHttpDataSourceFactory(Context context, DefaultBandwidthMeter bandwidthMeter) {
        String httpUserAgent = Util.getUserAgent(context, "ExoPlayer");
        return new DefaultHttpDataSourceFactory(httpUserAgent, bandwidthMeter);
    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {
        super.onPlayerError(e);
        inErrorState = true;
        updateResumePosition();
    }

    private void updateResumePosition() {
//        resumePosition = Math.max(0, mPlayer.getContentPosition());
        resumePosition = mPlayer.getContentPosition();
    }

    long getSavedPosition() {
        return C.POSITION_UNSET;
    }

    private void clearResumePosition() {
        resumePosition = C.POSITION_UNSET;
    }

    void onProgressChanged(float progress) {
//        Logger.d(TAG, "onProgressChanged: " + progress);
    }
}
