/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.catplus.impex.translator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.jalo.DistAssetType;
import com.namics.distrelec.b2b.core.jalo.DistDocumentType;
import com.namics.distrelec.b2b.core.jalo.DistDownloadMedia;
import com.namics.distrelec.b2b.core.model.DistAssetTypeModel;
import com.namics.distrelec.b2b.core.model.DistDocumentTypeModel;
import com.namics.distrelec.b2b.core.model.DistDownloadMediaModel;
import com.namics.hybris.toolbox.media.MediaUtil;
import com.namics.hybris.toolbox.spring.SpringUtil;

import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.impex.jalo.header.SpecialColumnDescriptor;
import de.hybris.platform.impex.jalo.media.MediaDataHandler;
import de.hybris.platform.impex.jalo.media.MediaDataTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
import de.hybris.platform.jalo.media.Media;
import de.hybris.platform.jalo.media.MediaContainer;
import de.hybris.platform.jalo.media.MediaFormat;
import de.hybris.platform.jalo.media.MediaManager;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.JaloAbstractTypeException;
import de.hybris.platform.jalo.type.JaloGenericCreationException;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * This translator is useful to import medias for catalogplus.<br>
 * The actual specification is to receive in the csv file maximum three media. Each media consists of a media-type column and media-name
 * column.<br>
 * All of these media are optional in the csv file and also the fields can contain images or pdf.<br>
 * <br>
 * 
 * The aim of this translator is to import all of the media within the product import impex line. There could be two types of media<br>
 * <ul>
 * <li>Media: the picture in jpeg format that will be used as primaryImage<br>
 * For each media we need to create the related MediaContainer</li>
 * <li>DistDownloadMedia: the pdf document that will be used as downloadMedias. also the documenttype is updated</li>
 * </ul>
 * <br>
 * For each media we need to create the related MediaFormat<br>
 * 
 * The format of the csv line should be this one:<br>
 * 
 * [mediaURLResource](_)[mimetype1](,)[filename1](|)[mimetype2](,)[filename2](|)[mimetype3](,)[filename3] <br>
 * 
 * 
 * 
 * The filepath must be specified according to the standard Media translator.<br>
 * The filepath could be specified without the filename. This is one case of empty element when also the mimetype is empty.<br>
 * 
 * @author daebersanif, Distrelec AG
 * @since Distrelec Extensions 1.0
 * 
 */
public class DistCatPlusMediaDataTranslator extends MediaDataTranslator {

    private static final Logger log = Logger.getLogger(DistCatPlusMediaDataTranslator.class);
    private SpecialColumnDescriptor myCd = null;

    // mime types
    private static final String MIME_PDF = "application/pdf";
    private static final String MIME_GIF = "image/gif";
    private static final String MIME_PNG = "image/png";
    private static final String MIME_JPEG = "image/jpeg";
    private static final String MIME_TIFF = "image/tiff";
    private static final String MIME_URL = "url";

    // configuration constants
    private static final String CATALOG_PLUS_MEDIA_IMAGE_FORMAT = "import.catplus.media.image.format";
    private static final String CATALOG_PLUS_MEDIA_DOCUMENT_FORMAT = "import.catplus.media.document.format";
    private static final String CATALOG_PLUS_MEDIA_DOCUMENT_DOCUMENTTYPE = "import.catplus.media.document.documenttype";
    private static final String CATALOG_PLUS_MEDIA_DOCUMENT_ASSETTYPE = "import.catplus.media.document.assettype";

    private static final String CATALOG_PLUS_MEDIA_IMPEX_MAIN_SEPARATOR = "import.catplus.media.impex.mainseparator";
    private static final String CATALOG_PLUS_MEDIA_IMPEX_FILE_SEPARATOR = "import.catplus.media.impex.fileseparator";
    private static final String CATALOG_PLUS_MEDIA_IMPEX_SUB_SEPARATOR = "import.catplus.media.impex.subseparator";

    private final ConfigurationService configurationService = SpringUtil.getBean("configurationService", ConfigurationService.class);
    private final Configuration configuration = configurationService.getConfiguration();

    private String mainMediaSeparator = null;
    private String fileMediaSeparator = null;
    private String subMediaSeparator = null;

