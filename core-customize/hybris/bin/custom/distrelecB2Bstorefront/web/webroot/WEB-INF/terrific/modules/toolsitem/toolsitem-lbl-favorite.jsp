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
<a class="ico ico-fav${isEShopGroup ? ' disabled' : ''}" title="<spring:message code='toolsitem.mark.favorite' />" data-product-code="${productId}" data-aainteraction="add to favourites" data-location="${dataLocation}">
	<span class="icon-holder">
		<i class="far fa-heart" aria-hidden="true"></i>
	</span>
	<div class="text-holder"><spring:message code="toolsitem.mark.favorite" /></div>
</a>