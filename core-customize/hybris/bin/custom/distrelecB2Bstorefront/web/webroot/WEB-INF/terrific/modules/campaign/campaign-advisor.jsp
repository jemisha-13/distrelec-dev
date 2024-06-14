<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>

<div class="advisorBox">
	<c:forEach var="question" items="${advisorQuestions}">
		<p>${question.question}</p>
		<c:if test="${not empty question.answers}">
			<ul>
				<c:forEach var="answer" items="${question.answers}">
					<li>
						<a href="${answer.query.url}" name="${fn:toLowerCase(currentCountry.isocode)}.faf-c-a.${namicscommerce:encodeURI(fn:toLowerCase(answer.text))}.-">
							<c:choose>
								<c:when test="${not empty answer.text && not empty answer.image }">
									<div class="classImage">
										<img style="display: inline" src="${answer.image}" />
										<div class="ff-advisor-campaign">${answer.text} <i></i></div>
									</div>
								</c:when>
								<c:when test="${empty answer.text && not empty answer.image }">
									<div class="classImage">
										<img style="display: inline" src="${answer.image}" />
									</div>
								</c:when>
								<c:otherwise>
									${answer.text} <i></i>
								</c:otherwise>
							</c:choose>
						</a>
					</li>
				</c:forEach>
			</ul>
		</c:if>
	</c:forEach>
</div>

