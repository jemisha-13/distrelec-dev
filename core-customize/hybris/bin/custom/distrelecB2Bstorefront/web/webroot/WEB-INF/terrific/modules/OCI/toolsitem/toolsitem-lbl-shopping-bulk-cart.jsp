<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="isEShopGroup" value="false"/>
<sec:authorize access="hasRole('ROLE_B2BEESHOPGROUP')">
	<c:set var="isEShopGroup" value="true"/>
</sec:authorize>

<a data-aainteraction="add to shopping list" class="ico ico-list${isEShopGroup ? ' disabled' : ''}" title="<spring:message code='toolsitem.save.cart' />">
	<span class="${isEShopGroup ? 'disabled' : ''}"><spring:message code="toolsitem.save.cartList" /></span>
</a>

