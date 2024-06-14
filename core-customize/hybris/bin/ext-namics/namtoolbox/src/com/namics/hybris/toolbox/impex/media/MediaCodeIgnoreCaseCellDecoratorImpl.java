package com.namics.hybris.toolbox.impex.media;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import de.hybris.platform.catalog.jalo.Catalog;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
import de.hybris.platform.jalo.media.Media;
import de.hybris.platform.util.CSVCellDecorator;

/**
 * Decorater for importing Medias by ignoring the case of the code attibute.
 * <p>
 * The decorater receives a cell Value formatted like '<code>code</code>', ' <code>code:catalogName:catalogVersionName</code>', '
 * <code>code:catalogName:catalogName:catalogVersionName</code>'. The Decorater searchs a Media with the code but by ignoring the code
 * cases, so the letters can be lower case or upper case.
 * </p>
 * <p>
 * If only the code is passed, the Media search is over all catalog versions. This can result in unexpected results.
 * </p>
 * <p>
 * The method returns a code formatted like the passed code (e.g. 'code:catalogName:catalogVersionName') but with the code in the correct
 * case sensitivity.
 * </p>
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
 * UPDATE Product;code[unique=true];logo($uidMedia)[cellDecorator=com.namics.hybris.toolbox.impex.media.MediaCodeIgnoreCaseCellDecoratorImpl]
 * # ;code;logo;thumbnails
 * ;10-101-190-106014;Head_sw.jpg:athleticum:athleticum:stage;
 * ;30-305-311-106367;k-tec_cmyk.jpg:athleticum:athleticum:stage;
 * </pre>
 * 
 * The keys in the impex script would be transformed to '<code>K-TEC_cmyk.jpg:athleticum:athleticum:stage</code>'.
 * </p>
 * 
 * @author jweiss, namics ag
 * @since Athleticum 1.0
 * 
 */
public class MediaCodeIgnoreCaseCellDecoratorImpl implements CSVCellDecorator {

    private static final Logger LOG = Logger.getLogger(MediaCodeIgnoreCaseCellDecoratorImpl.class.getName());

    /**
     * Delimiter for the cell calues
     */
    public final static String DELIMITER = ":";

    @SuppressWarnings("unchecked")
    @Override
    public String decorate(final int position, final Map srcLine) {
        final String cellValue = (String) srcLine.get(Integer.valueOf(position));

        String code;
        try {
            final String[] cellValues = StringUtils.tokenizeToStringArray(cellValue, DELIMITER, false, true);
            code = cellValues[0];
            final String catalogName = cellValues[1];
            final String catalogVersionName = (cellValues.length == 4) ? cellValues[3] : cellValues[2];

            String newCode;
            String newResult;

            if (cellValues.length > 1) {
                final Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("catalogversion", getCatalogVersion(catalogName, catalogVersionName));
                parameters.put("code", code);

                final String query = "SELECT {PK} FROM {Media}" + "WHERE LOWER({code}) = LOWER(?code) " + "AND {catalogversion} LIKE ?catalogversion ";

                final List<Media> pixelgrafiks = FlexibleSearch.getInstance().search(query, parameters, Media.class).getResult();

                newCode = pixelgrafiks.get(0).getCode();

                switch (cellValues.length) {
                case 3:
                    newResult = newCode + DELIMITER + catalogName + DELIMITER + catalogVersionName;
                    break;
                case 4:
                    newResult = newCode + DELIMITER + catalogName + DELIMITER + catalogName + DELIMITER + catalogVersionName;
                    break;
                default:
                    newResult = cellValue;
                }
            } else {
                final Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("code", code);

                final String query = "SELECT {PK} FROM {Media}" + "WHERE LOWER({code}) = LOWER(?code) ";

                final List<Media> medien = FlexibleSearch.getInstance().search(query, parameters, Media.class).getResult();

                newCode = medien.get(0).getCode();
                newResult = newCode;
            }
            LOG.info("Translated '" + cellValue + "' => '" + newResult + "'");

            return newResult;
        } catch (final Exception e) {
            LOG.info("No translation for '" + cellValue + "'.");
            return cellValue;
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
