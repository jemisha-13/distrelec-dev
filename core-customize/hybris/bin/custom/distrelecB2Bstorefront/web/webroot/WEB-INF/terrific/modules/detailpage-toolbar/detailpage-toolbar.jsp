<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<spring:theme code="toolsitem.share" var="sToolsitemShare" />

<%-- twitter / gplus is not available for oci / ariba as of per ticket DISTRELEC-5540 --%>
<sec:authorize access="!hasRole('ROLE_OCICUSTOMERGROUP') and !hasRole('ROLE_ARIBACUSTOMERGROUP') and !hasRole('ROLE_CXMLCUSTOMERGROUP')">
	<c:set var="enableSharingFunctions" value="true" />
</sec:authorize>

<c:set var="isEProcurement" value="false"/>
<sec:authorize access="hasAnyRole('ROLE_EPROCUREMENTGROUP','ROLE_OCICUSTOMERGROUP','ROLE_ARIBACUSTOMERGROUP','ROLE_CXMLCUSTOMERGROUP')">
	<c:set var="isEProcurement" value="true"/>
</sec:authorize>

<input type="hidden" class="hidden-product-code-erp" value="${product.codeErpRelevant}" />

<div class="social-buttons">
	<ul>
		<c:if test="${not product.catPlusItem}">
			<c:if test="${not isEProcurement}">
				<mod:toolsitem template="toolsitem-lbl-shopping" skin="without-label skin-toolsitem-shopping" tag="li" dataLocation="pdp" productId="${product.code}" productMinQuantity="${product.orderQuantityMinimum}" htmlClasses="big" />
			</c:if>	
			<mod:toolsitem template="toolsitem-lbl-compare" skin="without-label skin-toolsitem-compare" tag="li" dataLocation="pdp" productId="${product.code}" htmlClasses="big" />
		</c:if>	
	</ul>
</div>
