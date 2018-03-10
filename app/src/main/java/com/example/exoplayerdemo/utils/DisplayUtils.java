package com.example.exoplayerdemo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

/**
 * Description：
 * Created by kang on 2018/3/10.
 */

public class DisplayUtils {
    /**
     * 切换屏幕的方向.
     */
    public static void toggleScreenOrientation(Activity activity) {
        activity.setRequestedOrientation(isPortrait(activity)
                ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 获得当前屏幕的方向.
     *
     * @return 是否竖屏.
     */
    public static boolean isPortrait(Context context) {
        int orientation = context.getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_PORTRAIT;
    }
}
