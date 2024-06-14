<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="isEProcurement" value="false"/>
<sec:authorize access="hasAnyRole('ROLE_EPROCUREMENTGROUP','ROLE_OCICUSTOMERGROUP','ROLE_ARIBACUSTOMERGROUP','ROLE_CXMLCUSTOMERGROUP')">
	<c:set var="isEProcurement" value="true"/>
</sec:authorize>

<ul class="tools-bar" data-product-id="${productId}" >
	<mod:toolsitem template="toolsitem-compare-plp" skin="compare" tag="li" productId="${productId}" htmlClasses="tabular skin-toolsitem-compare-plp" positionIndex="${positionIndex}"/>
	<mod:toolsitem template="toolsitem-shopping-plp" skin="shopping" tag="li" productId="${productId}" productMinQuantity="${productOrderQuantityMinimum}" positionIndex="${positionIndex}"/>
</ul>
