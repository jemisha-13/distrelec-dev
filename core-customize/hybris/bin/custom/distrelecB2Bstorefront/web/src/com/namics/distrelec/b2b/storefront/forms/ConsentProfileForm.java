package com.namics.distrelec.b2b.storefront.forms;

import com.namics.distrelec.b2b.facades.user.data.ObsolescenceTempData;

import java.util.List;

public class ConsentProfileForm {
	
	private boolean selectAllemailConsents;
	private boolean emailConsent;
	private boolean saleAndClearanceConsent;
	private boolean knowHowConsent;
	private boolean obsolescenceConsent;
	private boolean smsConsent;
	private boolean phoneConsent;
	private boolean postConsent;
	private boolean personalisationConsent;
	private boolean profilingConsent;
	private boolean newsLetterConsent;
	private boolean termsAndConditionsConsent;
	private boolean personalisedRecommendationConsent;
	private boolean customerSurveysConsent;
	
	private List<ObsolescenceTempData> obsoleCategories;
	
	public boolean isEmailConsent() {
		return emailConsent;
	}
	public void setEmailConsent(boolean emailConsent) {
		this.emailConsent = emailConsent;
	}
	public boolean isSaleAndClearanceConsent() {
		return saleAndClearanceConsent;
	}
	public void setSaleAndClearanceConsent(boolean saleAndClearanceConsent) {
		this.saleAndClearanceConsent = saleAndClearanceConsent;
	}
	public boolean isKnowHowConsent() {
		return knowHowConsent;
	}
	public void setKnowHowConsent(boolean knowHowConsent) {
		this.knowHowConsent = knowHowConsent;
	}
	public boolean isObsolescenceConsent() {
		return obsolescenceConsent;
	}
	public void setObsolescenceConsent(boolean obsolescenceConsent) {
		this.obsolescenceConsent = obsolescenceConsent;
	}
	public boolean isSmsConsent() {
		return smsConsent;
	}
	public void setSmsConsent(boolean smsConsent) {
		this.smsConsent = smsConsent;
	}
	public boolean isPhoneConsent() {
		return phoneConsent;
	}
	public void setPhoneConsent(boolean phoneConsent) {
		this.phoneConsent = phoneConsent;
	}
	public boolean isPostConsent() {
		return postConsent;
	}
	public void setPostConsent(boolean postConsent) {
		this.postConsent = postConsent;
	}
	public boolean isPersonalisationConsent() {
		return personalisationConsent;
	}
	public void setPersonalisationConsent(boolean personalisationConsent) {
		this.personalisationConsent = personalisationConsent;
	}
	public boolean isProfilingConsent() {
		return profilingConsent;
	}
	public void setProfilingConsent(boolean profilingConsent) {
		this.profilingConsent = profilingConsent;
	}
	public boolean isSelectAllemailConsents() {
		return selectAllemailConsents;
	}
	public void setSelectAllemailConsents(boolean selectAllemailConsents) {
		this.selectAllemailConsents = selectAllemailConsents;
	}
    public boolean isNewsLetterConsent() {
        return newsLetterConsent;
    }
    public void setNewsLetterConsent(boolean newsLetterConsent) {
        this.newsLetterConsent = newsLetterConsent;
    }

    public boolean isTermsAndConditionsConsent() {
        return termsAndConditionsConsent;
    }
    public void setTermsAndConditionsConsent(boolean termsAndConditionsConsent) {
        this.termsAndConditionsConsent = termsAndConditionsConsent;
	}

    public List<ObsolescenceTempData> getObsoleCategories() {
        return obsoleCategories;
    }
    public void setObsoleCategories(List<ObsolescenceTempData> obsoleCategories) {
        this.obsoleCategories = obsoleCategories;
    }
	public boolean isPersonalisedRecommendationConsent() {
		return personalisedRecommendationConsent;
	}
	public void setPersonalisedRecommendationConsent(boolean personalisedRecommendationConsent) {
		this.personalisedRecommendationConsent = personalisedRecommendationConsent;
	}
	public boolean isCustomerSurveysConsent() {
		return customerSurveysConsent;
	}
	public void setCustomerSurveysConsent(boolean customerSurveysConsent) {
		this.customerSurveysConsent = customerSurveysConsent;
	}
	
    
	
	
}
