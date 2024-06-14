<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<c:set var="cssErrorClass" value="" />
<c:set var="action" value="/list-filter/send" />
<spring:message code="listfilter.buttonReset" var="sReset" />
<spring:message code="listfilter.OrderReference" var="sOrderReference" />
<spring:message code="listfilter.articleNumber" var="sArticleNumber" />
<spring:message code="listfilter.invoiceDate" var="sInvoiceDate" />
<spring:message code="accountlist.invoiceHistoryList.DueDate" var="sDueDate" />
<spring:message code="invoicetext.store.dateformat.datepicker.selection" var="sDateFormat" />

<%-- List Filter --%>
<div class="form-box">
	<form:form modelAttribute="invoiceHistoryForm" action="/my-account/invoice-history" method="POST">
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
		<%-- Hiding order reference since SAP hasn't supplied the work yet [DISTRELEC-13585]
            <div class="row base">
                <div class="gu-2">
                    <label for="filter_ordernf">${sOrderReference}</label>
                </div>
                <div class="gu-6">
                    <form:input id="filter_ordernf" path="ordernf" type="text" value="" cssClass="field"/>
                </div>
            </div>
             --%>
		<div class="row base">
			<div class="gu-2">
				<label for="filter_ordernan">${sArticleNumber}</label>
			</div>
			<div class="gu-6">
				<form:input id="filter_ordernan" path="articleNumber" type="text" value="" cssClass="field"/>
			</div>
		</div>
		<div class="row base">
			<div class="gu-2">
				<label for="filter_datefrom">${sInvoiceDate}</label>
			</div>
			<div class="gu-6">
				<div class="inline-fields date-fields" data-format="${sDateFormat}" data-conversion-format="${conversionFormat}">
					<label for="filter_datefrom" class="from"><spring:message code="listfilter.from" /></label><form:input id="filter_datefrom" path="fromDate" type="text" value="${fromDate}" cssClass="field" /> <span class="calendar value"><i></i></span>
					<label for="filter_dateto" class="to"><spring:message code="listfilter.to" /></label><form:input id="filter_dateto" path="toDate" type="text" value="${toDate}" cssClass="field" /> <span class="calendar value"><i></i></span>
				</div>
			</div>
		</div>
		<div class="row base">
			<div class="gu-2">
				<label for="filter_duedatefrom">${sDueDate}</label>
			</div>
			<div class="gu-6">
				<div class="inline-fields date-fields" data-format="${sDateFormat}">
					<label for="filter_duedatefrom" class="from"><spring:message code="listfilter.from" /></label><form:input id="filter_duedatefrom" path="fromDueDate" type="text" value="${fromDueDate}" cssClass="field" /> <span class="calendar value"><i></i></span>
					<label for="filter_duedateto" class="to"><spring:message code="listfilter.to" /></label><form:input id="filter_duedateto" path="toDueDate" type="text" value="${toDueDate}" cssClass="field" /> <span class="calendar value"><i></i></span>
				</div>
			</div>
		</div>
		<div class="row base">
			<div class="gu-2">
				<label for="filter_totalMin"><spring:message code="listfilter.invoiceTotal" /></label>
			</div>
			<div class="gu-6">
				<div class="inline-fields range-fields">
					<label for="filter_totalMin" class="from"><spring:message code="listfilter.from" /></label><form:input id="filter_totalMin" path="minTotal" type="text" value="${minTotal}" cssClass="field" size="6"/>
					<label for="filter_totalMax" class="to"><spring:message code="listfilter.to" /></label><form:input id="filter_totalMax" path="maxTotal" type="text" value="${maxTotal}" cssClass="field" size="6"/>
				</div>
			</div>
		</div>
		<c:if test="${not empty contactsOfCustomer}">
			<div class="row base">
				<div class="gu-2">
					<label for="filter_orderedBy"><spring:message code="listfilter.orderedBy" /></label>
				</div>
				<div class="gu-6">
					<formUtil:formSelectBox idKey="filter_orderedBy" path="contactId" selectCSSClass="field" items="${contactsOfCustomer}" itemValue="contactId" itemLabel="name" skipBlankSelectable="true" />
				</div>
			</div>
		</c:if>
		<div class="row base">
			<div class="gu-2">
				<label for="filter_state" class="required"><spring:message code="listfilter.state" /></label>
			</div>
			<div class="gu-6">
				<form:select path="status" cssClass="field">
					<option value="ALL" ${'ALL' eq invoiceHistoryForm.status ? 'selected' : ''}><spring:message code="listfilter.invoiceStatus.ALL" /></option>
					<c:forEach items="${stateList}" var="stateItem">
						<option value="${stateItem}" ${stateItem eq invoiceHistoryForm.status ? 'selected' : ''}><spring:message code="listfilter.invoiceStatus.${stateItem}" /></option>
					</c:forEach>
				</form:select>
			</div>
		</div>
		<div class="actions">
			<form:hidden path="sort" value="${sortCode}" />
			<form:hidden path="page" value="0" />
			<form:hidden path="pageSize" value="${pageSize}" />
			<input class="btn btn-secondary btn-reset" type="reset" value="${sReset}" />
			<button class="btn btn-primary btn-search" type="submit"><i></i><spring:message code="listfilter.buttonSearch" /></button>
		</div>
	</form:form>
</div>
