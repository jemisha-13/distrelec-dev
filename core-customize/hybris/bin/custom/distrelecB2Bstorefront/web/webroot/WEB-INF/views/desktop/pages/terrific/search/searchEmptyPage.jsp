<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:theme code="formfeedback.searchbox.text" var="searchText" />

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-search skin-layout-wide">
	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb/>
	</div>

	<mod:global-messages/>

	<div class="ct">
		<c:choose>
			<c:when test="${not empty searchPageData.freeTextSearch}">
				<h1 class="base page-title"><spring:message code="search.no.resultsFor" arguments="${searchTerm}" /></h1>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${not availableToB2B}">
						<h1 class="base page-title"><spring:message code="search.no.results"/></h1>
					</c:when>
					<c:otherwise>
						<h1 class="base page-title"><spring:message code="article.business.only.error"/> </h1>
					</c:otherwise>
				</c:choose>
			</c:otherwise>	
		</c:choose>

		<c:forEach var="feedbackCampaign" items="${searchPageData.feedbackCampaigns}">

			<%-- Searching Tips - Text Block --%>
			<c:if test="${not empty feedbackCampaign.feedbackTexts['SearchResult_top'] || not empty feedbackCampaign.pushedProducts}">
				<mod:campaign skin="feedback" feedbackTextTop="${feedbackCampaign.feedbackTexts['SearchResult_top']}" pushedProductsList="${feedbackCampaign.pushedProducts}" />
			</c:if>

		</c:forEach>
	</div>

	<mod:zeroresults />

	<div class="ct">
		<div class="row">
			<div class="cf">
				<mod:form-feedback template="search-noresults" skin="noresults" />
			</div>
		</div>
	</div>

</views:page-default>
