package com.namics.distrelec.b2b.facades.category.populator;

import com.namics.distrelec.b2b.core.model.DistVideoMediaModel;
import com.namics.distrelec.b2b.core.model.category.AbstractDistOrderedElementModel;
import com.namics.distrelec.b2b.core.model.category.DistOrderedMediaElementModel;
import com.namics.distrelec.b2b.core.model.category.DistOrderedTextElementModel;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.product.data.DistVideoData;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class DistProductFamilyPopulator implements Populator<CategoryModel, CategoryData> {
	
	private static final String VIDEO_MEDIA_TYPE="Video";
	
    @Autowired
    private DistCategoryService distCategoryService;

    @Autowired
    @Qualifier("mediaContainerToImageMapConverter")
    private Converter<MediaContainerModel, Map<String, ImageData>> mediaContainerToImageMapConverter;
    
	@Autowired
	@Qualifier("distVideoMediaConverter")
	private Converter<DistVideoMediaModel, DistVideoData> distVideoMediaConverter;
    
    @Autowired
    private I18NService i18nService;

    @Autowired
    private DistUrlResolver<CategoryModel> distProductFamilyUrlResolver;
    
    @Override
    public void populate(CategoryModel source, CategoryData target) throws ConversionException {
        if (!distCategoryService.isProductFamily(source)) {
            return;
        }
        populateFamilyMedia(source, target);
        populateFamilyVideo(source, target);
        populateBullets(source, target);
        populateApplications(source, target);
        Optional.ofNullable(source.getFamilyDatasheet(getI18nService().getCurrentLocale())).map(mediaContainerToImageMapConverter::convert).ifPresent(target::setFamilyDatasheet);
        Optional.ofNullable(source.getFamilyImage()).map(mediaContainerToImageMapConverter::convert).ifPresent(target::setFamilyImage);
        Optional.ofNullable(source.getFamilyManufacturerImage()).map(mediaContainerToImageMapConverter::convert).ifPresent(target::setFamilyManufacturerImage);
        target.setFamilyCategoryCode(getSuperCategory(source.getSupercategories()));
        target.setUrl(distProductFamilyUrlResolver.resolve(source));
    }
    
    private String getSuperCategory(Collection<CategoryModel> superCategories) {
    	if(!CollectionUtils.isEmpty(superCategories)) {
    		if(superCategories.iterator().hasNext()) {
    			return superCategories.iterator().next().getCode();
    		}
    	}
    	return "";
    	
    }

    private void populateApplications(CategoryModel source, CategoryData target) {
        target.setFamilyApplications(Optional.ofNullable(source.getApplications())
                                             .map(Set::stream)
                                             .orElse(Stream.empty())
                                             .map(DistOrderedTextElementModel::getText)
                                             .sorted()
                                             .collect(Collectors.toList()));
    }

    private void populateBullets(CategoryModel source, CategoryData target) {
        target.setFamilyBullets(Optional.ofNullable(source.getBullets())
                                        .map(Set::stream)
                                        .orElse(Stream.empty())
                                        .filter(p -> isNotBlank(p.getText()))
                                        .collect(Collectors.toMap(DistOrderedTextElementModel::getOrder, DistOrderedTextElementModel::getText)));
    }

    private void populateFamilyMedia(CategoryModel source, CategoryData target) {
        target.setFamilyMedia(Optional.ofNullable(source.getFamilyMedia())
                                      .map(Set::stream)
                                      .orElse(Stream.empty())
                                      .filter(el -> el.getMediaContainer() != null)
                                      .sorted(Comparator.comparing(AbstractDistOrderedElementModel::getOrder))
                                      .map(DistOrderedMediaElementModel::getMediaContainer)
                                      .map(mediaContainerToImageMapConverter::convert)
                                      .collect(Collectors.toList()));
    }
    
    private void populateFamilyVideo(CategoryModel source, CategoryData target) {
    	 List<DistVideoData> video=new ArrayList<DistVideoData>();
         for(DistOrderedMediaElementModel familyMediaObject:source.getFamilyMedia()) {
        	 if(familyMediaObject.getMediaType()!=null && VIDEO_MEDIA_TYPE.equalsIgnoreCase(familyMediaObject.getMediaType())) {
         		Iterator<MediaModel> videoIterator=familyMediaObject.getMediaContainer().getMedias().iterator();
 	        		while(videoIterator.hasNext()) {
 	        			video.add(distVideoMediaConverter.convert((DistVideoMediaModel) videoIterator.next()));
         		}
         	}
         }
         target.setFamilyVideo(video);
    }
    
    public I18NService getI18nService() {
        return i18nService;
    }

    public void setI18nService(final I18NService i18nService) {
        this.i18nService = i18nService;
    }

}
