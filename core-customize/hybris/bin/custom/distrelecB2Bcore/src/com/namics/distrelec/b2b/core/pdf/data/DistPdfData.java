package com.namics.distrelec.b2b.core.pdf.data;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.Map;

public class DistPdfData {

    private String templateName;

    private String xslName;

    private Map<String, Object> messages;

    private CMSSiteModel cmsSiteModel;

    private LanguageModel language;

    private CustomerModel customer;

    private String logoUrl;

    private Map<String, Object> additionalParameters;

    public DistPdfData(String templateName, String xslName) {
        this.templateName = templateName;
        this.xslName = xslName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getXslName() {
        return xslName;
    }

    public void setXslName(String xslName) {
        this.xslName = xslName;
    }

    public Map<String, Object> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, Object> messages) {
        this.messages = messages;
    }

    public CMSSiteModel getCmsSiteModel() {
        return cmsSiteModel;
    }

    public void setCmsSiteModel(CMSSiteModel cmsSiteModel) {
        this.cmsSiteModel = cmsSiteModel;
    }

    public LanguageModel getLanguage() {
        return language;
    }

    public void setLanguage(LanguageModel language) {
        this.language = language;
    }

    public CustomerModel getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerModel customer) {
        this.customer = customer;
    }

    public Map<String, Object> getAdditionalParameters() {
        return additionalParameters;
    }

    public void setAdditionalParameters(Map<String, Object> additionalParameters) {
        this.additionalParameters = additionalParameters;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
