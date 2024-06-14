<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/terrific/user" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="border-bottom">

	<div class="inner row gu-10 border-bottom">
	
		<div class="inner row padding-gu">
			<div class="gu-12 register-option base">
				<br/>
				<br/>
				<div class="intro small">
					<p class="required"><spring:message code="survey.required" arguments="*"/></p>
				</div>
			</div>
		</div>
	
		<div class="form-box">
			<form:form method="post" modelAttribute="surveyForm" action="/survey/${survey.version}">
				<c:forEach var="section" items="${survey.sections}">
					<div class="row base padding-gu">
						<h3><c:out value="${section.title}" /></h3>
						<c:forEach var="question" items="${section.questions}">
							<table>
								<tr style="${question.type.code == 'HIDDEN' ? 'display:none' : ''}">
									<c:choose>
										<c:when test="${question.type.code == 'CHECKBOX'}">
											<td valign="top">
												<formUtil:formCheckbox idKey="${question.uid}" value="${question.value}" path="questions['${question.uid}']" mandatory="${question.mandatory}" 
													inputCSS="${question.mandatory ? 'validate-checkbox' : '' } checkbox-big" showAsterisk="false"/>
											</td>
										</c:when>
										<c:otherwise>
											<td style="width:80%" valign="top">
												<formUtil:formLabel idKey="${question.uid}" labelKey="${question.value}" path="${question.uid}" mandatory="${question.mandatory}"/>
											</td>
										</c:otherwise>
									</c:choose>
									<td valign="top">
										<c:choose>
											<c:when test="${question.type.code == 'TEXT' or question.type.code == 'EMAIL'}">
												<formUtil:formInputBox idKey="${question.uid}" path="questions['${question.uid}']" mandatory="${question.mandatory}" 
														inputCSS="${question.mandatory ? 'validate-empty' : '' } ${question.type.code == 'EMAIL' ? 'validate-email' : ''}"/>
											</c:when>
																			
											<c:when test="${question.type.code == 'TEXTAREA'}">
												<formUtil:formTextarea idKey="${question.uid}" path="questions['${question.uid}']" mandatory="${question.mandatory}" inputCSS="${question.mandatory ? 'validate-empty' : '' }"/>
											</c:when>
											
											<c:when test="${question.type.code == 'HIDDEN'}">
												<form:hidden path="questions['${question.uid}']" id="${question.uid}" />
											</c:when>
											
											<c:when test="${question.type.code == 'CHECKBOX'}">
												<formUtil:formLabel idKey="${question.uid}" labelKey="${question.value}" path="${question.uid}" mandatory="${question.mandatory}"/>
											</c:when>
											
											<c:when test="${question.type.code == 'RADIO'}">
												<c:forEach var="value" items="${question.possibleAnswers}" varStatus="index">
													<formUtil:formRadioButton idKey="${question.uid}_${index}" path="questions['${question.uid}']" name="${question.uid}" value="${value.code}"/>
													<label for="${question.uid}_${index}" style="margin-right: 30px;"><c:out value="${value.code}" /></label>
												</c:forEach>
											</c:when>
											
											<c:when test="${question.type.code == 'DROPDOWN'}">
												<formUtil:formSelectBox items="${question.possibleAnswers}" idKey="${question.uid}" path="questions['${question.uid}']" mandatory="${question.mandatory}" 
																skipBlank="false" selectCSSClass="selectpicker validate-dropdown" skipBlankMessageKey="form.select.empty" />
											</c:when>
										</c:choose>
									</td>
								</tr>
							</table>
						</c:forEach>
					</div>
				</c:forEach>
				<br/>
				<%-- Captcha --%>
				<div class="row row-captcha base padding-gu">
					<div class="gu-4">
					</div>
					<div class="gu-4 recaptcha">
						<mod:captcha/>
					</div>
				</div>
				
				<div class="row padding-gu">
					<div class="gu-4">&nbsp;</div><div class="gu-4">&nbsp;</div>
					<div class="gu-4">&nbsp;</div><div class="gu-4">&nbsp;</div>
				</div>
				<%-- Submit Button --%>
				<div class="row padding-gu">
					<div class="gu-4">
						<a href="/" class="btn btn-secondary btn-back" tabindex="-1"><spring:theme code="survey.btn.cancel" text="Cancel"/><i></i></a>
					</div>
					<div class="gu-4">
						<button class="btn btn-primary btn-survey" type="submit"><i></i> <spring:theme code="survey.btn.send" text="Send"/></button>
					</div>
				</div>
			</form:form>
			
			<br/><br/>
			
			<script id="tmpl-survey-validation-error-dropdown" type="text/template">
				<spring:message code="validate.error.dropdown" />
			</script>

			<script id="tmpl-survey-validation-error-empty" type="text/template">
				<spring:message code="validate.error.required" />
			</script>

			<script id="tmpl-survey-validation-error-email" type="text/template">
				<spring:message code="validate.error.email" />
			</script>

			<script id="tmpl-survey-validation-error-captcha" type="text/template">
				<spring:message code="validate.error.captcha" />
			</script>

			<script id="tmpl-survey-validation-error-checkbox" type="text/template">
				<spring:message code="validate.error.checkbox" />
			</script>

			<script id="tmpl-survey-validation-error-length" type="text/template">
				<spring:message code="validate.error.length" />
			</script>
		</div>
	</div>
</div>
