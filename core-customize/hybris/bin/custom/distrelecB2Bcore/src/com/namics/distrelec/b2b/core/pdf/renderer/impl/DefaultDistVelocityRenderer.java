package com.namics.distrelec.b2b.core.pdf.renderer.impl;

import com.namics.distrelec.b2b.core.pdf.renderer.DistVelocityRenderer;
import com.namics.distrelec.b2b.core.pdf.service.DistPDFTemplateService;
import de.hybris.platform.commons.renderer.exceptions.RendererException;
import de.hybris.platform.commons.renderer.impl.VelocityTemplateRenderer;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class DefaultDistVelocityRenderer extends VelocityTemplateRenderer implements DistVelocityRenderer {

    @Autowired
    private DistPDFTemplateService distPDFTemplateService;

    @Override
    public String renderTemplateForContext(String templateName, VelocityContext context) {
        InputStream inputStream = distPDFTemplateService.getTemplateForName(templateName);
        StringWriter stringWriter = new StringWriter();
        try {
            writeToOutput(stringWriter, inputStream, context);
        } catch (IOException ex) {
            return null;
        }
        return stringWriter.toString();
    }

    private void writeToOutput(final Writer result, final InputStream inputStream, final VelocityContext ctx) throws IOException
    {
      final Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

        try
        {
            evaluate(result, ctx, reader);
            result.flush();
        }
        catch (final Exception e)
        {
            throw new RendererException("Problem with get velocity stream", e);
        }
        finally
        {
            IOUtils.closeQuietly(reader);
        }
    }
}
