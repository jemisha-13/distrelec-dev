<%@ page trimDirectiveWhitespaces="true" contentType="text/html"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<br />
<c:if test="${addressType eq 'billing'}">
	<h2 class="head"><spring:message code="checkout.address.b2b.billing.addressMultiple" text="Select your Billing Address" /></h2>
</c:if>
<c:if test="${addressType eq 'shipping'}">
	<h2 class="head"><spring:message code="checkout.address.shipping.additionalAddresses.header" text="Select your Shipping Address" /></h2>
</c:if>


<c:choose>
	<c:when test="${isExportShop}">
		<c:set var="addressEditMode" value="${false}" />
	</c:when>
	<c:when test="${cartData.b2bCustomerData.customerType eq 'B2B_KEY_ACCOUNT'}">
		<c:set var="addressEditMode" value="${addressType eq 'shipping'}" />
	</c:when>
	<c:otherwise>
		<c:set var="addressEditMode" value="${true}" />
	</c:otherwise>
</c:choose>
<c:set var="addressCount" value="0"/>

<c:if test="${not empty addresses}"> 
<c:set var="addressCount" value="${fn:length(addresses)}"/>
	<c:forEach var="address" items="${addresses}">
		<mod:address template="${addressType}-${cartData.b2bCustomerData.customerType eq 'B2B_KEY_ACCOUNT' or cartData.b2bCustomerData.customerType eq 'B2B' ? 'b2b' : 'b2c'}" 
			address="${address}" 
			addressCount="${addressCount}"
			customerType="${cartData.b2bCustomerData.customerType}"
			addressEditMode="${cartData.b2bCustomerData.customerType eq 'B2B_KEY_ACCOUNT' && address.billingAddress ? false : addressEditMode}" 
		/>			
	</c:forEach>
</c:if>
