<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>

<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ tag description="Module: Schema with Various Templates" %>

<%-- Module template selection --%>
<terrific:mod name="seo" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}"  attributes="${attributes}">
	<%-- set isOCI var --%>
	<c:set var="isOCI" value="false" />
	<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
		<c:set var="isOCI" value="true" />
	</sec:authorize>

	<c:choose>
		<c:when test="${isOCI eq false}">
			<c:if test="${ template == 'category' }">
				<%@ include file="/WEB-INF/terrific/modules/seo/seo-category.jsp" %>
			</c:if>
		</c:when>
		<c:otherwise>
			<c:if test="${ template == 'category' }">
				<%@ include file="/WEB-INF/terrific/modules/OCI/seo/seo-category.jsp" %>
			</c:if>
		</c:otherwise>
	</c:choose>
</terrific:mod>
