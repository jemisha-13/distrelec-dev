package com.namics.distrelec.b2b.core.event;

import com.namics.distrelec.b2b.core.message.queue.model.RowType;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

public class DistInternalLinkEvent extends AbstractEvent {

    private String code;
    private RowType type;
    private String site;
    private String language;
    private boolean force;

    public DistInternalLinkEvent(final String code, final RowType type, final String site, final String language, final boolean force) {
        this.code = code;
        this.type = type;
        this.site = site;
        this.language = language;
        this.force = force;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public RowType getType() {
        return type;
    }

    public void setType(final RowType type) {
        this.type = type;
    }

    public String getSite() {
        return site;
    }

    public void setSite(final String site) {
        this.site = site;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(final boolean force) {
        this.force = force;
    }
}
