package com.namics.distrelec.b2b.core.pdf.service.impl;

import com.namics.distrelec.b2b.core.pdf.data.DistPdfData;
import com.namics.distrelec.b2b.core.pdf.messages.DistPDFMessagesStrategy;
import com.namics.distrelec.b2b.core.pdf.renderer.DistVelocityRenderer;
import com.namics.distrelec.b2b.core.pdf.service.DistPDFService;
import com.namics.distrelec.b2b.core.pdf.strategy.DistXslTransformationStrategy;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Service
public class DefaultDistPDFService implements DistPDFService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistPDFService.class);

    @Autowired
    private DistVelocityRenderer distVelocityRenderer;

    @Autowired
    private DistPDFMessagesStrategy distPDFMessagesStrategy;

    @Autowired
    private DistXslTransformationStrategy distXslTransformationStrategy;


    @Override
    public InputStream generatePdfFromData(DistPdfData data) {
        if (data == null) {
            LOG.info("Passed null Pdf Data. Returning null");
            return null;
        }

        Map<String, Object> messages = distPDFMessagesStrategy.getMessagesForSiteAndStream(data.getTemplateName(),
                data.getCmsSiteModel(), data.getLanguage());
        data.setMessages(messages);

        String xml = distVelocityRenderer.renderTemplateForContext(data.getTemplateName(), getContext(data));

        if (StringUtils.isBlank(xml)) {
            LOG.warn("XML string is null or blank. Returning null");
            return null;
        }

        ByteArrayOutputStream outputStream = distXslTransformationStrategy.transformXslToPdf(xml, data.getXslName());
        if (outputStream == null) {
            LOG.warn("PDF output stream is null. Returning null");
            return null;
        }

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private VelocityContext getContext(DistPdfData data) {
        Field[] fields = data.getClass().getDeclaredFields();
        Map<String, Object> contextMap = new HashMap<>();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                contextMap.put(field.getName(), field.get(data));
            } catch (IllegalAccessException e) {
                //silent catch
            }
        }
        Map<String, Object> additionalParameters = data.getAdditionalParameters();
        if (additionalParameters != null) {
            contextMap.putAll(additionalParameters);
        }
        return new VelocityContext(contextMap);
    }
}
