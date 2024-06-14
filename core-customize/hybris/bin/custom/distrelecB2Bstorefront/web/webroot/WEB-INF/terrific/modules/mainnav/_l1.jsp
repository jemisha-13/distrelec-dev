<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<spring:eval expression="new java.util.Locale('en')" var="enLocale" />

<spring:theme code="metahd.lists.import-tool" text="Import Tool" var="sImportTool" />

<c:set var="nodesChildren" value="${nodes.get()}"/>
<c:if test="${not empty nodesChildren}">
	<ul class="l1 cf" data-enable-flyout="${!megaFlyOutDisabled}" style="display: table;">
		<c:forEach items="${nodesChildren}" var="level1node" varStatus="status">
			<c:set var="level1entry" value="${level1node.entries[0]}" />
			<c:if test="${ycommerce:displayCatalogEProcurement(level1entry.item, request) && ycommerce:hasNotOnlyPunchedOutCategories(level1node, request)}">
				<li class="e1 count_e1_${status.count}" >
					<c:set var="titleValueWt" value="${fn:replace(level1node.title, ' ', '-')}" />
					<c:set var="titleValueWtLower" value="${fn:toLowerCase(titleValueWt)}" />

					<c:set var="enLevel1nodeTitle" value="${fn:toLowerCase(level1node.getTitle(enLocale))}" />

					<a class="a1" href="#" title="${level1node.title}" data-aainteraction='navigation' data-location='top nav' data-parent-link='${fn:toLowerCase(component.rootNavigationNode.getTitle(enLocale))}' data-link-value='${fn:toLowerCase(level1node.getTitle(enLocale))}'>
						<div class="level1nodeTitle" data-entitle="${enLevel1nodeTitle}"><i class="fa fa-angle-down" aria-hidden="true"></i> ${level1node.title}</div>
					</a>
					<%@ include file="_l2.jsp" %>
				</li>
			</c:if>
		</c:forEach>
	</ul>
</c:if>
<div class="user-channel-toggle">
	<mod:user-channel-toggle />
</div>
