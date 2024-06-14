<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="saveUrl" required="true" type="java.lang.String"%>
<%@ attribute name="cancelUrl" required="true" type="java.lang.String"%>
<%@ attribute name="b2BBudgetForm" required="true"
	type="com.namics.distrelec.b2b.storefront.forms.B2BBudgetForm"%>

<form:form action="${saveUrl}" id="editB2bBudgetform"
	modelAttribute="b2BBudgetForm" method="POST">
	<p class="required">
		<spring:theme code="form.required" text="Fields marked * are required" />
	</p>
	<dl>
		<form:input type="hidden" name="originalCode" path="originalCode"
			id="originalCode" />

		<formUtil:formInputBox idKey="text.company.budget.budgetId"
			labelKey="text.company.budget.budgetId" path="code" inputCSS="text"
			placeHolderKey="text.company.budget.budgetId.placeholder" mandatory="true" />

		<formUtil:formInputBox idKey="text.company.budget.name"
			labelKey="text.company.budget.name" path="name" inputCSS="text"
			placeHolderKey="text.company.budget.name.placeholder" mandatory="true" />

		<formUtil:formSelectBox idKey="text.company.budget.unit"
			skipBlankMessageKey="text.company.costCenter.selectBox.unit"
			labelKey="text.company.costCenter.unit.title" path="parentB2BUnit"
			mandatory="true" items="${b2bUnits}" />

		<formUtil:formInputBox idKey="budgetStartDate"
			labelKey="text.company.budget.startDate" path="startDate"
			placeHolderKey="text.company.budget.startDate.placeholder" inputCSS="date text" mandatory="true" />

		<formUtil:formInputBox idKey="budgetEndDate"
			labelKey="text.company.budget.endDate" path="endDate" inputCSS="date text"
			placeHolderKey="text.company.budget.endDate.placeholder" mandatory="true" />


		<formUtil:formSelectBox idKey="text.company.budget.currency"
			skipBlankMessageKey="text.company.costCenter.selectBox.currency"
			labelKey="text.company.costCenter.currency.title" path="currency"
			mandatory="true" items="${b2bCostCenterCurrencies}" />


		<formUtil:formInputBox idKey="text.company.budget.amount"
			labelKey="text.company.budget.amount" path="budget" inputCSS="text"
			placeHolderKey="text.company.budget.amount.placeholder" mandatory="true" />

	</dl>
	<ycommerce:testId code="Budget_Cancel_button">
		<a class="cancel_button" href="${cancelUrl}">
			<button type="button" class="form">
				<spring:theme code="text.company.budget.cancelButton.displayName"
					text="Cancel" />
			</button>
		</a>
	</ycommerce:testId>
	<ycommerce:testId code="Budget_Save_button">
		<button type="submit" class="form" id="SubmitBudget_button">
			<spring:theme code="text.company.budget.saveButton.displayName"
				text="Save" />
		</button>
	</ycommerce:testId>
</form:form>
