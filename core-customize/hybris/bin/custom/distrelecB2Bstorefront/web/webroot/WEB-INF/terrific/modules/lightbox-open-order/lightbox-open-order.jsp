<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<spring:message code="text.store.dateformat" var="datePattern" />

<spring:theme code="lightbox.openOrder.editSettings.button.cancel" var="sCancel" />
<spring:theme code="lightbox.openOrder.editSettings.button.save" var="sSubmit" />

<%-- <c:set var="isCreatorOrAdminUser" value="true" /> --%>

<c:set var="openOrderSelectedClosingDate " value="${cartData != null ? cartData.openOrderSelectedClosingDate   : orderData.openOrderSelectedClosingDate  }"/>
<c:set var="openOrderClosingDates" value="${cartData != null ? cartData.openOrderClosingDates  : orderData.openOrderClosingDates }"/>
<c:set var="openOrderEditableForAllContacts" value="${cartData != null ? cartData.openOrderEditableForAllContacts  : orderData.openOrderEditableForAllContacts }"/>
<c:set var="openOrderSelectedClosingDateFormatted">
	<fmt:formatDate value="${openOrderSelectedClosingDate}" dateStyle="short" timeStyle="short" type="date" pattern="${datePattern}" />
</c:set>

<div class="modal" tabindex="-1">
	<div class="hd">
		<div class="-left">
			<h3 class="title"><spring:theme code="lightbox.openOrder.editSettings.title" /></h3>
		</div>
		<div class="-right">
			<a class="btn btn-close" href="#" data-dismiss="modal" aria-hidden="true"><spring:theme code="lightbox.openOrder.editSettings.button.close" /></a>
		</div>
	</div>
	<div class="bd">
		<p><spring:theme code="lightbox.openOrder.editSettings.intro" /></p>
		
		<form>
			<div class="dropdown-wrapper">
				<label for="openOrderDate"><spring:message code="checkout.address.openOrder.newOpenOrder.dates.label"/></label>
				<select id="openOrderDate" class="field selectpicker validate-dropdown select-open-order-date" name="openOrderDate" >
					<c:forEach items="${openOrderClosingDates}" var="closeDateOption">
						<c:set var="closeDateOptionFormatted">
							<fmt:formatDate value="${closeDateOption}" dateStyle="short" timeStyle="short" type="date" pattern="${datePattern}" />
						</c:set>
						<option value="${closeDateOptionFormatted}" title="${closeDateOptionFormatted}" ${openOrderSelectedClosingDateFormatted == closeDateOptionFormatted ? 'selected="selected"' : '' }>${closeDateOptionFormatted}</option>
					</c:forEach>
				</select>
			</div>
			<c:choose>
				<c:when test="${isOrderEditable}">
					<div class="checkbox-wrapper">
						<input id="isEditableByAllUsers" type="checkbox" class="checkbox all-users-can-edit" name="checkboxes" value="${openOrderEditableForAllContacts}" />
						<label for="isEditableByAllUsers"><spring:message code="checkout.address.openOrder.newOpenOrder.editableByAll.label"/></label>
					</div>
				</c:when>
				<c:otherwise>
					<div class="checkbox-wrapper">
						<input id="isEditableByAllUsers" type="checkbox" disabled="disabled" class="checkbox all-users-can-edit disabled" name="checkboxes" value="${openOrderEditableForAllContacts}" />
						<label for="isEditableByAllUsers" class="disabled" disabled="disabled" ><spring:message code="checkout.address.openOrder.newOpenOrder.editableByAll.label"/></label>
					</div>
				</c:otherwise>
			</c:choose>
		</form>
	</div>
	 <div class="ft">
		 <input type="reset" class="btn btn-secondary btn-cancel" value="${sCancel}" />
		 <input type="submit" class="btn btn-primary btn-submit" value="${sSubmit}" />
	</div>
</div>
