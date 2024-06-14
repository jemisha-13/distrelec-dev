package com.namics.distrelec.b2b.core.reevoo.purchaserfeed.smc;

import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import de.hybris.platform.servicelayer.config.ConfigurationService;

public class DistSMCServiceImpl implements DistSMCService {
	
	protected static final String APPLICATION_JSON = "application/json";
	private static final String ENCODE_AUTH = "Basic ";
	private static final String PERMISSION_GRANTED="\"PermissionGranted\":\"Y";
	private static final String SUBSCRIPTION_EXISTS="\"SubscriptionSignUpExists\":\"Y";
	private static final String AUTHORIZATION_HEADER="Authorization";
	private String rootUrl;

	@Autowired
	private ConfigurationService configurationService;

	
	@Override
	public boolean isUserSubscribedToEmail(String email) {
		HttpHeaders headers = createHttpHeaders(
				configurationService.getConfiguration().getString("sapymktcommon.odata.url.user"),
				configurationService.getConfiguration().getString("sapymktcommon.odata.url.password"));
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

		final RestTemplate restTemplate = new RestTemplate();
		final String fooResourceUrl = rootUrl+"ContactOriginData(ContactID='"+email.toLowerCase()+"',ContactOrigin='SAP_HYBRIS_CONSUMER')/MarketingPermissions?$filter=CommunicationMedium eq 'EMAIL'";
		System.out.println("fooResourceUrl"+fooResourceUrl);
		ResponseEntity<String> response =null;
		try {
		response= restTemplate.exchange(fooResourceUrl, HttpMethod.GET, entity,
				String.class);
		}catch (Exception ex ) {
			return false;
		}
		return (response!=null && response.getBody()!=null) ? response.getBody().contains(PERMISSION_GRANTED):false;
	}
	
	private HttpHeaders createHttpHeaders(String user, String password) {
		String notEncoded = user + ":" + password;

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add(AUTHORIZATION_HEADER, ENCODE_AUTH + Base64.getEncoder().encodeToString(notEncoded.getBytes()));
		return headers;
	}
	
	@Override
	public boolean isUserSubscribedToSurvey(String email) {
		HttpHeaders headers = createHttpHeaders(
				configurationService.getConfiguration().getString("sapymktcommon.odata.url.user"),
				configurationService.getConfiguration().getString("sapymktcommon.odata.url.password"));
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

		final RestTemplate restTemplate = new RestTemplate();
		String communicationCategory=configurationService.getConfiguration().getString("ymkt.category.customersurvey.id");
		final String fooResourceUrl =  rootUrl+"ContactOriginData(ContactID='"+email.toLowerCase()+"',ContactOrigin='SAP_HYBRIS_CONSUMER')/MarketingSubscriptions?$filter=SubscriptionTopic eq '"+communicationCategory+"'";
		System.out.println("fooResourceUrl"+fooResourceUrl);
		 ResponseEntity<String> response=null;
		try {
		response = restTemplate.exchange(fooResourceUrl, HttpMethod.GET, entity,
				String.class);
		}catch(Exception ex) {
			return false;
		}

		return (response!=null && response.getBody()!=null) ? response.getBody().contains(SUBSCRIPTION_EXISTS):false;
	}
	
	
	public String getRootUrl() {
		return rootUrl;
	}

	public void setRootUrl(String rootUrl) {
		this.rootUrl = rootUrl;
	}


}
