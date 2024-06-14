package com.namics.distrelec.b2b.core.util;

import com.namics.hybris.toolbox.media.MediaUtil;

public class DistMediaUtil extends MediaUtil {

    public static String replaceMediaFolder(String mediaFolder) {
        if ("WebShopImages".equals(mediaFolder)) {
            return "images/";
        } else if ("Downloads".equals(mediaFolder)) {
            return "documents/";
        } else if ("Streams".equals(mediaFolder)) {
            return "videos/";
        } else if ("Audio".equals(mediaFolder)) {
            return "audios/";
        } else {
            throw new IllegalArgumentException("Unknown media folder: " + mediaFolder);
        }
    }
}
