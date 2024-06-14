/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.distrelec.smartedit;

import org.apache.log4j.Logger;

/**
 * Simple test class to demonstrate how to include utility classes to your webmodule.
 */
public class DistrelecsmarteditWebHelper {
    /**
     * Edit the local|project.properties to change logging behavior (properties log4j.*).
     */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(DistrelecsmarteditWebHelper.class.getName());

    public static final String getTestOutput() {
        return "testoutput";
    }
}
