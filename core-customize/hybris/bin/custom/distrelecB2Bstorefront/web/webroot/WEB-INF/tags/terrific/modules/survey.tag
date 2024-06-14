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
<%@ tag description="Module: Survey - Templates: default" %>
<%@ attribute name="survey" description="The survey object" type="com.namics.distrelec.b2b.core.service.survey.data.DistOnlineSurveyData" rtexprvalue="true" %>
<%@ attribute name="action" %>
<%@ attribute name="captcha" type="java.lang.String" %>

<%-- Module template selection --%>
<terrific:mod name="survey" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${template eq 'survey-success'}">
			<%@ include file="/WEB-INF/terrific/modules/survey/survey-success.jsp" %>
		</c:when>
		<c:when test="${template eq 'survey-error'}">
			<%@ include file="/WEB-INF/terrific/modules/survey/survey-error.jsp" %>
		</c:when>
		<c:otherwise>
			<%@ include file="/WEB-INF/terrific/modules/survey/form-survey.jsp" %>
		</c:otherwise>
	</c:choose>
</terrific:mod>