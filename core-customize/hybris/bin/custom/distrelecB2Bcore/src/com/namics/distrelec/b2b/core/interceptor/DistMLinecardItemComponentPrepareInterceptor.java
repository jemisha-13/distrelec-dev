package com.namics.distrelec.b2b.core.interceptor;

import org.apache.commons.lang.StringUtils;

import com.namics.distrelec.b2b.core.model.cms2.components.DistrelecManufacturerLinecardItemComponentModel;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

/**
 * 
 * @author akshay
 * 
 */
public class DistMLinecardItemComponentPrepareInterceptor implements PrepareInterceptor {

	@Override
	public void onPrepare(Object model, InterceptorContext ctx) throws InterceptorException {
		final DistrelecManufacturerLinecardItemComponentModel linecardComponent = (DistrelecManufacturerLinecardItemComponentModel) model;
		if (linecardComponent.getManufacturer() != null) {
			for (final MediaModel media : linecardComponent.getManufacturer().getPrimaryImage().getMedias()) {
				if (StringUtils.isNotEmpty(media.getMediaFormat().getQualifier())
						&& media.getMediaFormat().getQualifier().equals("brand_logo")
						&& StringUtils.isEmpty(linecardComponent.getUrl())) {
					linecardComponent.setUrl("/manufacturer/" + linecardComponent.getManufacturer().getUrlId());
					linecardComponent.setTitle(linecardComponent.getManufacturer().getName());
					linecardComponent.setIcon(media);
				}
			}
		}
	}
}
