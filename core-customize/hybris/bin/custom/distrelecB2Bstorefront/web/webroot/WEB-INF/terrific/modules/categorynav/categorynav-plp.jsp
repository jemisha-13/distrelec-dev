<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"  %>


<c:if test="${ fn:length(categoryDisplayDataList) > 0}">

	<span class="mod-categorynav__wrapper__item mod-categorynav__wrapper__title"> <spring:message code="text.plp.filter.category" />: </span>
	<div class="mod-categorynav__wrapper" data-total-categories="${fn:length(categoryDisplayDataList)}">

		<c:forEach items="${categoryDisplayDataList}" var="category1data" varStatus="items" end="${fn:length(categoryDisplayDataList)}">

			<c:url value="${category1data.url}" var="facetValueQueryUrl"/>

			<div class="mod-categorynav__wrapper__item">
				<div class="mod-categorynav__wrapper__item__header">
					<a href="${facetValueQueryUrl}" data-aainteraction="apply category filter" data-aalinktext="${category1data.name}">
						<h3>${category1data.name} (${category1data.count})</h3>
					</a>
				</div>
			</div>

		</c:forEach>

	</div>


</c:if>

