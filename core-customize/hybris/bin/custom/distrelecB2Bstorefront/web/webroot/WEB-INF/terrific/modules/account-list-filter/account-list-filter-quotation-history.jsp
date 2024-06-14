<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<c:set var="cssErrorClass" value="" />
<c:set var="action" value="/list-filter/send" />
<spring:message code="listfilter.buttonReset" var="sReset" />
<spring:message code="text.store.dateformat.datepicker.selection" var="sDateFormat" />

<%-- List Filter --%>
<div class="form-box">
	<form:form modelAttribute="quotationHistoryForm" action="/my-account/quote-history" method="POST">
		<div class="row base">
			<div class="gu-2">
				<label for="filter_quotationnr"><spring:message code="listfilter.quotationNr" /></label>
			</div>
			<div class="gu-6">
				<form:input id="filter_quotationnr" path="quotationId" type="text" value="${quotationId}" cssClass="field"/>				
			</div>
		</div>
		<div class="row base">
			<div class="gu-2">
				<label for="filter_articleNumber"><spring:message code="listfilter.articleNumber" /></label>
			</div>
			<div class="gu-6">
				<form:input id="filter_articleNumber" path="articleNumber" type="text" value="${articleNumber}" cssClass="field"/>				
			</div>
		</div>
		<div class="row base">
			<div class="gu-2">
				<label for="filter_datefrom"><spring:message code="listfilter.requestDate" /></label>
			</div>
			<div class="gu-6">
				<div class="inline-fields date-fields" data-format="${sDateFormat}">
					<label for="filter_datefrom" class="from"><spring:message code="listfilter.from" /></label><form:input id="filter_datefrom" path="fromDate" type="text" value="${fromDate}" cssClass="field" /> <span class="calendar value"><i></i></span>
					<label for="filter_dateto" class="to"><spring:message code="listfilter.to" /></label><form:input id="filter_dateto" path="toDate" type="text" value="${toDate}" cssClass="field" /> <span class="calendar value"><i></i></span>
				</div>
			</div>
		</div>
		<div class="row base">
			<div class="gu-2">
				<label for="filter_expiry_datefrom"><spring:message code="listfilter.expiryDate" /></label>
			</div>
			<div class="gu-6">
				<div class="inline-fields date-fields" data-format="${sDateFormat}">
					<label for="filter_expiry_datefrom" class="from"><spring:message code="listfilter.from" /></label><form:input id="filter_expiry_datefrom" path="expiryFromDate" type="text" value="${expiryFromDate}" cssClass="field" /> <span class="calendar value"><i></i></span>
					<label for="filter_expiry_dateto" class="to"><spring:message code="listfilter.to" /></label><form:input id="filter_expiry_dateto" path="expiryToDate" type="text" value="${expiryToDate}" cssClass="field" /> <span class="calendar value"><i></i></span>
				</div>
			</div>
		</div>
		<div class="row base">
			<div class="gu-2">
				<label for="filter_totalMin"><spring:message code="listfilter.quotationTotal" /></label>
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
					<label for="filter_orderedBy"><spring:message code="listfilter.requestedBy" /></label>
				</div>
				<div class="gu-6">
					<formUtil:formSelectBox idKey="filter_orderedBy" path="contactId" selectCSSClass="field" items="${contactsOfCustomer}" itemValue="contactId" itemLabel="name" skipBlankSelectable="${true}" />
				</div>
			</div>
		</c:if>
		<div class="row base">
			<div class="gu-2">
				<label for="filter_state" class="required"><spring:message code="listfilter.state" /></label>
			</div>
			<div class="gu-6">
				<form:select path="status" cssClass="field">
					<option value="ALL" ${'ALL' eq quotationHistoryForm.status ? 'selected' : ''}><spring:message code="listfilter.quotationStatus.ALL" /></option>
					<c:forEach items="${stateList}" var="stateItem">
						<c:if test="${stateItem.code != '05'}">
							<option value="${stateItem.code}" ${stateItem.code eq quotationHistoryForm.status ? 'selected' : ''}>${stateItem.name}</option>
						</c:if>
					</c:forEach>
				</form:select>
			</div>
		</div>
		<div class="actions">
			<form:hidden path="sort" value="${sortCode}" />
			<form:hidden path="page" value="1" />
			<form:hidden path="pageSize" value="${pageSize}" />
			<input class="btn btn-secondary btn-reset" type="reset" value="${sReset}" />
			<button class="btn btn-primary btn-search" type="submit"><i></i><spring:message code="listfilter.buttonSearch" /></button>
		</div>
	</form:form>
</div>