package com.namics.hybris.toolbox.impex.media;

import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import de.hybris.platform.catalog.jalo.Catalog;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.util.CSVCellDecorator;

/**
 * <p>
 * Decorater for importing Medias by ignoring the case of the code attibute. For details about functionality, see the
 * {@link MediaCodeIgnoreCaseCollectionCellDecoratorImpl} class. This implementation supports collections (comma separated strings).
 * </p>
 * 
 * <p>
 * Example: <br>
 * An impex code like this can't be imported, when the code of the Media is <code>K-TEC_cmyk.jpg</code> because of the upper cases. This can
 * be transformed by the cell decorator.
 * 
 * <pre>
 * #
 * # Impex to link some Articles with a logo
 * #
 * $uidMedia=code,catalog(id),catalogVersion(catalog(id), version)
 * UPDATE Product;code[unique=true];thumbnails($uidMedia)[cellDecorator=com.namics.hybris.toolbox.impex.media.MediaCodeIgnoreCaseCollectionCellDecoratorImpl]
 * # ;code;logo;thumbnails
 * ;10-101-190-106014;Head_sw.jpg:athleticum:athleticum:stage,k-tec_cmyk.jpg:athleticum:athleticum:stage;
 * </pre>
 * 
 * The keys in the impex script would be transformed to '<code>K-TEC_cmyk.jpg:athleticum:athleticum:stage</code>'.
 * </p>
 * 
 * @author jweiss, namics ag
 * @see MediaCodeIgnoreCaseCellDecoratorImpl
 * @since Athleticum 1.0
 * 
 */
public class MediaCodeIgnoreCaseCollectionCellDecoratorImpl implements CSVCellDecorator {

    private static final Logger LOG = Logger.getLogger(MediaCodeIgnoreCaseCollectionCellDecoratorImpl.class.getName());

    /**
     * Delimiter for the cell calues
     */
    public final static String DELIMITER = ",";

    public String decorate(final int position, final Map<Integer, String> srcLine) {
        final MediaCodeIgnoreCaseCellDecoratorImpl singleDecorator = new MediaCodeIgnoreCaseCellDecoratorImpl();
        final String collectionCellValue = srcLine.get(Integer.valueOf(position));

        try {
            final String[] cellValues = StringUtils.tokenizeToStringArray(collectionCellValue, DELIMITER, false, true);
            final StringBuffer resultBuffer = new StringBuffer();
            final int key = 0;
            for (final String value : cellValues) {
                final String returnValue = singleDecorator.decorate(key, Collections.singletonMap(Integer.valueOf(key), value));
                resultBuffer.append(DELIMITER);
                resultBuffer.append(returnValue);
            }

            if (resultBuffer.length() > 0) {
                resultBuffer.delete(0, DELIMITER.length());
            }

            return resultBuffer.toString();
        } catch (final Exception e) {
            LOG.info("No collection translation for '" + collectionCellValue + "'.");
            return collectionCellValue;
        }
    }

    /**
     * @return The Catalog with name <code>catalogName</code>.
     */
    protected Catalog getCatalog(final String catalogName) {
        return CatalogManager.getInstance().getCatalog(catalogName);
    }

    /**
     * @return The CatalogVersion with catalog name <code>catalogName</code> and version <code>catalogVersionName</code>.
     */
    protected CatalogVersion getCatalogVersion(final String catalogName, final String catalogVersionName) {
        return getCatalog(catalogName).getCatalogVersion(catalogVersionName);
    }

}
