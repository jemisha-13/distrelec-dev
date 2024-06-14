/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement.service.cxml.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CxmlOutboundSection {

    private Map<String, String> fields;
    private String hookUrlFieldName;

    public CxmlOutboundSection(Map<String, String> requestParameters, String hookUrlFieldName) {

        this.hookUrlFieldName = hookUrlFieldName;
        fields = new HashMap<String, String>();

        setAllFields(requestParameters);

    }

    private void setAllFields(Map<String, String> map) {
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry mapEntry = (Map.Entry) it.next();
            setField((String) mapEntry.getKey(), (String) mapEntry.getValue());
        }

    }

    protected void setField(String name, String value) {
        if (name == null) {
            return;
        }
        this.fields.put(name.toUpperCase(), value);
    }

    public String getField(String name) {
        return ((name != null) ? (String) this.fields.get(name.toUpperCase()) : null);
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public String getHookURLFieldName() {
        return hookUrlFieldName;
    }

}