    @Override
    public void init(final SpecialColumnDescriptor cd) throws HeaderValidationException {
        super.init(cd);
        this.myCd = cd;

        // setup separators used in impex expression
        this.mainMediaSeparator = configuration.getString(CATALOG_PLUS_MEDIA_IMPEX_MAIN_SEPARATOR, "\\(\\_\\)");
        this.fileMediaSeparator = configuration.getString(CATALOG_PLUS_MEDIA_IMPEX_FILE_SEPARATOR, "\\(\\|\\)");
        this.subMediaSeparator = configuration.getString(CATALOG_PLUS_MEDIA_IMPEX_SUB_SEPARATOR, "\\(\\,\\)");
    }

    @Override
    public void validate(final String expr) throws HeaderValidationException {
        {
            if (isHeaderComposedTypeOf(Product.class) || isHeaderComposedTypeOf(Media.class)) {
                return;
            }
        }
        throw new HeaderValidationException("illegal composed type " + this.myCd.getHeader().getConfiguredComposedType().getCode()
                + " for DistCatPlusMediaDataTranslator - " + "must be Media (or any of its subtypes) or Product (or any of its subtypes)", 0);
    }

    @Override
    public void performImport(final String cellValue, final Item processedItem) throws ImpExException {
        final MediaDataHandler handler = getHandler();
        if ((handler != null) && (cellValue != null) && (cellValue.length() > 0) && (processedItem != null) && (processedItem.isAlive())
                && !cellValue.startsWith("<ignore>")) {

            // this is the case of catalog plus import.
            if (isHeaderComposedTypeOf(Product.class)) {
                final Product product = (Product) processedItem;
                final String catPlusSupplierAID = (String) getAttributeFromItem(product, ProductModel.CATPLUSSUPPLIERAID);

                final CatalogVersion catalogVersion = CatalogManager.getInstance().getCatalogVersion(product);

                // get the values from the csv colummn

                // array that contains the main mediaurl and the list of files(mime and filename)
                final String[] configuredMainValues = getConfiguredValues(cellValue);
                // array that contains the list of files (mime and filename)
                final String[] configuredMedias = getCellValues(configuredMainValues[1]);

                // get the url root that will be used for all the media configured
                final String configuredRootPath = configuredMainValues[0].endsWith("/") ? configuredMainValues[0] : configuredMainValues[0] + "/";

                for (int i = 0; i < configuredMedias.length; i++) {

                    // parse the columnValue and retrive the media definitions (mime and filenam)
                    final String[] configuredMediaParts = configuredMedias[i].split(subMediaSeparator);
                    if (configuredMediaParts.length == 0) {
                        // media is empty, skip it
                        continue;
                    }

                    // check if the configuration is valid.
                    if (configuredMediaParts.length != 2) {
                        log.error("Can not process media information. The format of configured string is wrong: \"" + cellValue
                                + "\". The format should be: mime/type1,media1.jpg[|mime/type2,media2.jpg|mime/type3,media3.jpg]");
                        continue;
                        // throw new ImpExException("Cannot process the media information: " + cellValue + ".");
                    }

                    // Media parameters defined
                    final String configuredMediaCode = getConfiguredMediaCode(configuredMediaParts);
                    if (configuredMediaCode == null || configuredMediaCode.isEmpty()) {
                        log.error("The format of the media definition in the impex is not correct: " + StringUtils.join(configuredMediaParts)
                                + ". Skipping the line.");
                        continue;
                        // throw new ImpExException("The format of the media definition in the impex is not correct: " +
                        // StringUtils.join(configuredMediaParts));
                    }

                    final String configuredMimeType = getConfiguredMimeType(configuredMediaParts);
                    final String configuredFileName = getConfiguredFileName(configuredMediaParts);
                    final String configuredFullPath = configuredRootPath + configuredFileName;

                    // manage media images.
                    if (isImage(configuredMimeType)) {

                        // 1 - MediaContainer preparation
                        final MediaContainer mediaContainer = MediaUtil.createOrGetMediaContainer(configuredMediaCode, catalogVersion);

                        // 2 - MediaFormat preparation
                        final MediaFormat mediaFormat = MediaUtil.createOrGetMediaFormat(configuration.getString(CATALOG_PLUS_MEDIA_IMAGE_FORMAT));

                        // 3 - Media preparation
                        final Media media = createOrGetMedia(configuredMediaCode, configuredMimeType, catalogVersion);
                        addAttributeToItem(media, MediaModel.MEDIAFORMAT, mediaFormat);
                        // addAttributeToItem(media, MediaModel.MEDIACONTAINER, mediaContainer);

                        // 4 - Associate the media to the mediacontainer
                        // get actual mediacontainer medias immutable
                        final Collection<Media> medias = (Collection<Media>) getAttributeFromItem(mediaContainer, MediaContainerModel.MEDIAS);
                        // create new medias set (mutable)
                        final Set<Media> newMedias = new HashSet<Media>();
                        newMedias.addAll(medias);
                        newMedias.add(media);
                        addAttributeToItem(mediaContainer, MediaContainerModel.MEDIAS, newMedias);
                        // 5 - Associate the mediacontainer to the product
                        addAttributeToItem(product, ProductModel.PRIMARYIMAGE, mediaContainer);

                        // 6 go ahead with standard content import
                        handler.importData(media, configuredFullPath);

                    }
                    // manage media documents
                    else if (isFileExtensionOf("pdf", configuredFileName)) {
                        // 1 - MediaFormat preparation
                        final MediaFormat mediaFormat = MediaUtil.createOrGetMediaFormat(configuration.getString(CATALOG_PLUS_MEDIA_DOCUMENT_FORMAT));
                        final DistDocumentType documentType = searchDocumentTypeByCode(configuration.getString(CATALOG_PLUS_MEDIA_DOCUMENT_DOCUMENTTYPE));
                        final DistAssetType assetType = searchAssetTypeByCode(configuration.getString(CATALOG_PLUS_MEDIA_DOCUMENT_ASSETTYPE));
                        final List<Language> languages = new ArrayList<Language>();
                        languages.add(C2LManager.getInstance().getLanguageByIsoCode("DE"));
                        languages.add(C2LManager.getInstance().getLanguageByIsoCode("IT"));
                        languages.add(C2LManager.getInstance().getLanguageByIsoCode("EN"));

                        // 3 - DistDownloadMedia preparation
                        final DistDownloadMedia downloadMedia = createOrGetDistDownloadMedia(configuredMediaCode, configuredMimeType, catalogVersion);
                        addAttributeToItem(downloadMedia, DistDownloadMedia.MEDIAFORMAT, mediaFormat);
                        addAttributeToItem(downloadMedia, DistDownloadMedia.DOCUMENTTYPE, documentType);
                        addAttributeToItem(downloadMedia, DistDownloadMedia.ASSETTYPE, assetType);
                        addAttributeToItem(downloadMedia, DistDownloadMedia.LANGUAGES, languages);

                        addAttributeToItem(downloadMedia, DistDownloadMedia.REALFILENAME, configuredFileName);
                        // 3 - Associate the download media to existing one
                        try {
                            List<DistDownloadMedia> downloadMedias = (List<DistDownloadMedia>) product.getAttribute(ProductModel.DOWNLOADMEDIAS);
                            if (downloadMedias == null || downloadMedias.isEmpty()) {
                                downloadMedias = ListUtils.EMPTY_LIST;
                            }
                            // create the mutable list
                            final java.util.Set<DistDownloadMedia> newList = new java.util.HashSet<DistDownloadMedia>();
                            newList.addAll(downloadMedias);
                            newList.add(downloadMedia);

                            addAttributeToItem(product, ProductModel.DOWNLOADMEDIAS, newList);
                        } catch (final JaloInvalidParameterException e) {
                            log.debug("Error during DistDownloadMedia creation" + e.getMessage());
                            throw new ImpExException(e);
                        } catch (final JaloSecurityException e) {
                            log.debug("Error during DistDownloadMedia creation" + e.getMessage());
                            throw new ImpExException(e);
                        }

                        if (isUrl(configuredMimeType)) {
                            // log in specific log file the media resource that needs to be downloaded later.
                            log.info("Url Media needs to be downloaded. CatalogPlusSupplierAID: " + catPlusSupplierAID + ", Product code: " + product.getCode()
                                    + ", Media code: " + configuredMediaCode + ", Download url: " + configuredMediaParts[1]);
                        }

                        // 5 go ahead with standard content import
                        handler.importData(downloadMedia, configuredFullPath);

                    } else {
                        log.debug("Media not recognized. Configured mimetype:" + configuredMimeType + ". Configured filename: " + configuredFileName
                                + ". Full line: " + StringUtils.join(configuredMediaParts, " "));
                    }
                }
            } else if (isHeaderComposedTypeOf(Media.class)) {
                // this is the case of the normal media. Then import the media as is
                super.performImport(cellValue, processedItem);
            }
        } else {
            if (handler == null) {
                throw new ImpExException("No MediaDataHandler is set for special column with qualifier " + this.myCd.getQualifier() + "!");
            }

            if (!(log.isDebugEnabled())) {
                return;
            }
            log.debug("Can not import data to media cause processed item is " + (processedItem == null ? "null" : " not alive "));
        }
    }

