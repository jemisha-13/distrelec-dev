<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
    <c:set var="isOCI" value="true" />
</sec:authorize>

<c:choose>
	<c:when test="${component.navigationType.code eq 'MAIN_NAV'}">
		<mod:mainnav tag="nav"/>
	</c:when>
	<c:when test="${component.navigationType.code eq 'MANUFACTURER_NAV' && isOCI eq false}">
		<mod:mainmanufacturernav tag="nav"/>
	</c:when>
	<c:when test="${component.navigationType.code eq 'CATEGORY_NAV'}">
		<mod:maincategorynav tag="nav"/>
	</c:when>
	<c:otherwise>
		<!-- Nothing TODO -->
	</c:otherwise>
</c:choose>
