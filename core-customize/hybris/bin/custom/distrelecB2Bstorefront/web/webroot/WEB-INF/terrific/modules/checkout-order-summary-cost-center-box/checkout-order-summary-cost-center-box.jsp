<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:message code="checkoutordersummarycostcenterbox.label" var="sMessage"/>
<spring:message code="checkoutordersummarycostcenterbox.title" var="sTitle"/>
<spring:message code="checkoutordersummarycostcenterbox.costCenter" var="sCostCenter"/>
<spring:message code="checkoutordersummarycostcenterbox.projectNumber" var="sProjectNumber"/>
<spring:message code="checkoutordersummarycostcenterbox.orderWarning" var="sOrderWarning"/>
<spring:message code="checkoutordersummarycostcenterbox.help" var="sHelpText"/>

<div class="title">
	<h4>${sOrderWarning}</h4>
</div>
<div class="field">
	<c:choose>
		<c:when test="${currentSalesOrg.erpSystem eq 'MOVEX'}">
			<input id="costcenter" name="costcenter" class="field" tabindex="1" maxlength="12" type="text" />
		</c:when>
		<c:otherwise>
			<input id="projectnumber" name="projectnumber" class="field cost-center" tabindex="2" maxlength="35" type="text" value="<c:out value='${projectNumber}' />">
		</c:otherwise>
	</c:choose>
</div>
<div class="info">
	<i class="fa fa-info-circle">&nbsp;</i>
	<span class="info__text">${sHelpText}</span>
</div>
