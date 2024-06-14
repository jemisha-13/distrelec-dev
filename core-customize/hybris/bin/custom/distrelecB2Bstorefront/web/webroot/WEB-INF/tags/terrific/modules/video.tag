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

<%-- Specific module settings --%>
<%@ tag description="Module: product-image-gallery - Templates: default" %>
<%@ attribute name="videoId" description="The video Id" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="playerId" description="The player Id, which is the same for all Videos" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="videoWidth" description="The video Width" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="videoHeight" description="The video Height" type="java.lang.String" rtexprvalue="true" %>

<%-- Module template selection --%>
<terrific:mod name="video" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<%@ include file="/WEB-INF/terrific/modules/video/video.jsp" %>
</terrific:mod>
