package com.namics.distrelec.b2b.storefront.util;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"success", "challenge_ts", "hostname", "error-codes"})
public class CaptchaResponse {

	@JsonProperty("success")
	private boolean success;

	@JsonProperty("challenge_ts")
	private String challengeTs;

	@JsonProperty("hostname")
	private String hostname;

	@JsonIgnore
	private String[] errorCodes;

	public String getChallengeTs() {
		return challengeTs;
	}

	public void setChallengeTs(String challengeTs) {
		this.challengeTs = challengeTs;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String[]  getErrorCodes() {
		return errorCodes;
	}

	public void setErrorCodes(String [] errorCodes) {
		this.errorCodes = errorCodes;
	}
}
