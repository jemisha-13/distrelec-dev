package com.namics.hybris.toolbox.impex.media;

import org.springframework.util.StringUtils;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.impex.jalo.media.MediaDataHandler;
import de.hybris.platform.impex.jalo.translators.SpecialValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.media.Media;

/**
 * Translator to read/write media files on file system level.
 * 
 * $Revision$ $LastChangedBy$ $LastChangedDate$
 */
public abstract class AbstractMediaDataTranslator implements SpecialValueTranslator {

    /**
     * The instance of mediaHandler to use.
     */
    protected MediaDataHandler mediaHandler = null;

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.impex.jalo.translators.SpecialValueTranslator#isEmpty(java.lang.String)
     */
    public boolean isEmpty(final String s) {
        return !StringUtils.hasText(s);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.impex.jalo.translators.SpecialValueTranslator#performExport(de.hybris.platform.jalo.Item)
     */
    public String performExport(final Item item) throws ImpExException {
        if (item instanceof Media) {
            final Media media = (Media) item;
            final String returnValue = mediaHandler.exportData(media);
            return returnValue;

        } else {
            throw new ImpExException("Item '" + item + "' is not a media. Couldn't export.");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.impex.jalo.translators.SpecialValueTranslator#performImport(java.lang.String, de.hybris.platform.jalo.Item)
     */
    public void performImport(final String value, final Item item) throws ImpExException {
        if (item instanceof Media) {
            final Media media = (Media) item;
            mediaHandler.importData(media, value);
        } else {
            throw new ImpExException("Item '" + item + "' is not a media. Couldn't import.");
        }
    }

    public void validate(final String s) throws HeaderValidationException {
        // do nothing
    }

}
