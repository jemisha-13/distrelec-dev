<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<div class="bd">
	<div class="_left">
		<ul class="tools-bar">
			<c:if test="${not empty orderData.entries}">
				<mod:toolsitem template="toolsitem-print" skin="print skin-toolsitem-narrow" tag="li" htmlClasses="tooltip-hover" />
			</c:if>
		</ul>
	</div>
	<div class="_right">
		<ul class="tools-bar">
			<c:if test="${not empty orderData.entries}">
				<mod:toolsitem template="toolsitem-lbl-order-detail-shopping-bulk" skin="with-label skin-toolsitem-shopping-bulk skin-toolsitem-narrow" tag="li" />
			</c:if>
		</ul>
	</div>
</div>