    private boolean isFileExtensionOf(final String extension, final String fileNameToCheck) {
        if (extension == null || fileNameToCheck == null || extension.isEmpty() || fileNameToCheck.isEmpty()) {
            return false;
        }
        return extension.equals(FilenameUtils.getExtension(fileNameToCheck));
    }

    /*
     * split the string with & separator the first element should be the media resource that will be used as url root part for all the media
     */
    private String[] getConfiguredValues(final String cellValue) throws ImpExException {
        final String[] parts = cellValue.split(mainMediaSeparator);
        if (parts == null || parts.length != 2 || parts[0].isEmpty()) {
            log.debug("Missing mediaresource in csv column value: " + cellValue);
        }
        return parts;
    }

    /**
     * Parse the value from the impex cell value. <br>
     * Empty elements are removed.<br>
     * An element is empty when:<br>
     * <ul>
     * <li>is null</li>
     * <li>is empty</li>
     * <li>contains only a comma (the sub separator between mime and filename)</li>
     * <li>it contains only the resource path. This means that no mime nor filename have been specified.</li>
     * 
     */
    private String[] getCellValues(final String cellValue) {

        final String[] parts = cellValue.split(fileMediaSeparator);
        final List<String> resultList = new ArrayList<String>();
        for (final String part : parts) {
            // ignore empty parameters, but consider that the url could be present as default, so ignore it when last char is a slash
            // TODO: check if is enough or add additional logic to identify empty elements.
            if (part != null && !part.trim().isEmpty() && !part.trim().equals(",") && !(part.trim().startsWith(",") && part.trim().endsWith("/"))) {
                resultList.add(part);
            }
        }
        return resultList.toArray(new String[resultList.size()]);
    }

