<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<c:set var="cssErrorClass" value="" />
<c:set var="action" value="/list-filter/send" />

<c:set value="employeeSearchB2BForm" var="modelAttribute" />
<c:url value="/my-account/company/user-management" var="submitAction" />

<!-- User Management Overview !-->
<div class="form-box">
	<form:form modelAttribute="${modelAttribute}" action="${submitAction}" method="post">
		<form:hidden path="sort" />
		<div class="row base">
			<div class="gu-2">
				<formUtil:formLabel idKey="listfilter.name" labelKey="listfilter.name" path="name" mandatory="false"/>
			</div>
			<div class="gu-6">
				<formUtil:formInputBox idKey="listfilter.name" path="name" mandatory="false" placeHolderKey="listfilter.name.placeholder" />
			</div>
		</div>
		<div class="row base">
				<div class="gu-2">
					<formUtil:formLabel idKey="listfilter.state" labelKey="listfilter.state" path="stateCode" mandatory="false"/>
				</div>
				<div class="gu-6">
					<formUtil:formSelectBox idKey="listfilter.state" path="stateCode" mandatory="true" skipBlank="false" selectCSSClass="selectpicker" skipBlankMessageKey="form.select.empty" items="${filterStates}"/>
				</div>
		</div>
		<div class="actions">
			<button class="btn btn-secondary btn-reset" type="reset"><i></i><spring:message code="listfilter.buttonReset" /></button>
			<button class="btn btn-primary btn-search" type="submit"><i></i><spring:message code="listfilter.buttonSearch" /></button>
		</div>
	</form:form>
</div>
