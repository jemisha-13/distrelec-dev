/**
 *
 */
package com.namics.distrelec.occ.core.xstream;

import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;


/**
 * @author abhinayjadhav
 *
 */
public class DistMediaTypes
{

	public static final MediaType APPLICATION_CSP_REPORT_UTF8 = new MediaType("application", "csp-report", StandardCharsets.UTF_8);
	public static final MediaType APPLICATION_CSP_REPORT = new MediaType("application", "csp-report");

	public static final String APPLICATION_CSP_REPORT_UTF8_VALUE = "application/csp-report;charset=UTF-8";
	public static final String APPLICATION_CSP_REPORT_VALUE = "application/csp-report";

}