    private DistDownloadMedia createOrGetDistDownloadMedia(final String code, final String mimeType, final CatalogVersion catalogVersion) throws ImpExException {
        final Collection<DistDownloadMedia> mediaCollection = searchDistDownloadMediaByCode(code, catalogVersion);
        DistDownloadMedia media = null;
        if (mediaCollection == null || mediaCollection.isEmpty()) {

            media = this.createDistDownloadMedia(code);
            addAttributeToItem(media, MediaModel.CATALOGVERSION, catalogVersion);
        } else {
            media = mediaCollection.iterator().next();
        }
        // TODO: CHECK IF WE CAN UPDATE THE MIMETYPE IF IT CHANGES FOR AN EXISTING MEDIA
        addAttributeToItem(media, MediaModel.MIME, mimeType);
        return media;

    }

    private Media createOrGetMedia(final String code, final String mimeType, final CatalogVersion catalogVersion) throws ImpExException {
        final Collection<Media> mediaCollection = searchMediaByCode(code, catalogVersion);
        Media media = null;
        if (mediaCollection == null || mediaCollection.isEmpty()) {

            media = MediaManager.getInstance().createMedia(code);
            addAttributeToItem(media, MediaModel.CATALOGVERSION, catalogVersion);
        } else {
            media = mediaCollection.iterator().next();
        }
        // TODO: CHECK IF WE CAN UPDATE THE MIMETYPE IF IT CHANGES FOR AN EXISTING MEDIA
        addAttributeToItem(media, MediaModel.MIME, mimeType);
        return media;
    }

