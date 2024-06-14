package com.namics.hybris.toolbox.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class ShortenTag extends TagSupport {
    private String value = null;
    private int length;

    @Override
    public int doStartTag() {
        final JspWriter out = pageContext.getOut();
        try {
            if (value.length() > length) {
                out.print(value.substring(0, length) + "...");
            } else {
                out.print(value);
            }
        } catch (final IOException e) {
            // ignore
        }
        return SKIP_BODY;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public int getLength() {
        return length;
    }

    public void setLength(final int length) {
        this.length = length;
    }
}
