<%-- Common module settings --%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>

<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- Required backend data --%>
<%@ attribute name="feedbackTextTop" required="false" type="java.lang.String" %>
<%@ attribute name="pushedProductsList" required="false" type="java.util.ArrayList" %>
<%@ attribute name="advisorQuestions" required="false" type="java.util.ArrayList" %>
<%@ attribute name="productCode" required="false" type="java.lang.String" %>

<%-- Specific module settings --%>
<%@ tag description="Module: campaign - Templates: default" %>

<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<c:set var="isOCI" value="true" />
</sec:authorize>

<%-- Module template selection --%>
<terrific:mod name="campaign" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${isOCI eq false}">
			<c:choose>
				<c:when test="${template == 'advisor'}">
					<%@ include file="/WEB-INF/terrific/modules/campaign/campaign-advisor.jsp" %>
				</c:when>
				<c:when test="${template == 'feedback-pagination'}">
					<%@ include file="/WEB-INF/terrific/modules/campaign/campaign-feedback-pagination.jsp" %>
				</c:when>
				<c:when test="${template == 'product-detail-page'}">
					<%@ include file="/WEB-INF/terrific/modules/campaign/campaign-product-detail-page.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/campaign/campaign-feedback.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${template == 'advisor'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/campaign/campaign-advisor.jsp" %>
				</c:when>
				<c:when test="${template == 'feedback-pagination'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/campaign/campaign-feedback-pagination.jsp" %>
				</c:when>
				<c:when test="${template == 'product-detail-page'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/campaign/campaign-product-detail-page.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/OCI/campaign/campaign-feedback.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
</terrific:mod>
