package com.namics.distrelec.b2b.core.pdf.renderer;

import org.apache.velocity.VelocityContext;

public interface DistVelocityRenderer {

    String renderTemplateForContext(String templateName, VelocityContext context);
}
