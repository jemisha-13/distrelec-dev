<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:theme code="manufacturerStores.productGroup.title" text="Product Groups" var="sProductGroups"/>
<spring:theme code="manufacturerStores.language.title" text="Languages" var="sLanguages"/>
<spring:theme code="manufacturerStores.phoneNumber.title" text="Phone Numbers" var="sPhoneNumbers"/>
<spring:theme code="manufacturerStores.website.title" text="Websites" var="sWebsites"/>
<spring:theme code="manufacturerStores.supportEmails.title" text="Support E-mails" var="sSupportEmails"/>
<spring:theme code="manufacturerStores.repair.title" text="Repairs" var="sRepairs"/>
<spring:theme code="productfinder.btn.results" text="Show Products" var="sShowProducts"/>

<spring:theme code="product.list.read.more" text="Read More" var="sShowMore"/>
<spring:theme code="product.list.read.less" text="Read Less" var="sShowLess"/>

<spring:theme code="product.list.no.result" text="Unfortunately, your query returned no relevant results:" var="sCategoryNoResult" arguments="${request.getParameter('digitalDataLayerTerm')}" />

<%--Filter out info blocks based on absence of info data--%>
<%--Initialisation of hide classes--%>
<c:set var="hideOuterBox" scope="page" value="hideOuterBox"/>
<c:set var="hideInnerBox" scope="page" value="hideInnerBox"/>
<c:set var="hideLogo" scope="page" value="hideLogo"/>
<c:set var="hideManufacturerDetails" scope="page" value="hideManufacturerDetails"/>
<c:set var="hideProductGroups" scope="page" value="hideProductGroups"/>
<c:set var="hidePhoneNumbers" scope="page" value="hidePhoneNumbers"/>
<c:set var="hideLanguages" scope="page" value="hideLanguages"/>
<c:set var="hideWebsites" scope="page" value="hideWebsites"/>
<c:set var="hideManufacturerLinks" scope="page" value="hideManufacturerLinks"/>
<c:set var="hideManufacturerRepairs" scope="page" value="hideManufacturerRepairs"/>

<c:set var="redirectedFromFilteredSearch" value="${request.getParameter('RedirectedFromSearch')}" />

<%--Check which hide classes to disable on account of info being found--%>
<c:set var="outerBoxMissingCtr" scope="page" value="0"></c:set>
<c:set var="innerBoxMissingCtr" scope="page" value="0"></c:set>
<c:set var="manufacturerDetailsMissingCtr" scope="page" value="0"></c:set>
<c:choose>
	<c:when test="${not empty manufacturer.url}">
		<c:set var="showManufacturerLink" scope="page" value="true" />
	</c:when>
	<c:otherwise>
		<c:set var="showManufacturerLink" scope="page" value="false" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${not empty manufacturer.image.brand_logo.url}">
		<c:set var="hideLogo" scope="page" value=""></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="innerBoxMissingCtr" scope="page" value="${innerBoxMissingCtr + 1}"></c:set>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${not empty manufacturer.productGroups}">
		<c:set var="hideProductGroups" scope="page" value=""></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="manufacturerDetailsMissingCtr" scope="page" value="${manufacturerDetailsMissingCtr + 1}"></c:set>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${not empty manufacturer.phoneNumbers}">
		<c:set var="hidePhoneNumbers" scope="page" value=""></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="manufacturerDetailsMissingCtr" scope="page" value="${manufacturerDetailsMissingCtr + 1}"></c:set>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${not empty manufacturer.languages}">
		<c:set var="hideLanguages" scope="page" value=""></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="manufacturerDetailsMissingCtr" scope="page" value="${manufacturerDetailsMissingCtr + 1}"></c:set>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${not empty manufacturer.websites}">
		<c:set var="hideWebsites" scope="page" value=""></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="manufacturerDetailsMissingCtr" scope="page" value="${manufacturerDetailsMissingCtr + 1}"></c:set>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${manufacturerDetailsMissingCtr < 4}">
		<c:set var="hideManufacturerDetails" scope="page" value=""></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="innerBoxMissingCtr" scope="page" value="${innerBoxMissingCtr + 1}"></c:set>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${not empty manufacturer.emailAddresses}">
		<c:set var="hideManufacturerLinks" scope="page" value=""></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="innerBoxMissingCtr" scope="page" value="${innerBoxMissingCtr + 1}"></c:set>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${innerBoxMissingCtr < 3}">
		<c:set var="hideInnerBox" scope="page" value=""></c:set>
	</c:when>
</c:choose>
<c:choose>
	<c:when test="${not empty manufacturer.repairsText}">
		<c:set var="hideManufacturerRepairs" scope="page" value=""></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="outerBoxMissingCtr" scope="page" value="${innerBoxMissingCtr + 1}"></c:set>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${outerBoxMissingCtr < 4}">
		<c:set var="hideOuterBox" scope="page" value=""></c:set>
	</c:when>
</c:choose>
<%--End of filter out info blocks based on absence of info data--%>

<div class="outerBox ${hideOuterBox}">
    <c:if test="${siteUid ne 'distrelec_DE' and siteUid ne 'distrelec_IT' and siteUid ne 'distrelec_CH'}">
        <div class="innerBox row gu-16 ${hideInnerBox} banner-wrapper">
            <c:if test="${redirectedFromFilteredSearch}">
                <div id="zero-category-search" class="col-sm-12 banner-wrapper__noresult">
                        ${sCategoryNoResult}
                </div>
            </c:if>

            <a href="#productsCount" class="btn btn-primary">
                ${sShowProducts}<i class="icon-arrow"></i>
            </a>
            <div class="logo ${hideLogo}">
                <mod:product-manufacturer distManufacturer="${manufacturer}" showLink="false" showExternalLink="${showManufacturerLink}" />
            </div>
            <c:if test="${not empty metaData}">
                <mod:metadata-content metaData="${metaData}" />
            </c:if>
            <c:if test="${not empty manufacturer.webDescription}">
                <div class="content-text comment more" data-showmore="${sShowMore}" data-showless="${sShowLess}">${manufacturer.webDescription}</div>
            </c:if>
        </div>
    </c:if>
    <div class="row ${hideManufacturerRepairs} gu-14">
        <h3 class="base">${sRepairs}</h3>
        ${manufacturer.repairsText}
    </div>
</div>

