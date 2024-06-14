package com.namics.hybris.toolbox;

import java.text.DateFormat;
import java.util.Date;

public class ExceptionUtils {

    /**
     * Zeilenumbruch.
     */
    protected static final String defaultLineDelimiter = "\n";

    /**
     * Generiert ein Text mit allen Informationen über eine Exception.
     * 
     * @param throwable
     *            Der Fehler, welcher geworfen wurde.
     * @param lineDelimiter
     *            Zeilenumbruch.
     * @param ident
     *            Verschachtelungstiefe der Fehlermeldung (cause)
     * @return Ein Text mit allen Informationen über die Fehlermeldung.
     */
    protected static String generateExceptionText(final Throwable throwable, final String lineDelimiter, final int ident) {
        final StringBuffer loggerText = new StringBuffer(120);
        final String identString = StringOperation.repeatString(" ", ident + 1);

        try {

            if (ident == 0) {
                final Date date = new Date();
                final DateFormat dateFormat = DateFormat.getDateTimeInstance();

                loggerText.append("--- Start Error Log ---" + lineDelimiter);
                loggerText.append("Date:" + dateFormat.format(date) + lineDelimiter);
            }

            if (throwable.getMessage().equals(throwable.getLocalizedMessage())) {
                loggerText.append(identString + "Message: " + throwable.getMessage() + lineDelimiter);
            } else {
                loggerText.append(identString + "Message: " + throwable.getMessage() + " (" + throwable.getLocalizedMessage() + ")" + lineDelimiter);
            }
            // loggerText.append(identString + "Message: " + throwable.getMessage() +" (" + throwable.getLocalizedMessage() + ")" +
            // lineDelimiter);
            loggerText.append(identString + "Cause: " + throwable.getCause() + lineDelimiter);
            loggerText.append(identString + "Exception: " + throwable + lineDelimiter);
            // loggerText.append(identString + "Stack Trace: " + lineDelimiter);

            if (throwable.getCause() != null) {
                // Rekursiver Aufruf, um die verschachtelte Exception auszugeben.
                loggerText.append(generateExceptionText(throwable.getCause(), lineDelimiter, ident + 2));
            } else {
                final StackTraceElement[] stackTraceArray = throwable.getStackTrace();
                if (stackTraceArray != null) {
                    for (int i = 0; i < stackTraceArray.length; i++) {
                        final StackTraceElement stackTraceElement = stackTraceArray[i];
                        loggerText.append(identString + "  " + stackTraceElement.toString() + lineDelimiter);
                    }
                }
            }
        } catch (final RuntimeException e) {
            loggerText.append(identString + "(Exception-Daten konnte nicht vollständig ermittelt werden)" + lineDelimiter);
        }

        if (ident == 0) {
            loggerText.append("--- End Error Log ---" + lineDelimiter);
        }

        return loggerText.toString();
    }

    /**
     * Generiert ein Text mit allen Informationen über eine Exception.
     * 
     * @param throwable
     *            Der Fehler, welcher geworfen wurde.
     * @return Ein Text mit allen Informationen über die Fehlermeldung.
     */
    public static String generateExceptionText(final Throwable throwable) {
        return generateExceptionText(throwable, defaultLineDelimiter);
    }

    /**
     * Generiert ein Text mit allen Informationen über eine Exception.
     * 
     * @param throwable
     *            Der Fehler, welcher geworfen wurde.
     * @param lineDelimiter
     *            Zeilenumbruch.
     * @return Ein Text mit allen Informationen über die Fehlermeldung.
     */
    public static String generateExceptionText(final Throwable throwable, final String lineDelimiter) {
        return generateExceptionText(throwable, lineDelimiter, 0);
    }

    /**
     * Gibt Standardwerte der JVM als String zurück.
     * 
     * @see "http://java.sun.com/j2se/1.3/docs/api/java/lang/System.html#getProperties()"
     */
    public static String generateSystemConfigurationText() {
        final StringBuffer resultText = new StringBuffer(1800);

        resultText.append(defaultLineDelimiter + "Java Runtime Environment version (java.version):" + System.getProperty("java.version"));
        resultText.append("Java Runtime Environment version (java.version):" + System.getProperty("java.version") + defaultLineDelimiter);
        resultText.append("Java Runtime Environment vendor (java.vendor):" + System.getProperty("java.vendor") + defaultLineDelimiter);
        resultText.append("Java vendor URL (java.vendor.url):" + System.getProperty("java.vendor.url") + defaultLineDelimiter);
        resultText.append("Java installation directory (java.home):" + System.getProperty("java.home") + defaultLineDelimiter);
        resultText.append("Java Virtual Machine specification version (java.vm.specification.version):" + System.getProperty("java.vm.specification.version")
                + defaultLineDelimiter);
        resultText.append("Java Virtual Machine specification vendor (java.vm.specification.vendor):" + System.getProperty("java.vm.specification.vendor")
                + defaultLineDelimiter);
        resultText.append("Java Virtual Machine specification name (java.vm.specification.name):" + System.getProperty("java.vm.specification.name")
                + defaultLineDelimiter);
        resultText.append("Java Virtual Machine implementation version (java.vm.version):" + System.getProperty("java.vm.version") + defaultLineDelimiter);
        resultText.append("Java Virtual Machine implementation vendor (java.vm.vendor):" + System.getProperty("java.vm.vendor") + defaultLineDelimiter);
        resultText.append("Java Virtual Machine implementation name (java.vm.name):" + System.getProperty("java.vm.name") + defaultLineDelimiter);
        resultText.append("Java Runtime Environment specification version (java.specification.version):" + System.getProperty("java.specification.version")
                + defaultLineDelimiter);
        resultText.append("Java Runtime Environment specification vendor (java.specification.vendor):" + System.getProperty("java.specification.vendor")
                + defaultLineDelimiter);
        resultText.append("Java Runtime Environment specification name (java.specification.name):" + System.getProperty("java.specification.name")
                + defaultLineDelimiter);
        resultText.append("Java class format version number (java.class.version):" + System.getProperty("java.class.version") + defaultLineDelimiter);
        resultText.append("Java class path (java.class.path):" + System.getProperty("java.class.path") + defaultLineDelimiter);
        resultText.append("Java library path (java.library.path) " + System.getProperty("java.library.path") + defaultLineDelimiter);
        resultText.append("Path of extension directory or directories (java.ext.dirs):" + System.getProperty("java.ext.dirs") + defaultLineDelimiter);
        resultText.append("Operating system name (os.name):" + System.getProperty("os.name") + defaultLineDelimiter);
        resultText.append("Operating system architecture (os.arch):" + System.getProperty("os.arch") + defaultLineDelimiter);
        resultText.append("Operating system version (os.version):" + System.getProperty("os.version") + defaultLineDelimiter);
        resultText.append("File separator (\"/\" on UNIX) (file.separator):" + System.getProperty("file.separator") + defaultLineDelimiter);
        resultText.append("Path separator (\":\" on UNIX) (path.separator):" + System.getProperty("path.separator") + defaultLineDelimiter);
        resultText.append("Line separator (\"\\n\" on UNIX) (line.separator):" + System.getProperty("line.separator") + defaultLineDelimiter);
        resultText.append("User's account name (user.name):" + System.getProperty("user.name") + defaultLineDelimiter);
        resultText.append("User's home directory (user.home):" + System.getProperty("user.home") + defaultLineDelimiter);
        resultText.append("User's current working directory (user.dir):" + System.getProperty("user.dir") + defaultLineDelimiter);
        return resultText.toString();
    }

}
