package com.namics.hybris.webservice.soapui;

import static org.junit.Assert.assertNotNull;

import de.hybris.platform.util.Config;

import java.io.File;
import java.net.URL;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class SoapUiProcessLoaderTest {

    private static final String EXTENSION_NAME = "namwebservice";
    private SoapUiProcessLoader soapUiServer;

    @After
    public void tearDown() {
        if (soapUiServer != null) {
            soapUiServer.stopSoapUiServer();
        }
    }

    @Test
    public void testSoapUiProcessLoader() throws Exception {
        // Root location
        final URL extensionRootLocationUrl = this.getClass().getResource("/namwebservice/test/example/Test-Webservices-v1-soapui-project.xml");
        final File rootLocationFile = new File(extensionRootLocationUrl.toURI());
        final String classpathRootLocation = rootLocationFile.getAbsolutePath();
        final String extensionRootLocation = classpathRootLocation.substring(0, classpathRootLocation.indexOf(EXTENSION_NAME)) + EXTENSION_NAME;
        System.out.println("Extension Root: " + extensionRootLocation);

        final String soapUiProjectLocation = extensionRootLocation + "/resources/namwebservice/test/example/Test-Webservices-v1-soapui-project.xml";
        final String soapUiBaseLocation = extensionRootLocation + "/soapui";
        final String soapUiListeningPort = Config.getString("webservices.soapui.port", "9008");

        final boolean printJvmSummary = true;
        final boolean printClasspathSummary = true;
        final boolean printStandardOutputToSysout = true;
        final int secondsToWait = 45;

        soapUiServer = new SoapUiProcessLoader(soapUiProjectLocation, soapUiBaseLocation, soapUiListeningPort);
        soapUiServer.setPrintJvmSummary(printJvmSummary);
        soapUiServer.setPrintClasspathSummary(printClasspathSummary);
        soapUiServer.setPrintStandardOutputToSysout(printStandardOutputToSysout);
        soapUiServer.setWaitTimoutMilliseconds(1000 * secondsToWait);

        assertNotNull("SoapUi Server was null", soapUiServer);

        soapUiServer.startSoapUiServer();
        System.out.println("SoapUI-Server gestartet.");
        soapUiServer.stopSoapUiServer();
        System.out.println("SoapUI-Server gestoppt.");
    }

}
