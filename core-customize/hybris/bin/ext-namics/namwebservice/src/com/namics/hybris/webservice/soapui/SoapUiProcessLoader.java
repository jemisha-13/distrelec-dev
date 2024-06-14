/*
 * Copyright 2000-2011 Namics AG. All rights reserved.
 */

package com.namics.hybris.webservice.soapui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Loads the Soap-UI-Server in a separate JVM.
 * 
 * @author jweiss, Namics AG
 * 
 */
public class SoapUiProcessLoader {
    // private static Logger log = Logger.getLogger(ProcessHelper.class);
    private Process process;
    private SoapUiOutputListenerThread outputWriter;

    private boolean printJvmSummary = true;
    private boolean printClasspathSummary;
    private boolean printStandardOutput = true;
    private boolean printStandardOutputToSysout;

    private String projectFileLocation;

    private String baseSoapUiLocation;

    private String librariesLocation;
    private String extensionsLocation;
    private String soapuiSettingsLocation;
    private String soapuiListeningPort;

    private static final long WAIT_MILLISECONDS = 250;
    /**
     * How long to to wait till timeout is signaled.
     */
    private long waitTimoutMilliseconds = 45 * 1000; // seconds to wait till timeout is signaled

    private static final String CLASS_TO_LOAD = "com.eviware.soapui.tools.SoapUIMockServiceRunner";

    public SoapUiProcessLoader(final String projectFileLocation, final String baseSoapUiLocation, final String port) {
        this.process = null;
        this.projectFileLocation = projectFileLocation;
        this.baseSoapUiLocation = baseSoapUiLocation;
        this.librariesLocation = baseSoapUiLocation + "/libs";
        this.extensionsLocation = baseSoapUiLocation + "/ext";
        this.soapuiSettingsLocation = baseSoapUiLocation + "/soapui-settings.xml";
        this.soapuiListeningPort = port;

    }

    /**
     * Starts the Soap-UI Server and returns when the Server is up-and-running.
     * 
     * @throws SoapUiException
     *             If the SOAP-UI-Server couldn't be started or a timeout occured.
     */
    public void startSoapUiServer() {
        final String optionsAsString = "";
        final String[] arguments = new String[] { this.projectFileLocation, "-s " + this.soapuiSettingsLocation, "-p" + this.soapuiListeningPort };

        String[] additionalClasspath = new String[] {};

        final File libsDirectory = new File(librariesLocation);
        if (!libsDirectory.exists() || !libsDirectory.isDirectory()) {
            throw new SoapUiException("The directory '" + libsDirectory.getAbsolutePath() + "' doesn't exists or isn't a directory.");
        }

        final File[] libraries = libsDirectory.listFiles(new JarFilter());
        if (libraries == null) {
            throw new SoapUiException("The libraries couldn't be found on path '" + libsDirectory.getAbsolutePath() + "'.");
        }
        final List<String> libraryLocations = new ArrayList<String>();
        for (final File libraryFile : libraries) {
            libraryLocations.add(libraryFile.getAbsolutePath());
        }
        additionalClasspath = libraryLocations.toArray(additionalClasspath);

        final ProcessBuilder processBuilder = createProcess(optionsAsString, CLASS_TO_LOAD, arguments, additionalClasspath);
        processBuilder.redirectErrorStream(true);
        try {
            this.process = processBuilder.start();
        } catch (final IOException e) {
            throw new SoapUiException("During the start of SoapUI-Server, there was an exception.", e);
        }

        this.outputWriter = new SoapUiOutputListenerThread(this.process.getInputStream(), this.process.getErrorStream(), isPrintStandardOutput(),
                isPrintStandardOutputToSysout());
        this.outputWriter.start();

        long waitingIterations = 0;
        final long iterationsTillTimeout = waitTimoutMilliseconds / WAIT_MILLISECONDS;
        while (!this.outputWriter.isSoapUiLoaded()) {
            try {
                Thread.sleep(WAIT_MILLISECONDS);
                waitingIterations++;
            } catch (final InterruptedException e) {
                // continue
            }

            if (waitingIterations > iterationsTillTimeout) {
                // run into timeout
                final long timeoutSeconds = waitTimoutMilliseconds / 1000;
                throw new SoapUiException("SoapUI-Server couldn't start successfully and run into a timeout (after " + timeoutSeconds + " seconds).");
            }
        }
    }

