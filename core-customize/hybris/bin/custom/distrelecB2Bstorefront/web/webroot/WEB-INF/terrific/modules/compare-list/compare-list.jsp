<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<c:choose>
	<c:when test="${empty currentList}">
		<p class="compare-empty padding-left"><spring:message code="compare.list.empty" /></p>
	</c:when>
	<c:otherwise>

	<div class="tableGrid">
		<mod:nextprevproductdetail skin="compare" template="compare" />
		<mod:compare-list-item product="${currentList[0]}" template="head" skin="head" htmlClasses="tableGrid__product__sidebar"/>

		<div class="compare-list__grid">

			<c:forEach items="${currentList}" var="product" varStatus="status">

				<div class="compare-list__grid-item">
					<mod:compare-list-item product="${product}" position="${status.index}"/>
				</div>

			</c:forEach>

		</div>

	</div>

	</c:otherwise>
</c:choose>
