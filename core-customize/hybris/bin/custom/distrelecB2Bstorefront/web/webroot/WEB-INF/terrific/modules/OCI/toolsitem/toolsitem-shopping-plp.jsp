<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="isEShopGroup" value="false"/>
<sec:authorize access="hasRole('ROLE_B2BEESHOPGROUP')">
	<c:set var="isEShopGroup" value="true"/>
</sec:authorize>

<c:set var="isEProcurement" value="false"/>
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<c:set var="isEProcurement" value="true"/>
</sec:authorize>

<a class="ico ico-list${isEShopGroup ? ' disabled' : ''} ${isEProcurement ? ' hidden' : ''}" title="<spring:message code='toolsitem.add.list' />"
	data-product-code="${productId}" 
	data-product-reference=""
	data-aainteraction="add to shopping list"
	data-position="${position}"
   	data-product-min-order-quantity="${productMinQuantity != null ? productMinQuantity : 1}">
	<i class="far fa-file-alt"></i>
	<spring:message code='toolsitem.add.list' />
</a>
 
