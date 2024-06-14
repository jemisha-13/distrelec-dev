/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.search;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class AdditionalControlParams {

    private boolean generateASN;
    private boolean idsOnly;
    private boolean generateAdvisorTree;
    private boolean useCampaigns = true;
    private boolean useFoundWords;
    private boolean useKeywords;
    private boolean usePersonalization;

    public boolean isGenerateASN() {
        return generateASN;
    }

    public void setGenerateASN(final boolean generateASN) {
        this.generateASN = generateASN;
    }

    public boolean isIdsOnly() {
        return idsOnly;
    }

    public void setIdsOnly(final boolean idsOnly) {
        this.idsOnly = idsOnly;
    }

    public boolean isGenerateAdvisorTree() {
        return generateAdvisorTree;
    }

    public void setGenerateAdvisorTree(final boolean generateAdvisorTree) {
        this.generateAdvisorTree = generateAdvisorTree;
    }

    /**
     * Tells whether we need to use campaigns or not.
     * <h4>The default value is {@code true}</h4>
     * 
     * @return {@code true} if we need campaigns, otherwise {@code false}
     */
    public boolean isUseCampaigns() {
        return useCampaigns;
    }

    public void setUseCampaigns(final boolean useCampaigns) {
        this.useCampaigns = useCampaigns;
    }

    public boolean isUseFoundWords() {
        return useFoundWords;
    }

    public void setUseFoundWords(final boolean useFoundWords) {
        this.useFoundWords = useFoundWords;
    }

    public boolean isUseKeywords() {
        return useKeywords;
    }

    public void setUseKeywords(final boolean useKeywords) {
        this.useKeywords = useKeywords;
    }

    public boolean isUsePersonalization() {
        return usePersonalization;
    }

    public void setUsePersonalization(boolean usePersonalization) {
        this.usePersonalization = usePersonalization;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
