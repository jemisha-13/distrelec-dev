/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.spring.integration;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.integration.file.locking.NioFileLocker;
import org.springframework.messaging.MessagingException;

/**
 * Tries to lock the file to determine if the fil is fully uploaded.
 * 
 * @author dsivakumaran, Namics AG
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class DistFileLocker extends NioFileLocker {

    private static final Logger LOG = LogManager.getLogger(DistFileLocker.class);

    @Override
    public boolean accept(final File file) {
        if (file != null && file.isFile()) {
            boolean locked = false;
            synchronized (this) {
                try {
                    locked = lock(file);
                } catch (final MessagingException e) {
                    // don't print because if it fails it's allright. It just means that another process is still using this file (e.g. still
                    // beeing copied).
                    LOG.debug("File cannot be locked (File probably is still being copied). Therefore the process cannot be started.");
                }
                if (locked) {
                    unlock(file);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void unlock(final File fileToUnlock) {
        try {
            super.unlock(fileToUnlock);
        } catch (final MessagingException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
