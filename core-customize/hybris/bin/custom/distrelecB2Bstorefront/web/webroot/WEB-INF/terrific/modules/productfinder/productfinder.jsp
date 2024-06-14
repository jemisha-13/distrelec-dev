<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<spring:message code="productfinder.range" var="sRange" text="to" />
<spring:message code="productfinder.btn.nolimit" var="sNoPriceLimit" text="no price limitation" />

<div class="pos-indicator b-loader">
<c:choose>
	<c:when test="${not empty productFinderData}">
		<%-- number of results --%>
		<div class="padding">
			<p class="finder-total b-total" data-text="<spring:message code="productfinder.count" />"></p>
		</div>


		<%-- JSON directly written from BE for performance reasons --%>
		<script>
			var productFinderData = '${productFinderDataJson}';
		</script>


		<%-- finder grid --%>
		<div class="grid padding cf">
			<form:form autocomplete="off" method="GET"><%-- disable browser remembering checkbox states (avoid back button inconsistency) --%>
				<c:forEach items="${productFinderData.columns}" var="colum" varStatus="columnStatus">
					<%-- column --%>
					<c:set var="c" value="${columnStatus.index}" />

					<div class="column">
						<c:forEach items="${colum.groups}" var="group" varStatus="groupStatus">
							<%-- group --%>
							<c:set var="g" value="${groupStatus.index}" />

							<div class="group">
								<h3 class="base"><c:out value="${group.name}" /></h3>
								<div class="groupControls">
									<%-- first single item is a default radio to reset any chosen limitation, see https://jira.namics.com/browse/DISTRELEC-2540 --%>
									<c:choose>
										<c:when test="${group.type eq 'SINGLE'}">
											<div class="itemControl">
												<input type="radio"
													data-column="<c:out value="${c}" />"
													data-group="<c:out value="${g}" />"
													data-value="nolimit"
													id="column<c:out value="${c}" />_group<c:out value="${g}" />_valuenoLimit"
													value="column<c:out value="${c}" />_group<c:out value="${g}" />_valuenoLimit"
													class="b-nolimit"
													name="column<c:out value="${c}" />_group<c:out value="${g}" />_value" />

												<%-- label on first default radio is NEVER disabled --%>
												<label class="customValue" for="column<c:out value="${c}" />_group<c:out value="${g}" />_valuenoLimit" title="${sNoPriceLimit}">
													<span class="label-minmax">${sNoPriceLimit}</span>
												</label>
											</div>
										</c:when>
									</c:choose>

									<c:forEach items="${group.values}" var="value" varStatus="valueStatus">
										<%-- value --%>
										<c:set var="v" value="${valueStatus.index}" />

										<div class="itemControl">
											<c:choose>
												<%-- type: SINGLE (price ranges from factfinder cannot be combined, see https://jira.namics.com/browse/DISTRELEC-1344) --%>
												<c:when test="${group.type eq 'SINGLE'}">
													<input type="radio"
														data-column="<c:out value="${c}" />"
														data-group="<c:out value="${g}" />"
														data-value="<c:out value="${v}" />"
														id="column<c:out value="${c}" />_group<c:out value="${g}" />_value<c:out value="${v}" />"
														value="column<c:out value="${c}" />_group<c:out value="${g}" />_value<c:out value="${v}" />"
														class="b-radio"
														name="column<c:out value="${c}" />_group<c:out value="${g}" />_value"
														<c:if test="${value.checked}"> checked="checked"</c:if>
														<c:if test="${value.disabled}"> disabled="disabled"</c:if> />

													<%-- label also needs disabled attribute if applicable --%>
													<label class="customValue" for="column<c:out value="${c}" />_group<c:out value="${g}" />_value<c:out value="${v}" />" title="<c:out value="${value.name}" />"<c:if test="${value.disabled}"> disabled="disabled"</c:if>>
														<span class="label-minmax"><c:out value="${value.name}" /></span>
													</label>
												</c:when>
												<c:otherwise>
													<%-- type: MULTI (default) --%>
													<input type="checkbox"
														data-column="<c:out value="${c}" />"
														data-group="<c:out value="${g}" />"
														data-value="<c:out value="${v}" />"
														id="column<c:out value="${c}" />_group<c:out value="${g}" />_value<c:out value="${v}" />"
														value="column<c:out value="${c}" />_group<c:out value="${g}" />_value<c:out value="${v}" />"
														class="b-finder"
														<c:if test="${value.checked}"> checked="checked"</c:if>
														<c:if test="${value.disabled}"> disabled="disabled"</c:if> />
													<%-- label also needs disabled attribute if applicable --%>
													<label for="column<c:out value="${c}" />_group<c:out value="${g}" />_value<c:out value="${v}" />" title="<c:out value="${value.name}" />"<c:if test="${value.disabled}"> disabled="disabled"</c:if>>
														<span><c:out value="${value.name}" /></span>
													</label>
												</c:otherwise>
											</c:choose>
										</div>
									</c:forEach>

									<c:choose>
										<%-- type: SINGLE custom min/max value --%>
										<c:when test="${group.type eq 'SINGLE' && group.customValue != null}">
											<c:set var="cvmin" value="${group.customValue.minValue}" />
											<c:set var="cvmax" value="${group.customValue.maxValue}" />
											<div class="itemControl base">
												<input type="radio"
													data-column="<c:out value="${c}" />"
													data-group="<c:out value="${g}" />"
													data-value="customValue"
													id="column<c:out value="${c}" />_group<c:out value="${g}" />_valuecustomValue"
													value="column<c:out value="${c}" />_group<c:out value="${g}" />_valuecustomValue"
													class="b-custom"
													name="column<c:out value="${c}" />_group<c:out value="${g}" />_value"
													<c:if test="${group.customValue.checked}"> checked="checked"</c:if> />

												<%-- label on customValue is NEVER disabled --%>
												<label class="customValue" for="column<c:out value="${c}" />_group<c:out value="${g}" />_valuecustomValue"></label>
												<fieldset class="validate-range">
													<input type="text" class="numeric-minmax customMin" value="<c:out value='${not empty cvmin ? cvmin : 0}' />" />
													<span class="between-minmax">${sRange}</span>
													<input type="text" class="numeric-minmax customMax" value="<c:out value='${not empty cvmax ? cvmax : 0}' />" />
												</fieldset>
											</div>
										</c:when>
									</c:choose>
								</div>
							</div><!--end group-->
						</c:forEach>
					</div><!--end column-->
				</c:forEach>

				<script id="tmpl-productfinder-validation-error-customrange" type="text/template">
					<spring:message code="productfinder.invalid.customrange" text="Invalid custom range" />
				</script>
			</form:form>
		</div><!--end grid-->

		<div class="controls base cf">
			<div class="float-right cf">
				<%-- buttons: reset, go to products page --%>
				<div class="float-left padding-right">
					<a href="#" class="btn btn-secondary b-reset"><spring:message code="productfinder.btn.reset" /></a>
				</div>
				<div class="float-left">
					<a href="<c:url value="${productFinderData.searchUrl}" />" class="btn btn-primary b-go"><spring:message code="productfinder.btn.results" /></a>
				</div>
			</div>
		</div><!--end controls-->
	</c:when>
	<c:otherwise>
		<spring:message code="productfinder.error" />
	</c:otherwise>
</c:choose>

	<%-- loading indicator --%>
	<div class="indicator c-center-vertical"><img class="centered" src="<c:url value="/_ui/all/media/img/throbber_white_matte.gif" />" alt="" /></div>
</div>