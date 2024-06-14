package com.namics.hybris.toolbox.impex.media;

import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.impex.jalo.header.SpecialColumnDescriptor;

/**
 * <p>
 * Translator to read/write media files from a specified directory.
 * </p>
 * 
 * <p>
 * To configure the directory, there are two ways available:
 * <ul>
 * <li>Set the two attributes <code>defaultImportDirectory</code> and <code>defaultExportDirectory</code> by a static setting, like...
 * 
 * <pre>
 * DirectoryMediaDataTranslator.defaultImportDirectory = "C:\\data\\import\\"
 * DirectoryMediaDataTranslator.defaultExportDirectory = "C:\\data\\export\\"
 * </pre>
 * 
 * </li>
 * <li>Inject the directory by a impex modifier:
 * 
 * <pre>
 * ...;@media[translator=com.namics.axi.axicore.impex.DirectoryMediaDataTranslator][importDir=C:/data/import/][exportDir=C:/data/export/]
 * </pre>
 * 
 * </li>
 * </ul>
 * </p>
 * 
 * <p>
 * When both attributes are set, the injected modifiers are overwriting the default configuration attributes.
 * </p>
 * 
 * 
 * $Revision$ $LastChangedBy$ $LastChangedDate$
 */
public class DirectoryMediaDataTranslator extends AbstractMediaDataTranslator {

    /**
     * Here, the directory to read media files can be set. Like
     * 
     * <pre>
     * &quot;C:\\data\\import\\&quot;
     * </pre>
     */
    public static String defaultImportDirectory = null;
    /**
     * Here, the directory to write media files can be set. Like
     * 
     * <pre>
     * &quot;C:\\data\\export\\&quot;
     * </pre>
     */
    public static String defaultExportDirectory = null;

    /*
     * (non-Javadoc)
     * 
     * @seede.hybris.platform.impex.jalo.translators.SpecialValueTranslator#init(de.hybris.platform.impex.jalo.header.
     * SpecialColumnDescriptor)
     */
    public void init(final SpecialColumnDescriptor specialcolumndescriptor) throws HeaderValidationException {
        String paramImportDirectory = null;
        String paramExportDirectory = null;

        // Are there parameters to reference the directories
        try {
            paramImportDirectory = specialcolumndescriptor.getDescriptorData().getModifier("importDir");
            paramExportDirectory = specialcolumndescriptor.getDescriptorData().getModifier("exportDir");
        } catch (final Exception e) {
            // we ignore, that's probably a null pointer exception
            // the staticMediaDataHandler is set to false
        }

        String importDirectory = defaultImportDirectory;
        String exportDirectory = defaultExportDirectory;

        if (paramImportDirectory != null) {
            importDirectory = paramImportDirectory;
        }

        if (paramExportDirectory != null) {
            exportDirectory = paramExportDirectory;
        }

        if (exportDirectory == null || importDirectory == null) {
            throw new HeaderValidationException("The import/export directory must be " + "injected eighter by the static default attributes or by the "
                    + "impex parameters importDir/exportDir.", -1);
        } else {
            final DirectoryMediaDataHandler handler = new DirectoryMediaDataHandler();
            handler.setImportDirectory(importDirectory);
            handler.setExportDirectory(exportDirectory);
            mediaHandler = handler;
        }
    }
}
