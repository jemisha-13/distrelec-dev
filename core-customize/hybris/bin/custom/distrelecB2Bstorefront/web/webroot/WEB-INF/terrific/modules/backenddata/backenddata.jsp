<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:eval expression="@configurationService.configuration.getString('distrelecfusionintegration.fusion.search.url')" var="fusionSearchUrl" scope="application" />
<spring:eval expression="@configurationService.configuration.getString('distrelecfusionintegration.fusion.api.key.search')" var="fusionApiKey" scope="application" />
<spring:eval expression="@configurationService.configuration.getString('distrelecfusionintegration.fusion.collectionSuffix')" var="collectionSuffix" scope="application" />

<div class="shopsettings" data-shop="${siteUid}" 
	data-shop-label="${siteName}" data-channel="${currentChannel.type}" 
	data-channel-label="${currentChannel.type}" 
	data-country="${currentCountry.isocode}" 
	data-country-label="${currentCountry.name}" 
	data-language="${currentLanguage.isocode}" 
	data-language-label="${currentLanguage.nativeName}" 
	data-currency="${currentCurrency.isocode}" 
	data-currency-label="${currentCurrency.symbol}" 
	data-use-technical-view="${useTechnicalView}" 
	data-use-list-view="${useListView}" 
	data-use-icon-view="${useIconView}"
	data-auto-apply-filter="${autoApplyFilter}"
	data-search-fusionurl="${fusionSearchUrl}"
	data-search-fusionapikey="${fusionApiKey}"
	data-search-collection-suffix="${collectionSuffix}" 
	data-search-experience="${searchExperience}"
>
</div>

<sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')">
    <div class="usersettings" data-login="true" data-role="customer"></div>
</sec:authorize>
<sec:authorize access="hasRole('ROLE_B2BADMINGROUP')">
    <div class="usersettings" data-login="true" data-role="admin"></div>
</sec:authorize>
<sec:authorize access="hasRole('ROLE_OCICUSTOMERGROUP')">
    <div class="usersettings" data-login="true" data-role="oci"></div>
</sec:authorize>
<%-- the Eproc Group is set when OCI / ARIBA Users see the page, we need it to disable functionality, which is not supported because of compat mode and documentmode IE5 Quirks --%>
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<div class="usersettings" data-login="true" data-role="eproc"></div>
</sec:authorize>
<sec:authorize access="hasRole('ROLE_ARIBACUSTOMERGROUP')">
    <div class="usersettings" data-login="true" data-role="ariba"></div>
</sec:authorize>
<sec:authorize access="!hasRole('ROLE_CUSTOMERGROUP') and !hasRole('ROLE_B2BADMINGROUP')">
    <div class="usersettings" data-login="false"></div>
</sec:authorize>
