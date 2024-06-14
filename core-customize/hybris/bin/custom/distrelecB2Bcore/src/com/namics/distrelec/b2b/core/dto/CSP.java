
package com.namics.distrelec.b2b.core.dto;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
{
		"csp-report"
})
@Generated("jsonschema2pojo")
@JsonSerialize
public class CSP
{

	@JsonProperty("csp-report")
	private CspReport cspReport;
	@JsonIgnore
	private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("csp-report")
	public CspReport getCspReport()
	{
		return cspReport;
	}

	@JsonProperty("csp-report")
	public void setCspReport(final CspReport cspReport)
	{
		this.cspReport = cspReport;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties()
	{
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(final String name, final Object value)
	{
		this.additionalProperties.put(name, value);
	}

}