    private ProcessBuilder createProcess(final String optionsAsString, final String mainClass, final String[] arguments, final String[] additionalClasspath) {
        // System.out.println("FileSeparator: " + File.separator);
        // System.out.println("PathSeparator: " + File.pathSeparator);

        final String jvm = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final String classpath = System.getProperty("java.class.path");

        final StringBuffer classpathStringBuffer = new StringBuffer();
        classpathStringBuffer.append(classpath);
        for (final String classpathEntry : additionalClasspath) {
            classpathStringBuffer.append(File.pathSeparatorChar); // Mac uses a :, DOS uses a ;
            classpathStringBuffer.append(classpathEntry);
        }

        if (printJvmSummary) {
            final String workingDirectory = System.getProperty("user.dir");
            final String runtimeName = System.getProperty("java.runtime.name");
            final String version = System.getProperty("java.runtime.version");
            final String userName = System.getProperty("user.name");

            System.out.println("******************************************");
            System.out.println("* Starting SoapUI Server process...");
            System.out.println("* JVM " + runtimeName);
            System.out.println("* JVM-Version: " + version);
            System.out.println("* JVM-Directory: " + jvm);
            System.out.println("* Working-Directory: " + workingDirectory);
            System.out.println("* User: " + userName);
            System.out.println("******************************************");

        }

        if (printClasspathSummary) {
            final String[] classpathEntries = classpathStringBuffer.toString().split(";");

            System.out.println("**** Summary of classpath ****************");
            for (final String classpathEntry : classpathEntries) {
                System.out.println(classpathEntry);
            }
            System.out.println("**** End of classpath summary ************");
        }

        final String[] options = optionsAsString.isEmpty() ? new String[0] : optionsAsString.split(" ");
        final List<String> command = new ArrayList<String>();
        command.add(jvm);
        command.addAll(Arrays.asList(options));
        command.add(mainClass);
        command.addAll(Arrays.asList(arguments));

        final ProcessBuilder processBuilder = new ProcessBuilder(command);
        final Map<String, String> environment = processBuilder.environment();
        environment.put("CLASSPATH", classpathStringBuffer.toString());

        if (printClasspathSummary) {
            System.out.println("**** Command *****************************");
            System.out.print("* ");
            for (final String commandEntry : command) {
                System.out.print(commandEntry + " ");
            }
            System.out.println("");
            System.out.println("* CLASSPATH:" + classpathStringBuffer.toString());
            System.out.println("******************************************");

        }

        return processBuilder;
    }

    /**
     * Stops the Soap-UI Server.
     * 
     * @throws SoapUiException
     *             If the SOAP-UI-Server couldn't be stopped successfully.
     */
    public void stopSoapUiServer() {

        Exception exception = null;
        try {
            this.process.destroy();
        } catch (final Exception e) {
            exception = e;
        }
        this.outputWriter.stopOutputWriting();

        if (exception != null) {
            throw new SoapUiException("An exception occured during end of soapUI Server.", exception);
        }
    }

    /**
     * Stopps and Starts the Soap-UI Server again and returns when the Server is up-and-running.
     * 
     * @throws SoapUiException
     *             If the SOAP-UI-Server couldn't be started or a timeout occured.
     */
    public void restartSoapUiServer() {
        try {
            stopSoapUiServer();
        } catch (final Exception e) {
            // ignore
        }
        startSoapUiServer();

    }

    public boolean isPrintClasspathSummary() {
        return printClasspathSummary;
    }

    public void setPrintClasspathSummary(final boolean printClasspathSummary) {
        this.printClasspathSummary = printClasspathSummary;
    }

    public boolean isPrintJvmSummary() {
        return printJvmSummary;
    }

    public void setPrintJvmSummary(final boolean printJvmSummary) {
        this.printJvmSummary = printJvmSummary;
    }

    public String getProjectFileLocation() {
        return projectFileLocation;
    }

    public void setProjectFileLocation(final String projectFileLocation) {
        this.projectFileLocation = projectFileLocation;
    }

    public String getBaseSoapUiLocation() {
        return baseSoapUiLocation;
    }

    public void setBaseSoapUiLocation(final String baseSoapUiLocation) {
        this.baseSoapUiLocation = baseSoapUiLocation;
    }

    public String getLibrariesLocation() {
        return librariesLocation;
    }

    public void setLibrariesLocation(final String librariesLocation) {
        this.librariesLocation = librariesLocation;
    }

    public String getExtensionsLocation() {
        return extensionsLocation;
    }

    public void setExtensionsLocation(final String extensionsLocation) {
        this.extensionsLocation = extensionsLocation;
    }

    public String getSoapuiSettingsLocation() {
        return soapuiSettingsLocation;
    }

    public void setSoapuiSettingsLocation(final String soapuiSettingsLocation) {
        this.soapuiSettingsLocation = soapuiSettingsLocation;
    }

    public long getWaitTimoutMilliseconds() {
        return waitTimoutMilliseconds;
    }

    public void setWaitTimoutMilliseconds(final long waitTimoutMilliseconds) {
        this.waitTimoutMilliseconds = waitTimoutMilliseconds;
    }

    public boolean isPrintStandardOutput() {
        return printStandardOutput;
    }

    public void setPrintStandardOutput(final boolean printStandardOutput) {
        this.printStandardOutput = printStandardOutput;
    }

    public boolean isPrintStandardOutputToSysout() {
        return printStandardOutputToSysout;
    }

    public void setPrintStandardOutputToSysout(final boolean printStandardOutputToSysout) {
        this.printStandardOutputToSysout = printStandardOutputToSysout;
    }

    public String getSoapuiListeningPort() {
        return soapuiListeningPort;
    }

    public void setSoapuiListeningPort(final String soapuiListeningPort) {
        this.soapuiListeningPort = soapuiListeningPort;
    }

}
