/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.data;

import de.hybris.platform.commercefacades.product.data.CategoryData;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code DistCategoryIndexData}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.6
 */
public class DistCategoryIndexData extends CategoryData {

    public static final String SPECIAL_SHOPS_PIM_CATEGORY_541300 = "cat-l1d_541300";
    public static final String SPECIAL_SHOPS_PIM_CATEGORY_90 = "cat-dnav_90";
    private final String [] specials = new String [] {SPECIAL_SHOPS_PIM_CATEGORY_90, SPECIAL_SHOPS_PIM_CATEGORY_541300};
    private static final String WORD_BOUNDARY = "\\b";
    private static final String FORWARD_SLASH = "/";
    private int level;
    private List<DistCategoryIndexData> children;

    private static final int ZERO = 0;

    public int getLevel() {
        return level;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public List<DistCategoryIndexData> getChildren() {
        return children;
    }

    public void setChildren(final List<DistCategoryIndexData> children) {
        this.children = children;
    }

    public boolean hasChildren() {
        return children != null && children.size() > ZERO;
    }

    public boolean urlIsNotNull(){
        return getUrl() != null;
    }

    public boolean isNotASpecialShop(){
        final String url = getUrl().toLowerCase();
        final String shopIdentifier = url.substring(url.lastIndexOf(FORWARD_SLASH), url.length());

        for(String shop: specials){
            final String pattern =  WORD_BOUNDARY+shop+WORD_BOUNDARY;
            final Matcher matcher = Pattern.compile(pattern).matcher(shopIdentifier);
            if(matcher.find()){
                return false;
            }
        }
        return true;
    }
}
