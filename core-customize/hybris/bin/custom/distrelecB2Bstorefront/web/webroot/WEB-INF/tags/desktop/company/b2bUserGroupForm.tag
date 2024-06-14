<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ attribute name="b2BUserGroupForm" required="true"
			  type="com.namics.distrelec.b2b.storefront.forms.B2BUserGroupForm" %>
<%@ attribute name="formUrl" required="true"
			  type="java.lang.String" %>
<%@ attribute name="cancelUrl" required="true"
			  type="java.lang.String" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>

<p>
	<spring:theme code="text.company.manageUsergroup.editForm"
				  text="Please use this form to update usergroup details"/>
</p>

<p class="required">
	<spring:theme code="form.required" text="Fields marked * are required"/>
</p>
<form:form action="${formUrl}" method="post" modelAttribute="b2BUserGroupForm">
	<dl>
		<form:input type="hidden" name="originalUid" path="originalUid" id="originalUid"/>
		<formUtil:formInputBox idKey="b2busergroup.id" labelKey="b2busergroup.id"
							   path="uid" inputCSS="text" mandatory="true"
							   placeHolderKey="b2busergroup.id.placeholder"/>
		<formUtil:formInputBox idKey="b2busergroup.name" labelKey="b2busergroup.name"
							   path="name" inputCSS="text" mandatory="true"
							   placeHolderKey="b2busergroup.name.placeholder"/>
		<formUtil:formSelectBox idKey="b2busergroup.parent" labelKey="b2busergroup.parent"
								path="parentUnit"
								mandatory="false" skipBlank="false"
								skipBlankMessageKey="form.select.empty"
								items="${branchSelectOptions}"/>
	</dl>
							
								<a class="cancel_button" href="${cancelUrl}">
									<button type="button" class="form">
										<spring:theme code="b2busergroup.cancel" text="Cancel"/>
									</button>
								</a>
								<ycommerce:testId code="Unit_SaveChange_button">
									<button type="submit" class="form">
										<spring:theme code="b2busergroup.save" text="Save Updates"/>
									</button>
								</ycommerce:testId>
							</span>
</form:form>
