<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="saveUrl" required="true" type="java.lang.String"%>
<%@ attribute name="cancelUrl" required="true" type="java.lang.String"%>
<%@ attribute name="b2BCostCenterForm" required="true"
	type="com.namics.distrelec.b2b.storefront.forms.B2BCostCenterForm"%>

<form:form action="${saveUrl}" id="b2BCostCenterform"
	modelAttribute="b2BCostCenterForm" method="POST">
	<p class="required">
		<spring:theme code="form.required" text="Fields marked * are required" />
	</p>
	<dl>
		<form:input type="hidden" name="originalCode" path="originalCode"
			id="originalCode" />
		<formUtil:formInputBox idKey="text.company.costCenter.id.title"
			labelKey="text.company.costCenter.id.title" path="code"
			inputCSS="text" placeHolderKey="text.company.costCenter.id.title.placeholder" mandatory="true" />
		<formUtil:formInputBox idKey="text.company.costCenter.name.title"
			labelKey="text.company.costCenter.name.title" path="name"
			inputCSS="text" placeHolderKey="text.company.costCenter.name.title.placeholder" mandatory="true" />
		<formUtil:formSelectBox idKey="text.company.costCenter.unit.title"
			skipBlank="true" labelKey="text.company.costCenter.unit.title"
			path="parentB2BUnit" mandatory="true" items="${b2bUnits}" />
		<formUtil:formSelectBox idKey="text.company.costCenter.currency.title"
			skipBlank="true" labelKey="text.company.costCenter.currency.title"
			path="currency" mandatory="true" items="${b2bCostCenterCurrencies}" />
	</dl>
	<ycommerce:testId code="CC_cancel">
		<a class="cancel_button" href="${cancelUrl}"><button type="button"
				class="form">
				<spring:theme code="text.company.costCenter.cancelButton.displayName" />
		</button> </a>
	</ycommerce:testId>	
	<ycommerce:testId code="CC_save">
		<button type="submit" class="form">
			<spring:theme code="text.company.costCenter.saveButton.displayName" />
		</button>
	</ycommerce:testId>	
</form:form>