<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:eval expression="@configurationService.configuration.getString('factfinder.json.campaign.url')" var="ffCampaignUrl" scope="application" />

<div class="data-product hidden" 
	data-ff-search-url="${ffCampaignUrl}"
	data-ff-search-channel="${ffsearchChannel}">${productCode}</div>