    private void addAttributeToItem(final Item item, final String attributeName, final Object value) throws ImpExException {
        try {
            item.setAttribute(attributeName, value);

        } catch (final JaloInvalidParameterException e) {
            log.debug("Error during " + item.getClass() + " creation for PK: " + item.getPK() + ". It was not possible to assign " + attributeName + ": "
                    + value + ". Detailed exception: " + e.getMessage());
            throw new ImpExException(e);
        } catch (final JaloSecurityException e) {
            log.debug("Error during " + item.getClass() + " creation for code: " + item.getPK() + ". It was not possible to assign " + attributeName + ": "
                    + value + ". Detailed exception: " + e.getMessage());
            throw new ImpExException(e);
        } catch (final JaloBusinessException e) {
            log.debug("Error during " + item.getClass() + " creation for code: " + item.getPK() + ". It was not possible to assign " + attributeName + ": "
                    + value + ". Detailed exception: " + e.getMessage());
            throw new ImpExException(e);
        }
    }

    private Object getAttributeFromItem(final Item item, final String attributeName) throws ImpExException {
        try {
            return item.getAttribute(attributeName);
        } catch (final JaloInvalidParameterException e1) {
            throw new ImpExException("Attribute: " + attributeName + " not found for item: " + item.getPK());
        } catch (final JaloSecurityException e1) {
            throw new ImpExException("There are problems when reading attribute: " + attributeName + " for item: " + item.getPK());
        }
    }

    private Collection<Media> searchMediaByCode(final String code, final CatalogVersion catalogVersion) {

        final Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put(MediaModel.CODE, code);
        parameters.put(MediaModel.CATALOGVERSION, catalogVersion);

        final String query = "SELECT {" + Item.PK + "} FROM {" + MediaModel._TYPECODE + "} " + "WHERE {" + MediaModel.CODE + "} = ?" + MediaModel.CODE
                + " AND " + "{" + MediaModel.CATALOGVERSION + "} = ?" + MediaModel.CATALOGVERSION + " ";

        return FlexibleSearch.getInstance().search(query, parameters, Media.class).getResult();

    }

    private Collection<DistDownloadMedia> searchDistDownloadMediaByCode(final String code, final CatalogVersion catalogVersion) {

        final Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put(DistDownloadMediaModel.CODE, code);
        parameters.put(DistDownloadMediaModel.CATALOGVERSION, catalogVersion);

        final String query = "SELECT {" + Item.PK + "} FROM {" + DistDownloadMediaModel._TYPECODE + "} " + "WHERE {" + DistDownloadMediaModel.CODE + "} = ?"
                + DistDownloadMediaModel.CODE + " AND " + "{" + DistDownloadMediaModel.CATALOGVERSION + "} = ?" + DistDownloadMediaModel.CATALOGVERSION + " ";

        return FlexibleSearch.getInstance().search(query, parameters, DistDownloadMedia.class).getResult();

    }

    private DistDocumentType searchDocumentTypeByCode(final String code) throws ImpExException {
        final Map<String, Object> parameters = new HashMap<String, Object>(1);
        parameters.put(DistDocumentType.CODE, code);

        final String query = "SELECT {" + Item.PK + "} FROM {" + DistDocumentTypeModel._TYPECODE + "} " + "WHERE {" + DistDocumentTypeModel.CODE + "} = ?"
                + DistDocumentTypeModel.CODE + " ";

        final List<DistDocumentType> resultList = FlexibleSearch.getInstance().search(query, parameters, DistDocumentType.class).getResult();
        if (resultList == null || resultList.isEmpty()) {
            throw new ImpExException("DistDocumentType code: " + code + " not found in the system!");
        }
        return resultList.iterator().next();

    }

    private DistAssetType searchAssetTypeByCode(final String code) throws ImpExException {
        final Map<String, Object> parameters = new HashMap<String, Object>(1);
        parameters.put(DistDocumentType.CODE, code);

        final String query = "SELECT {" + Item.PK + "} FROM {" + DistAssetTypeModel._TYPECODE + "} " + "WHERE {" + DistAssetTypeModel.CODE + "} = ?"
                + DistAssetTypeModel.CODE + " ";

        final List<DistAssetType> resultList = FlexibleSearch.getInstance().search(query, parameters, DistAssetType.class).getResult();
        if (resultList == null || resultList.isEmpty()) {
            throw new ImpExException("DistAssetType code: " + code + " not found in the system!");
        }
        return resultList.iterator().next();

    }

