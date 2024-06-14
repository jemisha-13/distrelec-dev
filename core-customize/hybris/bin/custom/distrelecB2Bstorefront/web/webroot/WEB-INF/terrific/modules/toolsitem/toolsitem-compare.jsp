<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="isEProcurement" value="false"/>
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<c:set var="isEProcurement" value="true"/>
</sec:authorize>

<a class="ico ico-compare" title="<spring:message code='toolsitem.compare' />" data-product-code="${productId}"><i></i></a>
