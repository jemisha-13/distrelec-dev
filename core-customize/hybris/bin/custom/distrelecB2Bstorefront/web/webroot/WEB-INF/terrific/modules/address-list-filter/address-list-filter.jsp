<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:message code="checkout.address.listFilter.label" text="Address List filter by" var="sLabelAddressListFilterByHidden" />
<spring:message code="checkout.address.listFilter.inputPlaceholder" text="Search by Name, Street, Town ect." var="sInputPlaceholder" />
<spring:message code="checkout.address.listFilter.noMatch" text="For the entered filter term, no Results were found." var="sNoMatch" />

<form:form action="#" method="GET">
	<div class="base">
		<label for="address-list-filter" class="vh">${sLabelAddressListFilterByHidden}</label>
		<input id="address-list-filter" class="field address-list-filter" type="text" value="" placeholder="${sInputPlaceholder}" name="addressListFilter">
		<span class="filter-icon search" ></span>
		<span class="filter-icon clear hidden" ></span>
	</div>
</form:form>
<p class="no-match hidden">${sNoMatch}</p>