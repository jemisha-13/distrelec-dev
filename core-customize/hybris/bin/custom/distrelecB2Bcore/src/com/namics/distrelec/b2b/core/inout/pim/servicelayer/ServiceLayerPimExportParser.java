package com.namics.distrelec.b2b.core.inout.pim.servicelayer;

import com.namics.distrelec.b2b.core.inout.pim.PimExportParser;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.PimImportElementConverter;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.dto.PimProductReferenceDto;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler.AttributeAwareDelegateHandler;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler.NoopElementHandler;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler.PimImportElementHandler;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler.PimImportElementHandlerTypeEnum;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.util.ProductReferenceCreator;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaContainerService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.ElementHandler;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Parses a PIM-Export XML file and persists the resulting models directly.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class ServiceLayerPimExportParser extends PimExportParser<ImportContext> {

    private static final Logger LOG = LogManager.getLogger(ServiceLayerPimExportParser.class);

    @Autowired
    private ModelService modelService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MediaContainerService mediaContainerService;

    @Autowired
    private DistProductService distProductService;

    @Autowired
    private ProductReferenceCreator productReferenceCreator;

    @Override
    protected void parseSpecific(final ImportContext importContext) {
        createNewProductReferences(importContext);
        handleClassificationMediaContainerReferences(importContext);
        saveProductHashes(importContext);
    }

    @Override
    protected ImportContext getImportContextInstance() {
        return new ImportContext();
    }

    @Override
    protected void registerHandler(final SAXReader reader, final ImportContext importContext) {
        // noop handler to free the space when parsing unused nodes
        final NoopElementHandler noopElementHandler = new NoopElementHandler();

        // register root handler
        reader.addHandler("/STEP-ProductInformation", getHandler(PimImportElementHandlerTypeEnum.ROOT_ELEMENT_HANDLER.getId(), importContext));

        // register unit handler
        reader.addHandler("/STEP-ProductInformation/UnitList/Unit", getHandler(PimImportElementHandlerTypeEnum.UNIT_ELEMENT_HANDLER.getId(), importContext));

        // register attribute handler
        reader.addHandler("/STEP-ProductInformation/AttributeList/Attribute", getHandler(PimImportElementHandlerTypeEnum.ATTRIBUTE_ELEMENT_HANDLER.getId(), importContext));

        // register classification handler
        // XML-Classification is converted into hybris product category

        final Map<String, ElementHandler> categoryDelegates = new HashMap<>();


        PimImportElementHandler categoryElementHandler = getHandler(PimImportElementHandlerTypeEnum.DEFAULT_CATEGORY_ELEMENT_HANDLER.getId(), importContext);

        PimImportElementHandler productLineElementHandler = getHandler(PimImportElementHandlerTypeEnum.PRODUCT_LINE_ELEMENT_HANDLER.getId(), importContext);


        categoryDelegates.put("ClassFolder", categoryElementHandler);
        categoryDelegates.put("L0D e-Shop", categoryElementHandler);
        categoryDelegates.put("L1D Section", categoryElementHandler);
        categoryDelegates.put("L2D Category", categoryElementHandler);
        categoryDelegates.put("L2-3D Cluster", categoryElementHandler);
        categoryDelegates.put("L3D Sub Category", categoryElementHandler);
        categoryDelegates.put("DL3_Productline", productLineElementHandler );


        final AttributeAwareDelegateHandler categoryDelegateHandler = new AttributeAwareDelegateHandler(PimImportElementConverter.ATTRIBUTE_USER_TYPE_ID,
                categoryDelegates);

        reader.addHandler("/STEP-ProductInformation/Classifications/Classification", categoryDelegateHandler);
        reader.addHandler("/STEP-ProductInformation/Classifications/Classification/Classification", categoryDelegateHandler);
        reader.addHandler("/STEP-ProductInformation/Classifications/Classification/Classification/Classification", categoryDelegateHandler);
        reader.addHandler("/STEP-ProductInformation/Classifications/Classification/Classification/Classification/Classification", categoryDelegateHandler);
        reader.addHandler("/STEP-ProductInformation/Classifications/Classification/Classification/Classification/Classification/Classification",
                categoryDelegateHandler);
        reader.addHandler("/STEP-ProductInformation/Classifications/Classification/Classification/Classification/Classification/Classification/Classification",
                categoryDelegateHandler);
        reader.addHandler(
                "/STEP-ProductInformation/Classifications/Classification/Classification/Classification/Classification/Classification/Classification/Classification",
                categoryDelegateHandler);

        // register attribute assignment handler
        reader.addHandler("/STEP-ProductInformation/Classifications/Classification/Classification/AttributeLink",
                getHandler(PimImportElementHandlerTypeEnum.ATTRIBUTE_LINK_ELEMENT_HANDLER.getId(), importContext));
        reader.addHandler("/STEP-ProductInformation/Classifications/Classification/Classification/Classification/AttributeLink",
                getHandler(PimImportElementHandlerTypeEnum.ATTRIBUTE_LINK_ELEMENT_HANDLER.getId(), importContext));
        reader.addHandler("/STEP-ProductInformation/Classifications/Classification/Classification/Classification/Classification/AttributeLink",
                getHandler(PimImportElementHandlerTypeEnum.ATTRIBUTE_LINK_ELEMENT_HANDLER.getId(), importContext));
        reader.addHandler("/STEP-ProductInformation/Classifications/Classification/Classification/Classification/Classification/Classification/AttributeLink",
                getHandler(PimImportElementHandlerTypeEnum.ATTRIBUTE_LINK_ELEMENT_HANDLER.getId(), importContext));
        reader.addHandler(
                "/STEP-ProductInformation/Classifications/Classification/Classification/Classification/Classification/Classification/Classification/AttributeLink",
                getHandler(PimImportElementHandlerTypeEnum.ATTRIBUTE_LINK_ELEMENT_HANDLER.getId(), importContext));
        reader.addHandler(
                "/STEP-ProductInformation/Classifications/Classification/Classification/Classification/Classification/Classification/Classification/Classification/AttributeLink",
                getHandler(PimImportElementHandlerTypeEnum.ATTRIBUTE_LINK_ELEMENT_HANDLER.getId(), importContext));

        // register product handler
        final Map<String, ElementHandler> productDelegates = new HashMap<>();
        productDelegates.put("Manufacturer", getHandler(PimImportElementHandlerTypeEnum.PRODUCT_MANUFACTURER_ELEMENT_HANDLER.getId(), importContext));
        productDelegates.put("360image_collection", getHandler(PimImportElementHandlerTypeEnum.PRODUCT_IMAGE_360_ELEMENT_HANDLER.getId(), importContext));

        // products
        final PimImportElementHandler productEventHandler = getHandler(PimImportElementHandlerTypeEnum.PRODUCT_ELEMENT_HANDLER.getId(), importContext);

        productDelegates.put("DI01_Article", productEventHandler);
        // TODO DISTRELEC-4779 Used for NEDIS products
        productDelegates.put("ne01Override", productEventHandler);
        productDelegates.put("MG01_Article", productEventHandler);
        productDelegates.put("MG02_Article", productEventHandler);
        productDelegates.put("manufacturer_topfolder", noopElementHandler);
        productDelegates.put("360image_collection_topfolder", noopElementHandler);

        final AttributeAwareDelegateHandler productDelegateHandler = new AttributeAwareDelegateHandler(PimImportElementConverter.ATTRIBUTE_USER_TYPE_ID,
                productDelegates);

        // Handle category level "Produktlinie"
        reader.addHandler("/STEP-ProductInformation/Products/Product", productDelegateHandler);
        // Handle category level "Familie"
        reader.addHandler("/STEP-ProductInformation/Products/Product/Product", productDelegateHandler);
        // Handle product OR category level "Serie"
        reader.addHandler("/STEP-ProductInformation/Products/Product/Product/Product", productDelegateHandler);
        // Handle product OR category level "SpezialSerie"
        reader.addHandler("/STEP-ProductInformation/Products/Product/Product/Product/Product", productDelegateHandler);

        // Register ClassificationReference handler
        reader.addHandler("/STEP-ProductInformation/Products/Product/ClassificationReference",
                getHandler(PimImportElementHandlerTypeEnum.CLASSIFICATION_REFERENCE_ELEMENT_HANDLER.getId(), importContext));

        // register asset handler
        final Map<String, ElementHandler> assetDelegates = new HashMap<>();

        // audio
        final PimImportElementHandler audioAssetEventHandler = getHandler(PimImportElementHandlerTypeEnum.AUDIO_ASSET_ELEMENT_HANDLER.getId(),  importContext);
        assetDelegates.put("Audio_mp3", audioAssetEventHandler);

        // images
        final ElementHandler imageAssetEventHandler = getHandler(PimImportElementHandlerTypeEnum.IMAGE_ASSET_ELEMENT_HANDLER.getId(), importContext);

        assetDelegates.put("EPSImage", imageAssetEventHandler);
        assetDelegates.put("JPGImage", imageAssetEventHandler);
        assetDelegates.put("TIFFImage", imageAssetEventHandler);

        // pdf
        final PimImportElementHandler datasheetAssetEventHandler = getHandler(PimImportElementHandlerTypeEnum.DATA_SHEET_ASSET_ELEMENT_HANDLER.getId(), importContext);

        assetDelegates.put("PDF", datasheetAssetEventHandler);
        assetDelegates.put("RTF", datasheetAssetEventHandler);
        assetDelegates.put("WORD", datasheetAssetEventHandler);
        assetDelegates.put("XLS", datasheetAssetEventHandler);
        assetDelegates.put("ZIP", datasheetAssetEventHandler);
        // pdf
        final ElementHandler videoAssetEventHandler = getHandler(PimImportElementHandlerTypeEnum.VIDEO_ASSET_ELEMENT_HANDLER.getId(), importContext);
        assetDelegates.put("AssetsVideo", videoAssetEventHandler);

        final AttributeAwareDelegateHandler assetAttributeAwareDelegateHandler = new AttributeAwareDelegateHandler(
                PimImportElementConverter.ATTRIBUTE_USER_TYPE_ID, assetDelegates, noopElementHandler);
        reader.addHandler("/STEP-ProductInformation/Assets/Asset", assetAttributeAwareDelegateHandler);
    }

    private void handleClassificationMediaContainerReferences(final ImportContext importContext) {
        for (final String categoryCode : importContext.getClassificationMediaContainerReferences().keySet()) {
            final String mediaContainerQualifier = importContext.getClassificationMediaContainerReferences().get(categoryCode);
            try {
                final MediaContainerModel mediaContainer = mediaContainerService.getMediaContainerForQualifier(mediaContainerQualifier);
                final CategoryModel category = categoryService.getCategoryForCode(categoryCode);
                category.setPrimaryImage(mediaContainer);
                modelService.save(category);
            } catch (final UnknownIdentifierException e) {
                LOG.error("Could not resolve mediacontainer for qualifier " + mediaContainerQualifier, e);
            }
        }
    }

    private void createNewProductReferences(final ImportContext importContext) {
        for (final PimProductReferenceDto productReferenceDto : importContext.getProductReferenceDtos()) {
            ProductModel sourceProduct = null;
            ProductModel targetProduct = null;
            try {
                sourceProduct = distProductService.getProductForCode(productReferenceDto.getSourceCode());
            } catch (final UnknownIdentifierException e) {
                LOG.error("Could not find source product with code [" + productReferenceDto.getSourceCode() + "] to create product reference: ", e);
            }

            try {
                targetProduct = distProductService.getProductForPimId(productReferenceDto.getTargetPimId());
            } catch (final UnknownIdentifierException e) {
                LOG.error("Could not find target product with PIM-ID [" + productReferenceDto.getTargetPimId() + "] to create product reference: ", e);
            } catch (final AmbiguousIdentifierException e) {
                LOG.error("More than one product found with PIM-ID [" + productReferenceDto.getTargetPimId() + "] : ", e);
            }

            if (sourceProduct != null && targetProduct == null) {
                // Reset hash value
                sourceProduct.setPimXmlHashMaster(null);
                sourceProduct.setPimHashTimestamp(null);
                try {
                    modelService.save(sourceProduct);
                } catch (final ModelSavingException e) {
                    LOG.error("Could not reset hash value of Product [" + sourceProduct.getCode() + "]", e);
                }
            } else {
                try {
                    modelService.save(productReferenceCreator.create(productReferenceDto, sourceProduct, targetProduct));
                } catch (final ModelSavingException e) {
                    String sourcePCode = sourceProduct == null ? null : sourceProduct.getCode();
                    String targetPCode = targetProduct == null ? null : targetProduct.getCode();
                    LOG.error("Error creating ProductReferenceModel for source [" + sourcePCode + "], target [" + targetPCode
                            + "], reference type [" + productReferenceDto.getProductReferenceType() + "]", e);
                }
            }
        }
    }

    private void saveProductHashes(final ImportContext importContext) {
        for (final Entry<String, String> entry : importContext.getProductHashesToSaveLater().entrySet()) {
            final ProductModel product = distProductService.getProductForCode(entry.getKey());
            boolean isDirty = makeDirty(product);
            product.setPimXmlHashMaster(isDirty ? null : entry.getValue());
            product.setPimHashTimestamp(isDirty ? null : new Date());
            modelService.save(product);
        }
    }

    /**
     * Make the product dirty to force the update for next PIM import for the current locale
     * 
     * @param product
     *            the target product
     * @return {@code true} if the product should be updated during next PIM import, {@code false} otherwise.
     */
    public static boolean makeDirty(final ProductModel product) {
        boolean isDirty = false;
        if (product.getManufacturer() == null) {
            isDirty = true;
        }

        // Coded like this to allow future updates.

        return isDirty;
    }

}
