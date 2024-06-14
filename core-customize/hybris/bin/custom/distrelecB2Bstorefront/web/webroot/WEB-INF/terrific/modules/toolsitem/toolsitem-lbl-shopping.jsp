<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="isEShopGroup" value="false"/>
<sec:authorize access="hasRole('ROLE_B2BEESHOPGROUP')">
	<c:set var="isEShopGroup" value="true"/>
</sec:authorize>

<%-- Label needs to be in a div to use the ellipsis class --%>
<a class="ico ico-list${isEShopGroup ? ' disabled' : ''}" title="<spring:message code='toolsitem.add.list' />" data-aainteraction="add to shopping list" data-location="${dataLocation}" data-product-reference="" data-product-code="${productId}" data-product-min-order-quantity="${productMinQuantity != null ? productMinQuantity : 1}">
	<span class="icon-holder">
		<i class="fas fa-list"></i>
	</span>
	<div class="text-holder"><spring:message code="cartTabs.title.shoppingList" /></div>
</a>