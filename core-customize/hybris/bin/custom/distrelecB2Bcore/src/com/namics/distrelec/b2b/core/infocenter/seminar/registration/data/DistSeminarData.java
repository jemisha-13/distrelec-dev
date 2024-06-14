/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.infocenter.seminar.registration.data;

import java.util.Date;
import java.util.List;


/**
 * {@code SeminarData}
 * 
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class DistSeminarData {

    private String uid;
    private String topic;
    private Date date;
    private Date registrationDelay;
    private String note;
    private String speakers;
    private String shortDesc;
    private String content;
    private String costDesc;
    private String location;
    private String prerequisites;
    private String schedule;
    private List<DistSeminarRegistrationData> registrations;

    /* Getters & Setters */

    public String getUid() {
        return uid;
    }

    public void setUid(final String uid) {
        this.uid = uid;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(final String topic) {
        this.topic = topic;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public Date getRegistrationDelay() {
        return registrationDelay;
    }

    public void setRegistrationDelay(final Date registrationDelay) {
        this.registrationDelay = registrationDelay;
    }

    public String getNote() {
        return note;
    }

    public void setNote(final String note) {
        this.note = note;
    }

    public String getSpeakers() {
        return speakers;
    }

    public void setSpeakers(final String speakers) {
        this.speakers = speakers;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(final String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getCostDesc() {
        return costDesc;
    }

    public void setCostDesc(final String costDesc) {
        this.costDesc = costDesc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public String getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(final String prerequisites) {
        this.prerequisites = prerequisites;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(final String schedule) {
        this.schedule = schedule;
    }

    public List<DistSeminarRegistrationData> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(final List<DistSeminarRegistrationData> registrations) {
        this.registrations = registrations;
    }
}
