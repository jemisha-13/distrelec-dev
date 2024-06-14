package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

import com.namics.distrelec.b2b.core.model.DistAudioMediaModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Element handler for "Asset" XML elements with UserTypeID="Audio_mp3". Such elements will result in a {@link DistAudioMediaModel} with the
 * multiple formats assigned.
 */
public class AudioAssetElementHandler extends AbstractHashAwarePimImportElementHandler {

    @Autowired
    private MediaService mediaService;

    @Autowired
    private ModelService modelService;

    public AudioAssetElementHandler() {
        super(DistAudioMediaModel._TYPECODE);
    }

    @Override
    protected ItemModel getModel(final String code, final Element element) {
        CatalogVersionModel productCatalogVersion = getImportContext().getProductCatalogVersion();
        try {
            return mediaService.getMedia(productCatalogVersion, code);
        } catch (final UnknownIdentifierException e) {
            DistAudioMediaModel audio = modelService.create(DistAudioMediaModel.class);
            audio.setCode(code);
            audio.setCatalogVersion(productCatalogVersion);
            return audio;
        }
    }
}
