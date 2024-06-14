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

<%-- Required backend data --%>
<%@ attribute name="heroTeaserData" type="java.util.List" required="true" %>
<%@ attribute name="componentWidth" type="java.lang.String" required="true" %>
<%@ attribute name="componentHeight" type="java.lang.String" required="true" %>
<%@ attribute name="title" type="java.lang.String" required="true" %>
<%@ attribute name="autoplay" type="java.lang.Boolean" required="true" %>
<%@ attribute name="autoplayTimeout" type="java.lang.Integer" required="true" %>
<%@ attribute name="wtTeaserTrackingId" type="java.lang.String" required="false" %>

<%-- Specific module settings --%>
<%@ tag description="Module: Hero Teaser - Templates: default" %>

<%-- Module template selection --%>
<terrific:mod name="hero-teaser-pure" tag="aside" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
<%@ include file="/WEB-INF/terrific/modules/hero-teaser-pure/hero-teaser-pure.jsp" %>
</terrific:mod>
