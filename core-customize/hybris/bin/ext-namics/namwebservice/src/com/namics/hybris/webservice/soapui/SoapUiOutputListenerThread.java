/*
 * Copyright 2000-2011 Namics AG. All rights reserved.
 */

package com.namics.hybris.webservice.soapui;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class SoapUiOutputListenerThread extends Thread {
    private static final Logger LOG = Logger.getLogger(SoapUiOutputListenerThread.class);

    public static final String SOAP_UI_IS_RUNNING_STRING = "Press any key to terminate...";

    protected InputStreamReader standardInputStream;
    protected InputStreamReader errorInputStream;

    protected boolean printStandardOutput = true;
    protected boolean writeToSysout;
    protected boolean soapUiLoadedSignaled;
    protected boolean stopSignaled;

    public SoapUiOutputListenerThread(final InputStream inputStream, final InputStream errorStream, final boolean printStandardOutput,
            final boolean writeToSysout) {
        this.standardInputStream = new InputStreamReader(inputStream);
        this.errorInputStream = new InputStreamReader(errorStream);
        this.printStandardOutput = printStandardOutput;
        this.writeToSysout = writeToSysout;
    }

    @Override
    public void run() {
        while (!stopSignaled) {
            try {
                final StringBuffer sb = new StringBuffer();
                while (standardInputStream.ready()) {
                    final int standardOutput = standardInputStream.read();
                    if (standardOutput > -1 && standardOutput != '\n') {
                        sb.append((char) standardOutput);
                    } else {
                        break;
                    }
                }
                final String line = sb.toString();
                if (!line.isEmpty()) {

                    if (line.contains(SOAP_UI_IS_RUNNING_STRING)) {
                        soapUiLoadedSignaled = true;
                    } else {
                        if (printStandardOutput) {
                            writeLine(line.replaceAll("\n", "").replaceAll("\r", ""));
                        }
                    }
                }

            } catch (final Exception e) {
                // ignore
            }
        }
    }

    protected void writeLine(final String line) {
        LOG.info(line);
        if (writeToSysout) {
            System.out.println(line);
        }
    }

    public void stopOutputWriting() {
        this.stopSignaled = true;
    }

    public boolean isSoapUiLoaded() {
        return this.soapUiLoadedSignaled;
    }

}