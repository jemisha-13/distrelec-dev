/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.Page;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.PageCategory;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.PageInfo;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.AbstractBindingResult;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@code DigitalDatalayerHandlerInterceptorAdapter}
 * 
 *
 * @author <a href="abhinay.jadhav@datwyler.com">Abhinay Jadhav</a>, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.2
 */
public class DigitalDatalayerHandlerInterceptorAdapter extends HandlerInterceptorAdapter {

    private static final Logger LOG = LogManager.getLogger(DigitalDatalayerHandlerInterceptorAdapter.class);
    private static final String FORM_ERROR= "form error";
    private static final String BUSINESS_ERROR= "business error";
    @Autowired
    private ConfigurationService configurationService;

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final ModelAndView modelAndView) {
        if (!configurationService.getConfiguration().getBoolean(DistConfigConstants.FEATURE_DATALAYER, true)) {
            return;
        }

        try {
            if (modelAndView == null || modelAndView.getModelMap() == null) {
                return;
            }

            final Object dataLayerObj = modelAndView.getModelMap().get(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER);
            if (dataLayerObj instanceof DigitalDatalayer) {
                final DigitalDatalayer digitalDatalayer = (DigitalDatalayer) dataLayerObj;
                try {
                    final StringBuilder hierarchy = new StringBuilder();
                    if (null != digitalDatalayer.getPage() && null != digitalDatalayer.getPage().getPageCategory()) {
                        final Page page = digitalDatalayer.getPage();

                        if (null == page) {
                            digitalDatalayer.setPage(new Page());
                        }
                        PageInfo pageInfo = page.getPageInfo();
                        if (null == pageInfo) {
                            pageInfo = new PageInfo();
                        }
                        if (null == digitalDatalayer.getPage().getPageCategory()) {
                            digitalDatalayer.getPage().setPageCategory(new PageCategory());
                        }

                        hierarchy.append(pageInfo.getCountryCode()).append("|");
                        hierarchy.append(pageInfo.getLanguage()).append("|");

                        hierarchy.append((null != digitalDatalayer.getPage().getPageCategory().getPrimaryCategory()
                                && !digitalDatalayer.getPage().getPageCategory().getPrimaryCategory().isEmpty())
                                        ? (digitalDatalayer.getPage().getPageCategory().getPrimaryCategory() + "|")
                                        : "");
                        hierarchy.append((null != digitalDatalayer.getPage().getPageCategory().getSubCategoryL1()
                                && !digitalDatalayer.getPage().getPageCategory().getSubCategoryL1().isEmpty())
                                        ? (digitalDatalayer.getPage().getPageCategory().getSubCategoryL1() + "|")
                                        : "");
                        hierarchy.append((null != digitalDatalayer.getPage().getPageCategory().getSubCategoryL2()
                                && !digitalDatalayer.getPage().getPageCategory().getSubCategoryL2().isEmpty())
                                        ? (digitalDatalayer.getPage().getPageCategory().getSubCategoryL2() + "|")
                                        : "");
                        hierarchy.append((null != digitalDatalayer.getPage().getPageCategory().getSubCategoryL3()
                                && !digitalDatalayer.getPage().getPageCategory().getSubCategoryL3().isEmpty())
                                        ? (digitalDatalayer.getPage().getPageCategory().getSubCategoryL3() + "|")
                                        : "");
                        hierarchy.append((null != digitalDatalayer.getPage().getPageCategory().getSubCategoryL4()
                                && !digitalDatalayer.getPage().getPageCategory().getSubCategoryL4().isEmpty())
                                        ? (digitalDatalayer.getPage().getPageCategory().getSubCategoryL4() + "|")
                                        : "");
                        hierarchy.append((null != digitalDatalayer.getPage().getPageCategory().getSubCategoryL5()
                                && !digitalDatalayer.getPage().getPageCategory().getSubCategoryL5().isEmpty())
                                        ? (digitalDatalayer.getPage().getPageCategory().getSubCategoryL5() + "|")
                                        : "");
                        digitalDatalayer.getPage().setHier1(hierarchy.toString().toLowerCase());
                      

                    }
                } catch (final Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                }
                populateFormError(modelAndView, response,digitalDatalayer);
                final ObjectMapper mapper = new ObjectMapper();
                final String result = mapper.writeValueAsString(digitalDatalayer);
                LOG.debug("Datalayer for {} is {}", request.getRequestURI(), result);
                modelAndView.addObject(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER_DATA, result);
            }
        } catch (final Exception ex) {
            LOG.error("Error while parsing datalayer object: ", ex);
        }
    }
    
    private void populateFormError(ModelAndView modelAndView, final HttpServletResponse response, DigitalDatalayer datalayer) {
    	List<org.springframework.validation.BeanPropertyBindingResult> bindingResults =  modelAndView.getModelMap().values().stream().filter(BeanPropertyBindingResult.class::isInstance).map(BeanPropertyBindingResult.class::cast).filter(br -> br.getAllErrors().size()>0).collect(Collectors.toList());
    	long errorCount = bindingResults.size();
    	com.namics.distrelec.b2b.facades.adobe.datalayer.data.Error error = new  com.namics.distrelec.b2b.facades.adobe.datalayer.data.Error();
    	if(errorCount > 0) {
    		error.setErrorPageType(FORM_ERROR);
    		List<List<FieldError>> fieldErrors = bindingResults.stream().map(AbstractBindingResult::getFieldErrors).collect(Collectors.toList());
    		List<List<ObjectError>> objectErrors = bindingResults.stream().map(AbstractBindingResult::getAllErrors).collect(Collectors.toList());
    		if(fieldErrors.size() >0 && null!=fieldErrors.get(0) && fieldErrors.get(0).size()>0) {
    			error.setErrorPageType(FORM_ERROR+"|"+bindingResults.stream().map(AbstractBindingResult::getFieldError).map(FieldError:: getField).collect( Collectors.joining("|")));

    		}else if(objectErrors.size() >0 && null!=objectErrors.get(0) && objectErrors.get(0).size()>0) {

    			StringBuilder errorType = new StringBuilder();
    			for(BeanPropertyBindingResult bpr: bindingResults) {
    				if(null!=bpr.getAllErrors() && bpr.getAllErrors().size()>0) {
    					errorType.append(FORM_ERROR).append("|");
    					errorType.append(bpr.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining("|")));
    					if(null==datalayer.getPage()) {
    						datalayer.setPage(new Page());
    					}
    					if(null==datalayer.getPage().getPageInfo()) {
    						datalayer.getPage().setPageInfo(new PageInfo());
    					}
    					datalayer.getPage().getPageInfo().setFormId(bpr.getAllErrors().stream().map(ObjectError::getObjectName).collect(Collectors.joining("|")));
    				}
    			}
    			error.setErrorPageType(errorType.toString());
    		}
    		LOG.info("Binding Errors! Error count :" + errorCount);
    	}else if(null!=modelAndView.getModelMap().get("accErrorMsgs")) {
    		error.setErrorPageType(BUSINESS_ERROR);
    	}
    	if(response.getStatus()!=200 && response.getStatus()!=302) {
    		error.setErrorPageType(String.valueOf(response.getStatus()+" error page"));
    	}
    	error.setErrorCode(String.valueOf(response.getStatus()));
		datalayer.getPage().getPageInfo().setError(error);
    }

}
