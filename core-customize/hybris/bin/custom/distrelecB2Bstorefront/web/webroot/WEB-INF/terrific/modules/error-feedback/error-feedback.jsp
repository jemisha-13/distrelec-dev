<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<c:set var="isLoggedin" value="false" />
<sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')">
	<c:set var="isLoggedin" value="true" />
</sec:authorize>
<c:set var="productIDFormat"><format:articleNumber articleNumber="${productID}" /></c:set>

<a href="/feedback" data-aainteraction="report an error" data-location="pdp" class="error-link"><i class="fa fa-bug" aria-hidden="true"></i><spring:message code="item.error.link" text="Report an error" /></a>
<div class="error-report hidden" data-productid="${productIDFormat}">
	<h3 class="base ellipsis"><i class="closer"></i><spring:message code="item.error.header" text="Report an error" /></h3>
	<div class="error-intro">
		<spring:message code="item.error.thanks" text="Thank you for reporting an error!" /><br />
		<spring:message code="item.error.intro" arguments="<span>${productIDFormat}</span>���${productName}" argumentSeparator="���" text="Please provide details about item <span>${productIDFormat}</span>, ${productName}" />
	</div>
	<div class="error-input">
		<div class="error-reason validate-checkbox-group">
			<input type="checkbox" name="errorReason" id="error-type-1" value="Description/Image" /><label for="error-type-1"><spring:message code="item.error.type.desc" text="Description/Image" /></label><br />
			<input type="checkbox" name="errorReason" id="error-type-2" value="Technical specs/Data sheets" /><label for="error-type-2"><spring:message code="item.error.type.specs" text="Technical specs/Data sheets" /></label><br />
			<input type="checkbox" name="errorReason" id="error-type-3" value="Search/Navigation" /><label for="error-type-3"><spring:message code="item.error.type.search" text="Search/Navigation" /></label><br />
			<input type="checkbox" name="errorReason" id="error-type-4" value="Accessories" /><label for="error-type-4"><spring:message code="item.error.type.accessories" text="Accessories" /></label><br />
			<input type="checkbox" name="errorReason" id="error-type-5" value="Other" /><label for="error-type-5"><spring:message code="item.error.type.other" text="Other" /></label><br />
		</div>	
		<textarea class="error-description validate-empty" maxlength="255" name="errorDescription" placeholder="<spring:message code="item.error.description.placeholder" text="Your feedback" />"></textarea>
		<div class="error-contact">
			<c:choose>
				<c:when test="${isLoggedin}">
				<span class="i">&#8505;</span><spring:message code="item.error.contact" arguments="${productID}" text="We may contact you regarding your error report." /><br />
				</c:when>
				<c:otherwise>
					<spring:message code="item.error.name" arguments="${productID}" text="Please enter your namne and e-mail address." /><br />
					<spring:message code="item.error.contact" arguments="${productID}" text="We may contact you regarding your error report." /><br />
					<input type="text" class="customer-name validate-empty" placeholder="<spring:message code="item.error.name.placeholder" text="Name" />"><br />
					<input type="text" class="customer-email validate-email" placeholder="<spring:message code="item.error.email.placeholder" text="E-mail" />"><br />
				</c:otherwise>
			</c:choose>
		</div>
		<div class="error-submit">
			<a href="/feedback" class="button" data-aainteraction="submit error"><spring:message code="item.error.submit" text="Submit" /></a>
		</div>
		<div class="error-sent success hidden">
			<a href="#" class="closer"><spring:message code="toolsitem.share.close" /></a>
			<spring:message code="toolsitem.share.message.sent" text="Message sent."/> 
		</div>
		<div class="error-sent fail hidden">
			<a href="#" class="closer"><spring:message code="toolsitem.share.close" /></a>
			<spring:message code="toolsitem.share.message.error" text="Error, message not sent." /> 
		</div>
	</div>
</div>
<script id="tmpl-validation-error-radio" type="text/template">
	<spring:message code="validate.error.dropdown" />
</script>
<script id="tmpl-validation-error-checkboxgroup" type="text/template">
	<spring:message code="validate.error.checkboxgroup" />
</script>
<script id="tmpl-validation-error-empty" type="text/template">
	<spring:message code="validate.error.required" />
</script>
<script id="tmpl-validation-error-email" type="text/template">
	<spring:message code="validate.error.email" />
</script>
<script id="tmpl-validation-error-length" type="text/template">
	<spring:message code="validate.error.length" />
</script>