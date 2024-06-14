/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.cms.data;

import de.hybris.platform.cms2.enums.LinkTargets;
import de.hybris.platform.commercefacades.product.data.ImageData;

/**
 * {@code DistMinCarouselItemData}
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>,
 *         Distrelec
 * @since Distrelec 3.0
 */
public class DistMinCarouselItemData {

	private ImageData picture;
	private String title;
	private String name;
	private String url;
	private LinkTargets linkTarget;
	private boolean fullsize;
	private String youTubeID;

	public ImageData getPicture() {
		return picture;
	}

	public void setPicture(final ImageData picture) {
		this.picture = picture;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public LinkTargets getLinkTarget() {
		return linkTarget;
	}

	public void setLinkTarget(final LinkTargets linkTarget) {
		this.linkTarget = linkTarget;
	}

	public boolean isFullsize() {
		return fullsize;
	}

	public void setFullsize(final boolean fullsize) {
		this.fullsize = fullsize;
	}

	public String getYouTubeID() {
		return youTubeID;
	}

	public void setYouTubeID(final String youTubeID) {
		this.youTubeID = youTubeID;
	}
}
