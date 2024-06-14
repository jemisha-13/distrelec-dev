<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="saveUrl" required="true" type="java.lang.String"%>
<%@ attribute name="cancelUrl" required="true" type="java.lang.String"%>
<%@ attribute name="b2BPermissionForm" required="true"
	type="com.namics.distrelec.b2b.storefront.forms.B2BPermissionForm"%>

<form:form action="${saveUrl}" id="b2BPermissionForm"
	modelAttribute="b2BPermissionForm" method="POST">
	<p class="required">
		<spring:theme code="form.required" text="Fields marked * are required" />
	</p>
	<dl>
		<form:input type="hidden" name="originalCode" path="originalCode"
			id="originalCode" />
		<form:input type="hidden" name="permissionType" path="permissionType"
			id="permissionType" />
		<form:input type="hidden" name="b2BPermissionTypeData.code"
			path="b2BPermissionTypeData.code" id="b2BPermissionTypeData.code" />
		<form:input type="hidden" name="b2BPermissionTypeData.name"
			path="b2BPermissionTypeData.name" id="b2BPermissionTypeData.name" />
		<p>
			<spring:theme code="text.company.managePermissions.type.label"
				text="Permission type" />
			: ${b2BPermissionForm.permissionType}
		</p>

		<formUtil:formInputBox
			idKey="text.company.managePermissions.name.label"
			labelKey="text.company.managePermissions.name.label" path="code"
			placeHolderKey="text.company.managePermissions.name.label.placeholder" inputCSS="text"/>
		<formUtil:formSelectBox
			idKey="text.company.managePermissions.unit.label"
			skipBlankMessageKey="text.company.costCenter.selectBox.unit"
			labelKey="text.company.managePermissions.unit.label"
			path="parentUnitName" mandatory="true" items="${b2bUnits}" />

		<c:if
			test="${b2BPermissionForm.b2BPermissionTypeData.code ne 'B2BBudgetExceededPermission' }">

			<c:if
				test="${b2BPermissionForm.b2BPermissionTypeData.code eq
				'B2BOrderThresholdTimespanPermission'}">

				<formUtil:formSelectBox
					idKey="text.company.managePermissions.timespan.label"
					skipBlankMessageKey="text.company.managePermissions.selectBox.timespan"
					labelKey="text.company.managePermissions.timespan.label"
					path="timeSpan" mandatory="true" items="${b2bPeriodRanges}" />
			</c:if>

			<formUtil:formSelectBox
				idKey="text.company.managePermissions.currency.label"
				skipBlankMessageKey="text.company.costCenter.selectBox.currency"
				labelKey="text.company.managePermissions.currency.label"
				path="currency" mandatory="true" items="${b2bCostCenterCurrencies}" />

			<formUtil:formInputBox
				idKey="text.company.managePermissions.value.label"
				labelKey="text.company.managePermissions.value.label" path="value"
				placeHolderKey="text.company.managePermissions.value.label.placeholder" inputCSS="text" mandatory="true" />
		</c:if>
	</dl>

	<ycommerce:testId code="Permission_Cancel_button">
		<a class="cancel_button" href="${cancelUrl}">
			<button type="button" class="form">
				<spring:theme
					code="text.company.managePermissions.edit.cancelButton"
					text="Cancel" />
			</button>
		</a>
	</ycommerce:testId>
	<ycommerce:testId code="Permission_Save_button">
		<button type="submit" class="form">
			<spring:theme code="text.company.managePermissions.edit.saveButton"
				text="Save" />
		</button>
	</ycommerce:testId>
</form:form>
