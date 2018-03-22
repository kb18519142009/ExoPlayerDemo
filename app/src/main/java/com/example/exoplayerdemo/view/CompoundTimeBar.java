package com.example.exoplayerdemo.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.exoplayer2.ui.TimeBar;

import java.util.ArrayList;
import java.util.List;

/**
 * 组合列表TimeBar
 * Created by kelseo on 2017/9/20.
 */
public class CompoundTimeBar extends View implements TimeBar {

    private static final String TAG = "CompoundTimeBar";

    List<TimeBar> mTimeBars = new ArrayList<>();

    private OnScrubListener mListener;

    public CompoundTimeBar(Context context) {
        super(context);
    }

    public CompoundTimeBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CompoundTimeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addTimeBar(TimeBar timeBar) {
        if (mListener != null) {
            timeBar.setListener(mListener);
        }
        mTimeBars.add(timeBar);
    }

    @Override
    public void setEnabled(boolean enabled) {
//        Logger.d(TAG, "setEnabled: " + enabled + ", size : " + mTimeBars.size());
        for (TimeBar timeBar : mTimeBars) {
            timeBar.setEnabled(enabled);
        }
    }

    @Override
    public void setListener(OnScrubListener listener) {
        mListener = listener;
//        Logger.d(TAG, "setListener: " + listener + ", size : " + mTimeBars.size());
        for (TimeBar timeBar : mTimeBars) {
            timeBar.setListener(listener);
        }
    }

    @Override
    public void setKeyTimeIncrement(long time) {
//        Logger.d(TAG, "setKeyTimeIncrement: " + time + ", size : " + mTimeBars.size());
        for (TimeBar timeBar : mTimeBars) {
            timeBar.setKeyTimeIncrement(time);
        }
    }

    @Override
    public void setKeyCountIncrement(int count) {
//        Logger.d(TAG, "setKeyCountIncrement: " + count + ", size : " + mTimeBars.size());
        for (TimeBar timeBar : mTimeBars) {
            timeBar.setKeyCountIncrement(count);
        }
    }

    @Override
    public void setPosition(long position) {
//        Logger.d(TAG, "setPosition: " + position + ", size : " + mTimeBars.size());
        for (TimeBar timeBar : mTimeBars) {
            timeBar.setPosition(position);
        }
    }

    @Override
    public void setBufferedPosition(long bufferedPosition) {
//        Logger.d(TAG, "setBufferedPosition: " + bufferedPosition + ", size : " + mTimeBars.size());
        for (TimeBar timeBar : mTimeBars) {
            timeBar.setBufferedPosition(bufferedPosition);
        }
    }

    @Override
    public void setDuration(long duration) {
//        Logger.d(TAG, "setDuration: " + duration + ", size : " + mTimeBars.size());
        for (TimeBar timeBar : mTimeBars) {
            timeBar.setDuration(duration);
        }
    }

    @Override
    public void setAdGroupTimesMs(@Nullable long[] adGroupTimesMs, @Nullable boolean[] playedAdGroups, int adGroupCount) {
        for (TimeBar timeBar : mTimeBars) {
            timeBar.setAdGroupTimesMs(adGroupTimesMs, playedAdGroups, adGroupCount);
        }
    }
}
