<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<spring:message code="text.store.dateformat" var="datePattern" />

<div id="read_reviews">
	<ul class="review_actions">
		<li>
			<a href="#" id="write_review_action">
				<c:choose>
					<c:when test="${not empty product.reviews}">
						<spring:theme code="review.write.title"/>
					</c:when>
					<c:otherwise>
						<spring:theme code="review.no.reviews"/>
					</c:otherwise>
				</c:choose>
			</a>
		</li>
		<li>${showingReviews}&nbsp;<spring:theme code="review.number.of"/>&nbsp;${totalReviews}&nbsp;<spring:theme code="review.number.reviews"/></li>
		<c:if test="${showingReviews != totalReviews}" >
		<li><a href="#" id="show_all_reviews_action"><spring:theme code="review.show.all"/></a></li>
		</c:if>
	</ul>
	<c:if test="${not empty product.reviews}">
		<c:forEach items="${product.reviews}" var="review" varStatus="status">
			<c:choose>
				<c:when test="${status.last}">
					<c:set var="reviewDetailStyle" scope="page" value="border:none"/>
				</c:when>
				<c:otherwise>
					<c:set var="reviewDetailStyle" scope="page" value=""/>
				</c:otherwise>
			</c:choose>
			<div class="review_detail" style="${reviewDetailStyle}">
				<h3>${review.headline}</h3>
				<span class="stars large right" style="display: inherit;">
					<span style="width: <fmt:formatNumber maxFractionDigits="0" value="${review.rating * 24}" />px;"></span>
				</span>
				<p>${review.comment}</p>
	
				<p class="review_origins">
					<spring:theme code="review.submitted.by"/><c:out value=" "/>
					
					<c:choose>
						<c:when test="${not empty review.alias}">
							${review.alias}
						</c:when>
						<c:otherwise>
							<spring:theme code="review.submitted.anonymous"/>
						</c:otherwise>
					</c:choose>
	
					<c:set var="reviewDate" value="${review.date}" />
					(<fmt:formatDate value="${reviewDate}" pattern="${datePattern}" />)
				</p>
			</div>
		</c:forEach>
	</c:if>
</div>