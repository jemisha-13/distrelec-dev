/*
 * Copyright 2000-2011 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.tags;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.ENVIRONMENT_ISPROD;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.distrelec.b2b.storefront.identify.MethodSignatureIdentifier;

import de.hybris.platform.util.Config;

/**
 * NamicsIdentifierTag evaluates a given Object and displays all important methods for the JSP pages on the browsers console.log.
 * Additionally the recursion depth can be adjusted.
 *
 * @author mwegener, Namics AG
 * @since Namics Extensions 1.0
 *
 */
public class IdentifierTag extends SimpleTagSupport {
    private final static Logger LOG = Logger.getLogger(IdentifierTag.class.getName());

    private Object obj = null;

    private JspWriter jspWriter = null;

    private Integer maxDepth = Integer.valueOf(2);

    @Override
    public void doTag() throws JspException, IOException {
        final boolean isProd = Config.getBoolean(ENVIRONMENT_ISPROD, true);

        if (isProd) {
            return;
        }

        LOG.warn("Use this Tag for Development only. Remove from Production environment!");
        writeConsoleBeginning();

        final StringWriter jsonResult = new StringWriter();
        final MethodSignatureIdentifier identifier = new MethodSignatureIdentifier();
        identifier.setMaxDepth(maxDepth);

        if (null == this.obj) {
            // if no object type is passed, show all available attributes from request scope
            final ServletRequest request = ((PageContext) getJspContext()).getRequest();
            new ObjectMapper().writeValue(jsonResult, identifier.identifyFromRequest(request));
        } else {
            new ObjectMapper().writeValue(jsonResult, identifier.identify(this.obj));
        }

        printLine(jsonResult.toString());
        writeConsoleEnding();
    }

    private void writeConsoleBeginning() throws IOException {
        printLine("<script type='text/javascript'>");
        // use if(console) so it won't crash on older IE versions
        printLine("if(typeof(console) === 'object') {");
        final String jspName = ((PageContext) getJspContext()).getPage().getClass().getName();
        if (null == this.obj) {
            printLine("console.group('Identify all request scope variable names for context " + jspName + "');");
        } else {
            printLine("console.group('Identify variable " + this.obj + " for context " + jspName + "');");
        }
        printLine("console.dir(");
    }

    private void writeConsoleEnding() throws IOException {
        printLine(");");
        printLine("console.groupEnd();");
        printLine("}");
        printLine("</script>");
    }

    private void printLine(final String msg) throws IOException {
        if (null == this.jspWriter) {
            final PageContext pageContext = (PageContext) getJspContext();
            this.jspWriter = pageContext.getOut();
        }
        this.jspWriter.println(msg);
    }

    public void setType(final Object obj) {
        this.obj = obj;
    }

    public void setDepth(final Integer depth) {
        this.maxDepth = depth;
    }
}
