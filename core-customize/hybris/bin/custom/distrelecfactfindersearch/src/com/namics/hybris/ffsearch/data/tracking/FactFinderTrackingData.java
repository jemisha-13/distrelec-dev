/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.tracking;

/**
 * POJO for a tracking data object.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class FactFinderTrackingData {

    // Generic attributes
    private String id;
    private String channel;
    private String sid;
    private FactFinderTrackingEventEnum event;
    private String title;
    private String userId;
    private String cookieId;

    // Detailpage specific
    private String query;
    private Integer pos;
    private Integer origPos;
    private Integer page;
    private Integer pageSize;
    private Integer origPageSize;
    private Float simi;

    // Cart specific
    private Integer count;
    private Double price;

    // Recommendation specific
    private String mainId;

    // Feedback specific
    private Boolean positive;
    private String message;
    
    private String campaign;
    
    // / BEGIN GENERATED CODE

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(final String channel) {
        this.channel = channel;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(final String sid) {
        this.sid = sid;
    }

    public FactFinderTrackingEventEnum getEvent() {
        return event;
    }

    public void setEvent(final FactFinderTrackingEventEnum event) {
        this.event = event;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getCookieId() {
        return cookieId;
    }

    public void setCookieId(final String cookieId) {
        this.cookieId = cookieId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(final String query) {
        this.query = query;
    }

    public Integer getPos() {
        return pos;
    }

    public void setPos(final Integer pos) {
        this.pos = pos;
    }

    public Integer getOrigPos() {
        return origPos;
    }

    public void setOrigPos(final Integer origPos) {
        this.origPos = origPos;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(final Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(final Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getOrigPageSize() {
        return origPageSize;
    }

    public void setOrigPageSize(final Integer origPageSize) {
        this.origPageSize = origPageSize;
    }

    public Float getSimi() {
        return simi;
    }

    public void setSimi(final Float simi) {
        this.simi = simi;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(final Integer count) {
        this.count = count;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(final Double price) {
        this.price = price;
    }

    public String getMainId() {
        return mainId;
    }

    public void setMainId(final String mainId) {
        this.mainId = mainId;
    }

    public Boolean getPositive() {
        return positive;
    }

    public void setPositive(final Boolean positive) {
        this.positive = positive;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }
    
    

}