    private String getConfiguredMediaCode(final String[] configuredMediaParts) throws ImpExException {
        try {
            return FilenameUtils.getBaseName(configuredMediaParts[1]);
        } catch (final Exception ex) {
            log.debug("The format of the media definition in the impex is not correct: " + StringUtils.join(configuredMediaParts)
                    + ". Full configuration line: " + StringUtils.join(configuredMediaParts) + ". See stackTrace for details.");
            throw new ImpExException("The format of the media definition in the impex is not correct: " + configuredMediaParts[1]);
        }
    }

    private String getConfiguredMimeType(final String[] configuredMediaParts) throws ImpExException {
        try {
            final String mime = configuredMediaParts[0];
            if (mime == null || mime.trim().isEmpty()) {
                log.error("The format of the media definition in the impex is not correct: " + StringUtils.join(configuredMediaParts));
            }
            return mime;
        } catch (final Exception ex) {
            log.debug("The format of the media definition in the impex is not correct: " + StringUtils.join(configuredMediaParts)
                    + ". Full configuration line: " + StringUtils.join(configuredMediaParts) + ". See stackTrace for details.");
            throw new ImpExException("The format of the media definition in the impex is not correct: " + StringUtils.join(configuredMediaParts)
                    + ". Detailed Exception: " + ex.getMessage());
        }
    }

    private String getConfiguredFileName(final String[] configuredMediaParts) throws ImpExException {
        try {
            String filename = "";
            if (isUrl(configuredMediaParts[0])) {
                filename = URIUtil.getName(configuredMediaParts[1]);
            } else {
                filename = FilenameUtils.getName(configuredMediaParts[1]);
            }
            if (filename == null || filename.trim().isEmpty()) {
                throw new ImpExException("The format of the media definition in the impex is not correct: " + configuredMediaParts[1]);
            }
            return filename;
        } catch (final Exception ex) {
            log.debug("The format of the media definition in the impex is not correct: " + configuredMediaParts[1] + ". Full configuration line: "
                    + StringUtils.join(configuredMediaParts) + ". See stackTrace for details.");
            throw new ImpExException("The format of the media definition in the impex is not correct: " + StringUtils.join(configuredMediaParts));
        }

    }

    /*
     * Test if the configured type defined in the impex line is of specific ComposedType
     */
    private boolean isHeaderComposedTypeOf(final Class clazz) {

        return (TypeManager.getInstance().getComposedType(clazz).isAssignableFrom(this.myCd.getHeader().getConfiguredComposedType()));

    }

    private boolean isImage(final String mimetype) {
        return (mimetype.equals(MIME_GIF) || mimetype.equals(MIME_JPEG) || mimetype.equals(MIME_PNG) || mimetype.equals(MIME_TIFF)) ? true : false;
    }

    private boolean isDocument(final String mimetype) {
        return (mimetype.equals(MIME_PDF));
    }

    private boolean isUrl(final String mimetype) {
        return (mimetype.equals(MIME_URL)) ? true : false;
    }

    /*
     * Create Jalo item for DistDownloadMedia
     */
    public DistDownloadMedia createDistDownloadMedia(final String code) {
        try {
            final Item.ItemAttributeMap params = new Item.ItemAttributeMap();
            params.put(Item.PK, null);
            params.put(DistDownloadMedia.CODE, code);
            params.put(DistDownloadMedia.FOLDER, MediaManager.getInstance().getRootMediaFolder());
            ComposedType adjustedType;

            adjustedType = TypeManager.getInstance().getComposedType(DistDownloadMedia.class);

            return ((DistDownloadMedia) adjustedType.newInstance(MediaManager.getInstance().getSession().getSessionContext(), params));
        } catch (final JaloGenericCreationException e) {
            Throwable cause = e.getCause();
            if (cause == null) {
                cause = e;
            }
            if (cause instanceof RuntimeException) {
                throw ((RuntimeException) cause);
            }

            throw new JaloSystemException(cause);

        } catch (final JaloAbstractTypeException e) {
            throw new JaloSystemException(e);
        } catch (final JaloItemNotFoundException e) {
            throw new JaloSystemException(e);
        }
    }

}
