package com.namics.hybris.toolbox.impex.media;

import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.impex.jalo.header.SpecialColumnDescriptor;
import de.hybris.platform.impex.jalo.media.MediaDataHandler;

/**
 * Get the <code>MediaDataHandler</code> from the hybris application context.
 * 
 * <pre>
 * Registry.getApplicationContext().getBean(&quot;mediaDataHandler&quot;);
 * </pre>
 * 
 * $Revision$ $LastChangedBy$ $LastChangedDate$
 */
public class SpringbeanMediaDataTranslator extends AbstractMediaDataTranslator {

    /*
     * (non-Javadoc)
     * 
     * @seede.hybris.platform.impex.jalo.translators.SpecialValueTranslator#init(de.hybris.platform.impex.jalo.header.
     * SpecialColumnDescriptor)
     */
    public void init(final SpecialColumnDescriptor specialcolumndescriptor) throws HeaderValidationException {

        mediaHandler = (MediaDataHandler) Registry.getApplicationContext().getBean("mediaDataHandler");

    }

}
