package com.example.exoplayerdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TimeUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    private static final String PLAY_VIDEO_URL = "http://small-bronze.oss-cn-shanghai.aliyuncs.com/video/de_logo/2018/1/8/B74AFBC46F524D1D968A9905AE0DB39C.mp4";

    @BindView(R.id.simpleExoPlayerView)
    SimpleExoPlayerView simpleExoPlayerView;

    //ExoPlayer相关成员变量
    protected SimpleExoPlayer mPlayer;
    private DataSource.Factory mMediaDataSourceFactory;
    private ExtractorsFactory mExtractorsFactory;

    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initVideo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPlayer != null && mPlayer.getCurrentPosition() > 0) {
            mPlayer.setPlayWhenReady(true); //播放
        }
    }

    //初始化视频
    private void initVideo() {
        if (mMediaDataSourceFactory == null) {
            mMediaDataSourceFactory = buildDataSourceFactory(this, false);
        }

        if (mExtractorsFactory == null) {
            mExtractorsFactory = new DefaultExtractorsFactory();
        }

        if (mPlayer == null) {
            mPlayer = createNewPlayer(this);
            mPlayer.addListener(eventListener);
        }

        mPlayer.prepare(createMediaSource(PLAY_VIDEO_URL));
        simpleExoPlayerView.setPlayer(mPlayer);
        mPlayer.setPlayWhenReady(true);
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

    private MediaSource createMediaSource(String... playUrls) {
        MediaSource[] ma = new MediaSource[playUrls.length];
        int i = 0;
        for (String url : playUrls) {
            ma[i++] = new ExtractorMediaSource(Uri.parse(url),
                    mMediaDataSourceFactory, mExtractorsFactory, null, null);
        }
        return new ConcatenatingMediaSource(ma);
    }

    //播放事件监听
    private ExoPlayer.EventListener eventListener = new ExoPlayer.EventListener() {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            System.out.println("播放: onTimelineChanged 周期总数 " + timeline);
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            System.out.println("播放: TrackGroupArray  ");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case Player.STATE_ENDED:
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPlayer.prepare(createMediaSource(PLAY_VIDEO_URL));
                            simpleExoPlayerView.setPlayer(mPlayer);
//                            simpleExoPlayerView.setUseController(false);
                            mPlayer.setPlayWhenReady(true);
                        }
                    }, 3000);
                    break;
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Log.e(TAG, "onPlayerError: " + "播放: onPlayerError");
        }

        @Override
        public void onPositionDiscontinuity() {
            Log.e(TAG, "onPositionDiscontinuity: ");
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer != null && mPlayer.getPlayWhenReady()) {
            mPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放播放器
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }
}
