/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.cms.data;

import com.namics.distrelec.b2b.facades.product.data.DistVideoData;

/**
 * {@code DistExtCarouselItemData}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class DistExtCarouselItemData extends DistMinCarouselItemData {

    private boolean video;
    private DistVideoData videoData;
    private boolean videoAutoplay;

    public boolean isVideo() {
        return video;
    }

    public void setVideo(final boolean video) {
        this.video = video;
    }

    public DistVideoData getVideoData() {
        return videoData;
    }

    public void setVideoData(final DistVideoData videoData) {
        this.videoData = videoData;
    }

    public boolean isVideoAutoplay() {
        return videoAutoplay;
    }

    public void setVideoAutoplay(final boolean videoAutoplay) {
        this.videoAutoplay = videoAutoplay;
    }
}
