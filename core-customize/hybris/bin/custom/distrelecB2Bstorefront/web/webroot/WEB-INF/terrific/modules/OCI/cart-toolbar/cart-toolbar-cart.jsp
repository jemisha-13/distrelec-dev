<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="isEProcurement" value="false"/>
<sec:authorize access="hasAnyRole('ROLE_EPROCUREMENTGROUP','ROLE_OCICUSTOMERGROUP','ROLE_ARIBACUSTOMERGROUP','ROLE_CXMLCUSTOMERGROUP')">
	<c:set var="isEProcurement" value="true"/>
</sec:authorize>


<div class="skin-cart-toolbar-cart__holder">
	<div class="skin-cart-toolbar-cart__holder__left">
		<mod:toolsitem template="toolsitem-lbl-empty-cart" skin="with-label skin-toolsitem-empty-cart big" tag="div" />
	</div>
	<div class="skin-cart-toolbar-cart__holder__right">
		<c:if test="${not empty cartData.entries}">
			<c:if test="${not empty cartData.entries}">
				<mod:toolsitem template="toolsitem-print-cart" skin="print" tag="div" htmlClasses="skin-toolsitem-print-cart tooltip-hover" />
				<mod:toolsitem template="toolsitem-share-email-only-cart" skin="share skin-toolsitem-share-email-only" htmlClasses="skin-toolsitem-share-email-only-cart" tag="div" />
				<mod:toolsitem template="toolsitem-download-cart" skin="download-cart" htmlClasses="skin-toolsitem-download-cart" tag="div" downloadUrl="/cart/download" />
			</c:if>

			<c:if test="${not isEProcurement}">
				<mod:toolsitem template="toolsitem-lbl-shopping-bulk-cart" htmlClasses="skin-toolsitem-lbl-shopping-bulk-cart" skin="with-label skin-toolsitem-shopping-bulk big" tag="div" />
			</c:if>
		</c:if>
	</div>
</div>
