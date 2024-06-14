package com.namics.distrelec.b2b.core.inout.pim.families;

import com.namics.distrelec.b2b.core.inout.pim.PimExportParser;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.PimImportElementConverter;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler.AttributeAwareDelegateHandler;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler.NoopElementHandler;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler.PimImportElementHandlerTypeEnum;
import org.dom4j.ElementHandler;
import org.dom4j.io.SAXReader;

import java.util.HashMap;
import java.util.Map;

public class ProductFamilyPimExportParser extends PimExportParser<ImportContext> {

    @Override
    protected void parseSpecific(ImportContext importContext) {

    }

    @Override
    protected ImportContext getImportContextInstance() {
        return new ImportContext();
    }

    @Override
    protected void registerHandler(SAXReader reader, ImportContext importContext) {
        // noop handler to free the space when parsing unused nodes
        final NoopElementHandler noopElementHandler = new NoopElementHandler();

        reader.addHandler("/STEP-ProductInformation", getHandler(PimImportElementHandlerTypeEnum.ROOT_ELEMENT_HANDLER.getId(), importContext));
        reader.addHandler("/STEP-ProductInformation/Products/Product", getHandler(PimImportElementHandlerTypeEnum.PRODUCT_FAMILY_ELEMENT_HANDLER.getId(), importContext));

        // register asset handler
        Map<String, ElementHandler> assetDelegates = new HashMap<>();

        // images
        final ElementHandler imageAssetEventHandler = getHandler(PimImportElementHandlerTypeEnum.PRODUCT_FAMILY_IMAGE_ASSET_HANDLER.getId(), importContext);
        assetDelegates.put("EPSImage", imageAssetEventHandler);
        assetDelegates.put("JPGImage", imageAssetEventHandler);
        assetDelegates.put("TIFFImage", imageAssetEventHandler);
        assetDelegates.put("PDF", imageAssetEventHandler);
        
        // video
        ElementHandler videoAssetEventHandler = getHandler(PimImportElementHandlerTypeEnum.PRODUCT_FAMILY_VIDEO_ASSET_HANDLER.getId(), importContext);
        assetDelegates.put("AssetsVideo", videoAssetEventHandler);

        AttributeAwareDelegateHandler assetAttributeAwareDelegateHandler = new AttributeAwareDelegateHandler(
            PimImportElementConverter.ATTRIBUTE_USER_TYPE_ID, assetDelegates, noopElementHandler);
        reader.addHandler("/STEP-ProductInformation/Assets/Asset", assetAttributeAwareDelegateHandler);
    }
}
