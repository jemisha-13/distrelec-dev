package com.namics.hybris.ffsearch.export.sequence;

public class Channel {

    private final String code;
    private final String languageIsocode;

    public Channel(final String code, final String languageIsocode) {
        this.code = code;
        this.languageIsocode = languageIsocode;
    }

    public String getCode() {
        return code;
    }

    public String getLanguageIsocode() {
        return languageIsocode;
    }
}
