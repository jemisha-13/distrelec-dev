<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<c:set var="cssErrorClass" value="" />
<c:set var="action" value="/list-filter/send" />
<spring:message code="listfilter.buttonReset" var="sReset" />
<spring:message code="text.store.dateformat.datepicker.selection" var="sDateFormat" />
<spring:message code="listfilter.from" var="sListfilterFrom" />
<spring:message code="listfilter.to" var="sListfilterTo" />

<%-- List Filter --%>
<div class="form-box">
	<form:form modelAttribute="orderHistoryForm" action="/my-account/order-history" method="POST">
		<div class="form-box__item">
			<div class="row base">
				<div class="gu-2">
					<label for="filter_ordernr"><spring:message code="listfilter.orderNr" /></label>
				</div>
				<div class="gu-6">
					<form:input id="filter_ordernr" path="orderNumber" type="text" value="${orderNumber}" cssClass="field"/>
				</div>
			</div>
			<div class="row base">
				<div class="gu-2">
					<label for="filter_invoicenr"><spring:message code="listfilter.invoiceNr" /></label>
				</div>
				<div class="gu-6">
					<form:input id="filter_invoicenr" path="invoiceNumber" type="text" value="${invoiceNumber}" cssClass="field"/>
				</div>
			</div>
			<div class="row base">
				<div class="gu-2 label_filter_div">
					<label class="label_filter_reference" for="filter_reference"><spring:message code="listfilter.reference" /></label>
				</div>
				<div class="gu-6">
					<form:input id="filter_reference" path="reference" type="text" value="${reference}" cssClass="field"/>
				</div>
			</div>
			<div class="row base">
				<div class="gu-2 label_filter_div">
					<label class="" for="filter_product_name">  <spring:message code="productinfo.articlenumber" /> </label>
				</div>
				<div class="gu-6">
					<form:input id="filter_product_name" path="productNumber" type="text" value="${productNumber}" cssClass="field"/>
				</div>
			</div>
		</div>
		<div class="form-box__item form-box__item--second">
			<div class="row base">
				<div class="gu-2">
					<label for="filter_datefrom"><spring:message code="listfilter.date" /></label>
				</div>
				<div class="gu-6">
					<div class="inline-fields date-fields" data-format="${sDateFormat}">
						<label for="filter_datefrom" class="from hidden">${sListfilterFrom}</label><form:input id="filter_datefrom" path="fromDate" type="text" value="${fromDate}" cssClass="field" placeholder="${sListfilterFrom}" /> <span class="calendar value"><i></i></span>
						<label for="filter_dateto" class="to hidden">${sListfilterTo}</label><form:input id="filter_dateto" path="toDate" type="text" value="${toDate}" cssClass="field" placeholder="${sListfilterTo}" /> <span class="calendar value"><i></i></span>
					</div>
				</div>
			</div>
			<div class="row base">
				<div class="gu-2">
					<label for="filter_totalMin"><spring:message code="listfilter.orderTotal" /></label>
				</div>
				<div class="gu-6">
					<div class="inline-fields range-fields">
						<label for="filter_totalMin" class="from hidden">${sListfilterFrom}</label><form:input id="filter_totalMin" path="minTotal" type="text" value="${minTotal}" cssClass="field half" size="6" placeholder="${sListfilterFrom}"/>
						<label for="filter_totalMax" class="to hidden">${sListfilterTo}</label><form:input id="filter_totalMax" path="maxTotal" type="text" value="${maxTotal}" cssClass="field half" size="6" placeholder="${sListfilterTo}"/>
					</div>
				</div>
			</div>
			<c:if test="${not empty contactsOfCustomer}">
				<div class="row base">
					<div class="gu-2">
						<label for="filter_orderedBy"><spring:message code="listfilter.orderedBy" /></label>
					</div>
					<div class="gu-6">
						<formUtil:formSelectBox idKey="filter_orderedBy" path="filterContactId" selectCSSClass="field" items="${contactsOfCustomer}" itemValue="contactId" itemLabel="name" skipBlankSelectable="${true}" />
					</div>
				</div>
			</c:if>
			<div class="row base">
				<div class="gu-2">
					<label for="filter_state" class="required"><spring:message code="listfilter.state" /></label>
				</div>
				<div class="gu-6">
					<form:select id="filter_state" path="status" cssClass="field">
						<option value="ALL" ${'ALL' eq orderHistoryForm.status ? 'selected' : ''}><spring:message code="listfilter.orderStatus.ALL" /></option>
						<c:forEach items="${stateList}" var="stateItem">
							<option value="${stateItem.code}" ${stateItem.code eq orderHistoryForm.status ? 'selected' : ''}><spring:message code="listfilter.orderStatus.${stateItem.code}" /></option>
						</c:forEach>
					</form:select>
				</div>
			</div>
            <div class="actions">
                <form:hidden path="sort" value="${sortCode}" />
                <form:hidden path="page" value="0" />
                <form:hidden path="pageSize" value="${pageSize}" />
				<button class="btn btn-primary btn-search" type="submit"><i></i><spring:message code="listfilter.buttonSearch" /></button>
                <input class="btn btn-secondary btn-reset" type="reset" value="${sReset}" />
            </div>
		</div>
	</form:form>
</div>
