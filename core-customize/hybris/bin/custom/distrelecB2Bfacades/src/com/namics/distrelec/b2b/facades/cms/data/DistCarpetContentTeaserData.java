/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.cms.data;

import de.hybris.platform.cms2.enums.LinkTargets;
import de.hybris.platform.commercefacades.product.data.ImageData;

/**
 * POJO for DistCarpetContentTeaser data.
 *
 * @author lzamofing, Distrelec
 * @since Distrelec 1.0
 *
 */
public class DistCarpetContentTeaserData {

	private String title;
	private String text;
	private String subText;
	private String link;
	private LinkTargets linkTarget;
	private ImageData imageLarge;
	private ImageData imageSmall;
	private boolean fullsize;
	private String youTubeID;

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(final String text) {
		this.text = text;
	}

	public String getSubText() {
		return subText;
	}

	public void setSubText(final String subText) {
		this.subText = subText;
	}

	public String getLink() {
		return link;
	}

	public void setLink(final String link) {
		this.link = link;
	}

	public LinkTargets getLinkTarget() {
		return linkTarget;
	}

	public void setLinkTarget(final LinkTargets linkTarget) {
		this.linkTarget = linkTarget;
	}

	public ImageData getImageLarge() {
		return imageLarge;
	}

	public void setImageLarge(final ImageData imageLarge) {
		this.imageLarge = imageLarge;
	}

	public ImageData getImageSmall() {
		return imageSmall;
	}

	public void setImageSmall(final ImageData imageSmall) {
		this.imageSmall = imageSmall;
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
