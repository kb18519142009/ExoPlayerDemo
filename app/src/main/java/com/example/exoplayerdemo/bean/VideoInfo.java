package com.example.exoplayerdemo.bean;

/**
 * Description：
 * Created by kang on 2018/3/10.
 */

public class VideoInfo implements IVideoInfo {

    private String title;

    private String path;

    public VideoInfo(String title, String path) {
        this.title = title;
        this.path = path;
    }

    @Override
    public String getVideoTitle() {
        return title;
    }

    @Override
    public String getVideoPath() {
        return path;
    }
}
