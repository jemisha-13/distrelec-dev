<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<spring:message code="import-tool.matching.description" var="sMatchingDescription" />


<div class="bd">
	
	<div class="matching-intro">
		<spring:message code="import-tool.matching.instructions"/>
	</div>

	<div class="warning-no-columns hidden">
		<spring:message code="import-tool.matching.nocolumnswarning" />
	</div>
	
	
	
	<form:form action="/bom-tool/review-file" class="matching-table" id="review-file" modelAttribute="uploadForm" enctype="application/x-form-urlencoded" method="post" accept-charset="ISO-8859-1">

		<div class="matching-table__content">

			<p class="content-description">
				${sMatchingDescription}
			</p>

			<div class="firstRowDataCheckBox">
				<input id="firstRowData" type="checkbox" class="checkbox-big is-open-order" name="ignoreFirstRow" value="true"/>
				<label for="firstRowData"><spring:message code="import-tool.matching.checkbox" /></label>
			</div>


			<input type="hidden" name="articleNumberPosition" id="articleNumberPosition" />
			<input type="hidden" name="quantityPosition" id="quantityPosition" />
			<input type="hidden" name="referencePosition" id="referencePosition" />


			<div class="matching-table-main">
				<table class="table-import-matching">

					<tr class="first-row">
						<c:set var="columnCounter" value="0" />
						<c:forEach items="${fileLines[0]}" var="header" varStatus="status">

							<c:if test="${status.index lt '3'}">
								<th>
									<select id="columnNameSelector-${columnCounter}" class="columnNameSelector">
										<option value="" class="disabled"> <spring:message code="import-tool.matching.options.select" /> </option>
										<option value="quant"> <spring:message code="import-tool.matching.options.quantity" /> </option>
										<option value="dis-an"> <spring:message code="import-tool.matching.options.distArticleNumber" /> </option>
										<option value="ref"> <spring:message code="import-tool.matching.options.reference" /> </option>
									</select>
								</th>
							</c:if>

							<c:set var="columnCounter" value="${columnCounter + 1}" />
						</c:forEach>
					</tr>

					<c:set var="rowCounter" value="0" />
					<c:forEach items="${fileLines}" var="line" varStatus="status">
						<tr id="row-${rowCounter}">
							<c:forEach items="${line}" var="cell" varStatus="status">
								<c:if test="${status.index lt '3'}">
									<td>${cell}</td>
								</c:if>
							</c:forEach>
						</tr>

						<c:set var="rowCounter" value="${rowCounter + 1}" />
					</c:forEach>

					<fmt:parseNumber var="columnCounterInt" integerOnly="true" type="number" value="${columnCounter}" />

					<colgroup>
						<c:forEach begin="0" end="${columnCounterInt-1}" varStatus="loop">
							<c:if test="${loop.index lt '3'}">
								<col class="columnNameSelector-${loop.index}">
							</c:if>

						</c:forEach>
					</colgroup>

				</table>

			</div>
		</div>
		
		<div class="matching-table__btn">
			<div class="import-tool-matching-buttons">
				<a href="javascript:history.back();" class="btn btn-secondary btn-back"><i></i><spring:message code="bomdataimport.review.back" text="Back" /></a>
				<button type="submit" class="btn btn-primary btn-continue"><spring:theme code="bomdataimport.continue" /></button>
			</div>
		</div>
		
		<input type="hidden" name="fileName" id="fileName" value="${fileName}">
		
	</form:form>
	<mod:lightbox-login-required-import-tool />
</div>



