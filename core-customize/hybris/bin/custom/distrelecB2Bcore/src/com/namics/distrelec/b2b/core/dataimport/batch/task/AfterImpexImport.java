/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.dataimport.batch.task;

import java.io.File;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.servicelayer.impex.ImportConfig;

/**
 * Execute some business logic after an ImpEx file has been executed. To specify which business logic has be executed, you can define a map
 * entry in the XML configuration of the bean batchRunnerTask
 * 
 * @author dathusir, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public interface AfterImpexImport {

    void after(final ImportConfig importConfig, final BatchHeader batchHeader, final File originalFile, final File impexFile);

}
