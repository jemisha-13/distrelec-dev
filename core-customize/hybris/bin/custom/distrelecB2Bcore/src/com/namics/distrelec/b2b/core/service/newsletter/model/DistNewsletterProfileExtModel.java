/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.newsletter.model;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Model class to handle a newsletter profile.
 * 
 * @author pnueesch, Namics AG
 * @since Distrelec 1.0
 * 
 */
public class DistNewsletterProfileExtModel {

    private String email;

    private String titleCode;

    private String firstName;

    private String lastName;

    private String division;

    private String role;

    private Boolean segmentedTopic;

    private Boolean activeSubscription;
    
    private Boolean anonymousUser;
    
    private String consentType;
    private String erpContactId;
    private Date modifiedTime;
    private String marketingArea;
    private String contactOrigin;
    private String lastChangedbyUser;
    private String communicationCategory;
    private String consentStatus;
    private Boolean npsSubscription;
    private Boolean isRegistration;
    private String phoneNumber;
    private String mobileNumber;
    private Boolean sendWelcomeEmail;	
	private boolean smsPermissions;		
	private boolean phonePermission;		
	private boolean paperPermission;		
	private boolean personalisationSubscription;		
	private boolean profilingSubscription;
    
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getTitleCode() {
        return titleCode;
    }

    public void setTitleCode(final String titleCode) {
        this.titleCode = titleCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(final String division) {
        this.division = division;
    }

    public Boolean getSegmentedTopic() {
        return segmentedTopic;
    }

    public void setSegmentedTopic(final Boolean segmentedTopic) {
        this.segmentedTopic = segmentedTopic;
    }

    public Boolean getActiveSubscription() {
        return activeSubscription;
    }

    public void setActiveSubscription(final Boolean activeSubscription) {
        this.activeSubscription = activeSubscription;
    }

    public String getRole() {
        return role;
    }

    public void setRole(final String role) {
        this.role = role;
    }
    
    public Boolean getAnonymousUser() {
        return anonymousUser;
    }

    public void setAnonymousUser(Boolean anonymousUser) {
        this.anonymousUser = anonymousUser;
    }
    
    public String getConsentType() {
        return consentType;
    }

    public void setConsentType(String consentType) {
        this.consentType = consentType;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
    

    public String getErpContactId() {
        return erpContactId;
    }

    public void setErpContactId(String erpContactId) {
        this.erpContactId = erpContactId;
    }
    
    public String getMarketingArea() {
        return marketingArea;
    }

    public void setMarketingArea(String marketingArea) {
        this.marketingArea = marketingArea;
    }

    public String getContactOrigin() {
        return contactOrigin;
    }

    public void setContactOrigin(String contactOrigin) {
        this.contactOrigin = contactOrigin;
    }

    public String getLastChangedbyUser() {
        return lastChangedbyUser;
    }

    public void setLastChangedbyUser(String lastChangedbyUser) {
        this.lastChangedbyUser = lastChangedbyUser;
    }
    
    

    public String getCommunicationCategory() {
        return communicationCategory;
    }

    public void setCommunicationCategory(String communicationCategory) {
        this.communicationCategory = communicationCategory;
    }

    public String getConsentStatus() {
        return consentStatus;
    }

    public void setConsentStatus(String consentStatus) {
        this.consentStatus = consentStatus;
    }

    public Boolean getNpsSubscription() {
        return npsSubscription;
    }

    public void setNpsSubscription(Boolean npsSubscription) {
        this.npsSubscription = npsSubscription;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public Boolean getIsRegistration() {
        return isRegistration;
    }

    public void setIsRegistration(Boolean isRegistration) {
        this.isRegistration = isRegistration;
    }

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Boolean getSendWelcomeEmail() {
		return sendWelcomeEmail;
	}

	public void setSendWelcomeEmail(Boolean sendWelcomeEmail) {
		this.sendWelcomeEmail = sendWelcomeEmail;
	}

	public boolean isSmsPermissions() {
		return smsPermissions;
	}

	public void setSmsPermissions(boolean smsPermissions) {
		this.smsPermissions = smsPermissions;
	}

	public boolean isPhonePermission() {
		return phonePermission;
	}

	public void setPhonePermission(boolean phonePermission) {
		this.phonePermission = phonePermission;
	}

	public boolean isPaperPermission() {
		return paperPermission;
	}

	public void setPaperPermission(boolean paperPermission) {
		this.paperPermission = paperPermission;
	}

	public boolean isPersonalisationSubscription() {
		return personalisationSubscription;
	}

	public void setPersonalisationSubscription(boolean personalisationSubscription) {
		this.personalisationSubscription = personalisationSubscription;
	}

	public boolean isProfilingSubscription() {
		return profilingSubscription;
	}

	public void setProfilingSubscription(boolean profilingSubscription) {
		this.profilingSubscription = profilingSubscription;
	}
    
	
    

}
