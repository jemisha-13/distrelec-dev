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
<%@ taglib prefix="oscache" uri="http://www.opensymphony.com/oscache" %>

<%@ attribute name="socialMediaLinkList" description="List of Social Media Links" type="java.util.List" rtexprvalue="true" %>

<%-- Module template selection --%>
<c:if test="${param.flush eq 'footer'}">
	<oscache:flush key="footer-swyn-${cachingKeyFooter}" scope="application" language="${currentLanguage.isocode}" />
</c:if>
<oscache:cache key="footer-swyn-${cachingKeyFooter}" time="${cachingTimeFooter}" scope="application" language="${currentLanguage.isocode}">
    <!-- Render time (Swyn): ${currentDateTime}, cached for ${cachingTimeFooter} seconds -->
	<terrific:mod name="footer-swyn" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
		<%@ include file="/WEB-INF/terrific/modules/footer-swyn/footer-swyn.jsp" %>
	</terrific:mod>
</oscache:cache>
