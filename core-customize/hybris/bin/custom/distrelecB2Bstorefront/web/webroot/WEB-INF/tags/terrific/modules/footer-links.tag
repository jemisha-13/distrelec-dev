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
<%@ attribute name="navigationNodes" description="List of Footer Navigation Nodes" type="java.util.List" rtexprvalue="true" %>
<%@ attribute name="wrapAfter" description="Number of Links that should be showed below one Nav Node" type="java.lang.String" rtexprvalue="true" %>

<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<c:set var="isOCI" value="true" />
</sec:authorize>


<c:choose>
	<c:when test="${isOCI eq false}">
		<c:set var="footerCacheKey" value="footer-links-${cachingKeyFooter}" />
	</c:when>
	<c:otherwise>
		<c:set var="footerCacheKey" value="footer-links-${cachingKeyFooter}-oci" />
	</c:otherwise>
</c:choose>

<%-- Module template selection --%>
<c:if test="${param.flush eq 'footer'}">
	<oscache:flush key="${footerCacheKey}" scope="application" language="${currentLanguage.isocode}" />
</c:if>
<oscache:cache key="${footerCacheKey}" time="${cachingTimeFooter}" scope="application" language="${currentLanguage.isocode}">
    <!-- Render time (Footer links): ${footerCacheKey}, ${currentDateTime}, cached for ${cachingTimeFooter} seconds -->
	<terrific:mod name="footer-links" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
		<c:choose>
			<c:when test="${isOCI eq false}">
				<%@ include file="/WEB-INF/terrific/modules/footer-links/footer-links.jsp" %>
			</c:when>
			<c:otherwise>
				<%@ include file="/WEB-INF/terrific/modules/OCI/footer-links/footer-links.jsp" %>
			</c:otherwise>
		</c:choose>
	</terrific:mod>
</oscache:cache>
