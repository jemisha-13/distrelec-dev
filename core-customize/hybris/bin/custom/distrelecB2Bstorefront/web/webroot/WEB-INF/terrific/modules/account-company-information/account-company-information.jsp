<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>



<spring:message code="companyinformation.companyName" var="sCompanyName" />
<spring:message code="companyinformation.companyName2" var="sCompanyName2" />
<spring:message code="companyinformation.vatId" var="sVatId" />
<spring:message code="companyinformation.department" var="sDepartment" />
<spring:message code="companyinformation.customerId" var="sCustomerId" />


<ul class="company-data cf">
	<li class="entry">
		<dl>
			<dt class="label">${sCompanyName}</dt>
			<dd class="value"><c:out value="${empty companyInformationAdr.companyName ?  companyName  : companyInformationAdr.companyName}" /></dd>
			<dd class="value"><c:out value="${empty companyInformationAdr.companyName2 ? companyName2 : companyInformationAdr.companyName2}" /></dd>
			<dd class="value"><c:out value="${empty companyInformationAdr.companyName3 ? companyName3 : companyInformationAdr.companyName3}" /></dd>
		</dl>
	</li>

	<c:if test="${currentCountry.isocode ne 'GB'}">
		<li class="entry">
			<dl>
				<dt class="label">${sVatId}</dt>
				<dd class="value">${companyInformationVatId}</dd>
			</dl>
		</li>
	</c:if>

	<%--
	<li class="entry">
		<dl>
			<dt class="label">${sDepartment}</dt>
			<dd class="value">${companyInformationAdr.department}</dd>
		</dl>
	</li>
	 --%>
	<li class="entry">
		<dl>
			<dt class="label">${sCustomerId}</dt>
			<dd class="value">${companyInformationCustomerId}</dd>
		</dl>
	</li>
</ul>
