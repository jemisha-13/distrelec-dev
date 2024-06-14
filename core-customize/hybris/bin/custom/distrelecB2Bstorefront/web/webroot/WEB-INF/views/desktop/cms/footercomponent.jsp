<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>

<div id="footer">
	<ul class="Fa">
		<c:forEach items="${navigationNodes}" var="level1node">
			<li>
				<ul class="Fb">
					<c:set var="level2node_max" value="${component.wrapAfter-1}" />
					<c:set var="nrOfLevel2nodes" value="${fn:length(level1node.children)}" />
					<c:set var="level1entry" value="${level1node.entries[0]}" />
					<c:choose>
						<c:when test="${level1entry.item.itemtype eq 'ContentPage'}">
							<c:url value="${level1entry.item.label}" var="level1url" />
						</c:when>
						<c:when test="${level1entry.item.itemtype eq 'CMSLinkComponent'}">
							<c:url value="${ycommerce:cmsLinkComponentUrl(level1entry.item, request)}" var="level1url" />
						</c:when>
						<c:otherwise>
							<c:url value="" var="level1url" />
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${(nrOfLevel2nodes gt level2node_max)  }">
							<h3><a href="${level1url}">${level1node.title}</a></h3>
						</c:when>
						<c:otherwise>
							<h3>${level1node.title}</h3>
						</c:otherwise>
					</c:choose>
					<c:forEach items="${level1node.children}" var="level2node"  begin="0" end="${level2node_max}" varStatus="i">
						<li>
							<c:set var="level2entry" value="${level2node.entries[0]}" />
							<c:choose>
								<c:when test="${level2entry.item.itemtype eq 'ContentPage'}">
									<c:url value="${level2entry.item.label}" var="level2url" />
								</c:when>
								<c:when test="${level2entry.item.itemtype eq 'CMSLinkComponent'}">
									<c:url value="${ycommerce:cmsLinkComponentUrl(level2entry.item, request)}" var="level2url" />
								</c:when>
								<c:otherwise>
									<c:url value="#" var="level2url" />
								</c:otherwise>
							</c:choose>
							<a href="${level2url}" title="${level2node.title}">${level2node.title}</a>
						</li>
					</c:forEach>
					<c:if test="${nrOfLevel2nodes gt level2node_max}">
						<li><a href="${level1url}"><spring:theme code="footer-links.more" text="more"/></a></li>
					</c:if>
				</ul>
			</li>
		</c:forEach>
	</ul>
</div>


