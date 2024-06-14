/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.dataimport.batch.task;

import java.io.File;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.servicelayer.impex.ImportConfig;

/**
 * {@code BeforeImpexImport}
 * <p>
 * A job to be executed before importing each impex file.
 * </p>
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.8
 */
public interface BeforeImpexImport {

    void before(final ImportConfig importConfig, final BatchHeader batchHeader, final File originalFile, final File impexFile);
}
