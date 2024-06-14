<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<c:if test="${not empty campaigns}">
	<json:object>
		<json:object name="campaigns">
			<json:array name="campaigns" items="${campaigns}" var="campaign">
				<json:object>
					<json:property name="id" value="${campaign.id}" />
					<json:property name="html" value="${campaign.html}" />
					<json:property name="label" value="${campaign.label}" />
					<json:property name="top" value="${campaign.label eq 'SearchResult_top'}" />
					<json:property name="text" value="${campaign.text}" />
				</json:object>
			</json:array>	
		</json:object>
	</json:object>
</c:if>