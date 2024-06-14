<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- Get Logged in Status --%>
<c:set var="isLoggedin" value="n" />
<sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')">
	<c:set var="isLoggedin" value="y" />
</sec:authorize>

<%-- Create Breadcrumb string --%>
<c:set var="breadcrumb" value="" />
<c:forEach items="${breadcrumbs}" var="breadcrumbItem" varStatus="status" >
	<c:set var="breadcrumb" value="${status.first ? '' : breadcrumb}/${breadcrumbItem.name}" />
</c:forEach>
<c:set var ="category">
	${fn:escapeXml(fn:replace(breadcrumb, "'", "\\'"))}
</c:set>

<%-- Format Total Cart Price Value --%>
<c:choose>
	<c:when test="${empty cartData.subTotal.value}">
		<c:set var="totalCartValue" value="" />
	</c:when>
	<c:otherwise>
		<c:set var="totalCartValue" value="${namicscommerce:formatAnalyticsPrice(cartData.subTotal)}" />
	</c:otherwise>
</c:choose>

<%-- Format Product Price Value --%>
<c:choose>
	<c:when test="${empty product.price.value}">
		<c:set var="productPrice" value="" />
	</c:when>
	<c:otherwise>
		<c:set var="productPrice" value="${namicscommerce:formatAnalyticsPrice(product.price)}" />
	</c:otherwise>
</c:choose>

<c:set var="productId" value="${product.codeErpRelevant}" />
<c:if test="${empty productId}">
	<c:set var="productId" value="" />
</c:if>

<%-- Google Code for Remarketing Tag --%>
<script type="text/javascript">
	var google_tag_params = {
		ecomm_prodid: '${productId}',
		ecomm_pagetype: '${pageType}',
		ecomm_totalvalue: '${totalCartValue}',
		ecomm_category: '${category}',
		ecomm_pvalue: '${productPrice}',
		ecomm_hasaccount: '${isLoggedin}'
	};
</script>
<script type="text/javascript">
	/* <![CDATA[ */
	var google_conversion_id = ${googleAdWordsConversionId};
	var google_custom_params = window.google_tag_params;
	var google_remarketing_only = true;
	/* ]]> */
</script>

<script type="text/javascript" src="//www.googleadservices.com/pagead/conversion.js"></script>

<noscript>
	<div style="display:inline;">
		<img height="1" width="1" style="border-style:none;" alt="" src="//googleads.g.doubleclick.net/pagead/viewthroughconversion/${googleAdWordsConversionId}/?value=0&amp;guid=ON&amp;script=0"/>
	</div>
</noscript>


