<%-- Common module settings --%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>

<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Required backend data --%>
<%@ attribute name="media" type="de.hybris.platform.core.model.media.MediaModel" required="true" %>
<%@ attribute name="contentText" type="java.lang.String" required="false" %>
<%@ attribute name="encodedUrl" type="java.lang.String" required="false" %>
<%@ attribute name="imgWidth" type="java.lang.String" required="false" %>
<%@ attribute name="imgHeight" type="java.lang.String" required="false" %>
<%@ attribute name="caption" type="java.lang.String" required="false" %>

<%-- Specific module settings --%>
<%@ tag description="Module: linked-image - Templates: default" %>

<%-- Module template selection --%>
<terrific:mod name="linked-image" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${template == 'text'}">
			<%@ include file="/WEB-INF/terrific/modules/linked-image/linked-image-text.jsp" %>
		</c:when>
		<c:otherwise>
			<%@ include file="/WEB-INF/terrific/modules/linked-image/linked-image.jsp" %>
		</c:otherwise>
	</c:choose>
</terrific:mod>
