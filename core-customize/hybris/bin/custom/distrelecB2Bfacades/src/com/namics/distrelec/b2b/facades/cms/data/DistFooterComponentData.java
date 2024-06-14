/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.cms.data;

import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;

import java.util.List;

/**
 * {@code DistFooterComponentData}
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>,
 * Distrelec
 * @since Distrelec 3.0
 */
public class DistFooterComponentData {

	private String uid;
	private String name;
	private String notice;
	private int wrapAfter;
	private List<DistFooterComponentItemData> usps;
	private List<DistFooterComponentItemData> addedValues;
	private List<DistExternalLinkData> socialMedias;
	private List<CMSNavigationNodeModel> navigationNodes;
	private List<CMSLinkComponentModel> impressumLinks;
	private List<DistExternalLinkData> paymentMethods;
	private List<DistExternalLinkData> trademarks;
	private List<CMSLinkComponentModel> countryLinks;

	private boolean checkoutFooter;

	public String getNotice() {
		return notice;
	}

	public void setNotice(final String notice) {
		this.notice = notice;
	}

	public int getWrapAfter() {
		return wrapAfter;
	}

	public void setWrapAfter(final int wrapAfter) {
		this.wrapAfter = wrapAfter;
	}

	public List<DistFooterComponentItemData> getUSPs() {
		return usps;
	}

	public void setUSPs(final List<DistFooterComponentItemData> uSPs) {
		usps = uSPs;
	}

	public List<DistFooterComponentItemData> getAddedValues() {
		return addedValues;
	}

	public void setAddedValues(final List<DistFooterComponentItemData> addedValues) {
		this.addedValues = addedValues;
	}

	public List<DistExternalLinkData> getSocialMedias() {
		return socialMedias;
	}

	public void setSocialMedias(final List<DistExternalLinkData> socialMedias) {
		this.socialMedias = socialMedias;
	}

	public List<CMSNavigationNodeModel> getNavigationNodes() {
		return navigationNodes;
	}

	public void setNavigationNodes(final List<CMSNavigationNodeModel> navigationNodes) {
		this.navigationNodes = navigationNodes;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(final String uid) {
		this.uid = uid;
	}

	public List<CMSLinkComponentModel> getImpressumLinks() {
		return impressumLinks;
	}

	public void setImpressumLinks(final List<CMSLinkComponentModel> cmsLinks) {
		this.impressumLinks = cmsLinks;
	}

	public boolean isCheckoutFooter() {
		return checkoutFooter;
	}

	public void setCheckoutFooter(final boolean checkoutFooter) {
		this.checkoutFooter = checkoutFooter;
	}

	public List<DistExternalLinkData> getPaymentMethods() {
		return paymentMethods;
	}

	public void setPaymentMethods(final List<DistExternalLinkData> paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

	public List<DistExternalLinkData> getTrademarks() {
		return trademarks;
	}

	public void setTrademarks(final List<DistExternalLinkData> trademarks) {
		this.trademarks = trademarks;
	}

	public List<CMSLinkComponentModel> getCountryLinks() {
		return countryLinks;
	}

	public void setCountryLinks(final List<CMSLinkComponentModel> countryLinks) {
		this.countryLinks = countryLinks;
	}

}
