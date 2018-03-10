package com.example.exoplayerdemo.bean;

import java.io.Serializable;

/**
 * Description：视频数据类
 * Created by kang on 2018/3/10.
 */

public interface IVideoInfo extends Serializable {
    /**
     * 视频标题
     */
    String getVideoTitle();

    /**
     * 视频播放路径 url / file path
     */
    String getVideoPath();
}
