<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- counter need to be outside of both loops since there can be multiple classifications on one product, but only one of them actually containing features --%>
<c:set var="sectionCount" value="0" />
<c:set var="visibilityCount" value="0" />
<c:set var="totalCount" value="0" />

<table>
	<tbody>
		<c:forEach items="${product.classifications}" var="classification">
			<tr>
				<c:forEach items="${classification.features}" var="feature">
					<c:choose>
						<c:when test="${feature.visibility eq 'a_visibility' or visibilityCount < 10}">
							<c:set var="visibilityCount" value="${ visibilityCount + 1 }" />
						</c:when>
					</c:choose>
					<c:if test="${visibilityCount == totalCount}">
						<c:set var="sectionCount" value="0" />
					</c:if>
					<c:if test="${ (totalCount != 0 && sectionCount mod 2 == 0)}">
						<%-- Add Spacer tds if there is only one visible feature to keept correct alignment of columns --%>
						<c:if test="${ (totalCount != 0 && visibilityCount == 1)}">
							<td class="td1" valign="top"></td><td class="td2" valign="top"></td>
						</c:if>
						<c:choose>
							<c:when test="${feature.visibility eq 'a_visibility' or visibilityCount < 10}">
								</tr><tr>
							</c:when>
							<c:otherwise>
								</tr><tr class="row-hidden">
							</c:otherwise>
						</c:choose>
					</c:if>
					<td class="td1" valign="top">
						<c:choose>
							<c:when test="${feature.searchable}">
								<input id="${feature.name}" type="checkbox" name="${feature.name}" />
								<label for="${feature.name}">${feature.name}</label>
							</c:when>
							<c:otherwise>
								<label class="no-checkbox">${feature.name}</label>
							</c:otherwise>
						</c:choose>
					</td>
					<td class="td2" valign="top">
						<c:forEach items="${feature.featureValuesWithUnit}" var="entry" varStatus="status">
							<span class="feature-value">${entry.key.value}</span>
							<c:choose>
								<c:when test="${feature.range}">
									<span class="feature-unit">${not status.last ? '-' : entry.value.symbol}</span>
								</c:when>
								<c:otherwise>
									<span class="feature-unit">${entry.value.symbol}</span>
									${not status.last ? '<br/>' : ''}
								</c:otherwise>
							</c:choose>
						</c:forEach>						
					</td>
					<c:set var="sectionCount" value="${ sectionCount + 1 }" />
					<c:set var="totalCount" value="${ totalCount + 1 }" />
				</c:forEach>
			</tr>
		</c:forEach>
	</tbody>
</table>
<c:if test="${totalCount > visibilityCount}">
	<div class="row show-more"><a class="show-more-link btn-show-more" href="/"><span><spring:message code="product.accordion.show.more" /></span></a></div>
</c:if>
<div class="row row-search">
	<a class="btn btn-secondary btn-search" data-search-url="${similarProductBaseUrl}" href="#"><i></i><spring:message code="product.accordion.search.similar.products" /></a>
</div>


<script id="tmpl-similarsearch-error-empty" type="text/template">
	<spring:message code="validate.error.checkboxgroup" />
</script>