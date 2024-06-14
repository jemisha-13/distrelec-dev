<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<c:set var="cssErrorClass" value="" />
<c:set var="action" value="/list-filter/send" />
<spring:message code="text.store.dateformat.datepicker.selection" var="sDateFormat" />

<!-- List Filter !-->
<div class="form-box">
	<form>
		<div class="row base">
			<div class="gu-2">
				<label for="filter_invoicenr"><spring:message code="listfilter.invoiceNr" /></label>
			</div>
			<div class="gu-6">
				<input id="filter_invoicenr" name="filter_invoicenr" class="field" type="text">
			</div>
		</div>
		<div class="row base">
			<div class="gu-2">
				<label for="filter_ordernr"><spring:message code="listfilter.orderNr" /></label>
			</div>
			<div class="gu-6">
				<input id="filter_ordernr" name="filter_ordernr" class="field" type="text">
			</div>
		</div>
		<div class="row base">
			<div class="gu-2">
				<label for="filter_buyer"><spring:message code="listfilter.buyer" /></label>
			</div>
			<div class="gu-6">
				<input id="filter_buyer" name="filter_buyer" class="field" type="text">
			</div>
		</div>
		<div class="row base">
			<div class="gu-2">
				<label for="filter_invoicefrom"><spring:message code="listfilter.invoiceTotal" /></label>
			</div>
			<div class="gu-6">
				<div class="inline-fields currency-fields">
					<label for="filter_invoicefrom" class="from"><spring:message code="listfilter.from" /></label> <input id="filter_invoicefrom" name="filter_invoicefrom" class="field" type="text"> <span class="currency value">CHF</span>
					<label for="filter_invoiceto" class="to"><spring:message code="listfilter.to" /></label> <input id="filter_invoiceto" name="filter_invoiceto" class="field" type="text"> <span class="currency value">CHF</span>
				</div>
			</div>
		</div>
		<div class="row base">
			<div class="gu-2">
				<label for="filter_datefrom"><spring:message code="listfilter.date" /></label>
			</div>
			<div class="gu-6">
				<div class="inline-fields date-fields" data-format="${sDateFormat}">
					<label for="filter_datefrom" class="from"><spring:message code="listfilter.from" /></label> <input id="filter_datefrom" name="filter_datefrom" class="field" type="text"> <span class="calendar value"><i></i></span>
					<label for="filter_dateto" class="to"><spring:message code="listfilter.to" /></label> <input id="filter_dateto" name="filter_dateto" class="field" type="text"> <span class="calendar value"><i></i></span>
				</div>
			</div>
		</div>
		<div class="row base">
			<div class="gu-2">
				<label for="filter_name" class="required"><spring:message code="listfilter.name" /></label>
			</div>
			<div class="gu-6">
				<input id="filter_name" name="filter_name" class="field" type="text">
			</div>
		</div>
		<div class="row base">
			<div class="gu-2">
				<label for="filter_state" class="required"><spring:message code="listfilter.state" /></label>
			</div>
			<div class="gu-6">
				<select name="filter_state" id="filter_state" class="field">
					<option>User confirmation pending (before he has set a password)</option>
					<option>Active</option>
					<option>Deactivated (after deactivation by admin)</option>
				</select>
			</div>
		</div>
		<div class="actions">
			<button class="btn btn-secondary btn-reset" type="reset"><i></i><spring:message code="listfilter.buttonReset" /></button>
			<button class="btn btn-primary btn-search" type="submit"><i></i><spring:message code="listfilter.buttonSearch" /></button>
		</div>
	</form>
</div>
