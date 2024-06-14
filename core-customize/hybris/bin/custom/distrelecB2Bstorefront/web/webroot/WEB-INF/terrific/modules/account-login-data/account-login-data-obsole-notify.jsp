<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<c:set var="formAction" value="/login-data/changeObsolPreference" />

<spring:theme var="allCategories" code="text.all.categories"  />
<spring:theme var="orderinfoandupdates" code="text.orderinfoandupdates" />
<spring:theme var="orderinfoandupdatesConsent" code="text.orderinfoandupdates.consent" />
<spring:theme var="orderinfoandupdatesQuestion" code="text.orderinfoandupdates.question" />
<spring:theme var="obsolecenceNotifications" code="text.obsolecence.notifications" />
<spring:theme var="obsolecenceNotificationsConsent" code="text.obsolecence.notificationsconsent" />
<spring:theme var="obsoleSuccessMessage" code="account.confirmation.profile.updated" />

<form:form action=""  modelAttribute="obsolePreferenceForm" method="post">

	<div class="gu-8 form-box obsolescence-notifications">

		<h2 class="form-title"><spring:theme code="text.communication.preferences" /></h2>

		<strong class="obsolescence-notifications__title">${orderinfoandupdates}</strong>
		<div class="obsolescence-notifications__description">${orderinfoandupdatesConsent}</div>
		<div class="obsolescence-notifications__description">${orderinfoandupdatesQuestion}</div>

		<div class="obsolescence-notifications__email-content">

			<p class="obsolescence-notifications__email-label"><spring:message code="metahd.account.email" /></p>
			<div class="obsolescence-notifications__email-checkbox">

				<c:set var="dataObsol" value="off" />
				<c:set var="dataEmail" value="off" />

				<c:if test="${updateNewsletterForm.marketingConsent eq 'true'}">
					<c:set var="dataEmail" value="on" />
				</c:if>

				<c:if test="${optedForObsolescence eq 'true'}">
					<c:set var="optedForObsolescenceValue" value="checked" />
					<c:set var="dataObsol" value="on" />
				</c:if>

				<div class="obsolescence-notifications__email-checkbox-header">
					<span>${obsolecenceNotifications}</span>
					<i class="fas fa-chevron-up arrows "></i>
					<i class="fas fa-chevron-down arrows hidden"></i>
				</div>

				<div class="obsolescence-notifications__email-checkbox-content">

					<p class="obsolescence-notifications__description">${obsolecenceNotificationsConsent}</p>

					<ul class="obsolescence-notifications__category-options">

						<c:choose>
							<c:when test="${allCatSelected eq 'true'}">
								<c:set var="allCatSelectedValue" value="checked" />
							</c:when>
							<c:otherwise>
								<c:set var="allCatSelectedValue" value="" />
							</c:otherwise>
						</c:choose>

						<li>
							<input id="obsolescenceNotifications_AllOption"  type="checkbox" value="All" ${allCatSelectedValue} >
							<label for="obsolescenceNotifications_AllOption">${allCategories}</label>
						</li>

						<c:forEach items="${categories}" var="obsolCategory">

							<c:choose>
								<c:when test="${allCatSelected eq 'true'}">
									<c:set var="categoryOptedValue" value="" />
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${obsolCategory.isObsolCategorySelected eq 'true'}">
											<c:set var="categoryOptedValue" value="checked" />
										</c:when>
										<c:otherwise>
											<c:set var="categoryOptedValue" value="" />
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>

							<li class="obsolescence-notifications__category-option">
								<input  id="${obsolCategory.category.code}" type="checkbox" data-category="${obsolCategory.category.nameEN}" value="${obsolCategory.isObsolCategorySelected}" ${categoryOptedValue}>
								<label for="${obsolCategory.category.code}">${obsolCategory.category.name}</label>
							</li>

						</c:forEach>

					</ul>

				</div>

			</div>

			<mod:global-messages template="component" skin="component success hidden"  headline='' body='${obsoleSuccessMessage}' type="success"/>

		</div>

		<button class="btn btn-primary btn-change" data-aainteraction="save order email preferences" data-email="${dataEmail}" data-obsol="${dataObsol}"><i></i><spring:theme code="logindata.buttonChange" /></button>
	</div>

</form:form>
