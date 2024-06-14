
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
		"document-uri",
		"effective-directive",
		"disposition",
		"status-code",
		"blocked-uri",
		"referrer",
		"violated-directive",
		"original-policy",
		"source-file",
		"script-sample",
		"remote-address"
})
@Generated("jsonschema2pojo")
@JsonSerialize
public class CspReport
{

	@JsonProperty("document-uri")
	private String documentUri;
	@JsonProperty("effective-directive")
	private String effectiveDirective;
	@JsonProperty("disposition")
	private String disposition;
	@JsonProperty("status-code")
	private Integer statusCode;
	@JsonProperty("blocked-uri")
	private String blockedUri;
	@JsonProperty("referrer")
	private String referrer;
	@JsonProperty("violated-directive")
	private String violatedDirective;
	@JsonProperty("original-policy")
	private String originalPolicy;
	@JsonProperty("source-file")
	private String sourceFile;
	@JsonProperty("script-sample")
	private String scriptSample;
	@JsonProperty("remote-address")
    private String remoteAddress;
	
	@JsonIgnore
	private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("document-uri")
	public String getDocumentUri()
	{
		return documentUri;
	}

	@JsonProperty("document-uri")
	public void setDocumentUri(final String documentUri)
	{
		this.documentUri = documentUri;
	}

	@JsonProperty("effective-directive")
	public String getEffectiveDirective()
	{
		return effectiveDirective;
	}

	@JsonProperty("effective-directive")
	public void setEffectiveDirective(final String effectiveDirective)
	{
		this.effectiveDirective = effectiveDirective;
	}

	@JsonProperty("disposition")
	public String getDisposition()
	{
		return disposition;
	}

	@JsonProperty("disposition")
	public void setDisposition(final String disposition)
	{
		this.disposition = disposition;
	}

	@JsonProperty("status-code")
	public Integer getStatusCode()
	{
		return statusCode;
	}

	@JsonProperty("status-code")
	public void setStatusCode(final Integer statusCode)
	{
		this.statusCode = statusCode;
	}

	@JsonProperty("blocked-uri")
	public String getBlockedUri()
	{
		return blockedUri;
	}

	@JsonProperty("blocked-uri")
	public void setBlockedUri(final String blockedUri)
	{
		this.blockedUri = blockedUri;
	}

	@JsonProperty("referrer")
	public String getReferrer()
	{
		return referrer;
	}

	@JsonProperty("referrer")
	public void setReferrer(final String referrer)
	{
		this.referrer = referrer;
	}

	@JsonProperty("violated-directive")
	public String getViolatedDirective()
	{
		return violatedDirective;
	}

	@JsonProperty("violated-directive")
	public void setViolatedDirective(final String violatedDirective)
	{
		this.violatedDirective = violatedDirective;
	}

	@JsonProperty("original-policy")
	public String getOriginalPolicy()
	{
		return originalPolicy;
	}

	@JsonProperty("original-policy")
	public void setOriginalPolicy(final String originalPolicy)
	{
		this.originalPolicy = originalPolicy;
	}

	@JsonProperty("source-file")
	public String getSourceFile()
	{
		return sourceFile;
	}

	@JsonProperty("source-file")
	public void setSourceFile(final String sourceFile)
	{
		this.sourceFile = sourceFile;
	}

	@JsonProperty("script-sample")
	public String getScriptSample()
	{
		return scriptSample;
	}

	@JsonProperty("script-sample")
	public void setScriptSample(final String scriptSample)
	{
		this.scriptSample = scriptSample;
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

	@JsonProperty("remote-address")
    public String getRemoteAddress() {
        return remoteAddress;
    }

	@JsonProperty("remote-address")
    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

	
}
