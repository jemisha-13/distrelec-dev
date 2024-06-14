<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="isEProcurement" value="false"/>
<sec:authorize access="hasAnyRole('ROLE_EPROCUREMENTGROUP','ROLE_OCICUSTOMERGROUP','ROLE_ARIBACUSTOMERGROUP','ROLE_CXMLCUSTOMERGROUP')">
	<c:set var="isEProcurement" value="true"/>
</sec:authorize>


<div class="bd">
	<div class="border-container cf">
		<div class="_left">
			<ul>
				<c:if test="${not empty cartData.entries}">
					<mod:toolsitem template="toolsitem-print" skin="print" tag="li" htmlClasses="tooltip-hover" />
					<mod:toolsitem template="toolsitem-share-email-only" skin="share skin-toolsitem-share-email-only" tag="li" />
					<mod:toolsitem template="toolsitem-download" skin="download" tag="li" downloadUrl="/cart/download" />
				</c:if>

				<mod:toolsitem template="toolsitem-upload" skin="upload" tag="li" htmlClasses="tooltip-hover" />
			</ul>
		</div>
		<div class="_right">
			<c:if test="${not empty cartData.entries}">
				<ul>
					<c:if test="${not isEProcurement}">
						<mod:toolsitem template="toolsitem-lbl-shopping-bulk" skin="with-label skin-toolsitem-shopping-bulk big" tag="li" />
					</c:if>
					<mod:toolsitem template="toolsitem-lbl-empty-cart" skin="with-label skin-toolsitem-empty-cart big" tag="li" />
				</ul>
			</c:if>
		</div>
	</div>
</div>
