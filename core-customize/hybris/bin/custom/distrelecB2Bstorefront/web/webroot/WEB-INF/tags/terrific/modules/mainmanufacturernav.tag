<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oscache" uri="http://www.opensymphony.com/oscache" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- Module template selection  --%>
<c:if test="${param.flush eq 'mainmanufacturernav' or mainNavFlush}">
	<oscache:flush key="mainmanufacturernav-${cachingKeyMainnav}" scope="application" language="${currentLanguage.isocode}" />
</c:if>
<oscache:cache key="mainmanufacturernav-${cachingKeyMainnav}" time="${cachingTimeMainnav}" scope="application" language="${currentLanguage.isocode}">
    <!-- Render time (Main Manufacturer navigation): ${currentDateTime}, cached for ${cachingTimeMainnav} seconds with key mainnav-${cachingKeyMainnav} -->
	<terrific:mod name="mainmanufacturernav" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
		<%-- set isOCI var --%>
		<c:set var="isOCI" value="false" />
		<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
			<c:set var="isOCI" value="true" />
		</sec:authorize>

		<c:choose>
			<c:when test="${isOCI eq false}">
				<%@ include file="/WEB-INF/terrific/modules/mainmanufacturernav/mainmanufacturernav.jsp" %>
			</c:when>
			<c:otherwise>
				<%@ include file="/WEB-INF/terrific/modules/OCI/mainmanufacturernav/mainmanufacturernav.jsp" %>
			</c:otherwise>
		</c:choose>
	</terrific:mod>
</oscache:cache>