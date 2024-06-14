<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<spring:eval expression="new java.util.Locale('en')" var="enLocale" />
<spring:theme code="footer-links.title" text="Direct Access" var="sTitle" />
<spring:theme code="footer-links.more" text="more" var="sMore" />

<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_OCICUSTOMERGROUP') or hasRole('ROLE_ARIBACUSTOMERGROUP')">
	<c:set var="isOCI" value="true" />
</sec:authorize>

<c:choose>
	<c:when test="${customFooterEnabled}">
		<mod:impressum impressumLinkList="${footerComponentData.impressumLinks}" />
	</c:when>
	<c:otherwise>
		<c:forEach items="${navigationNodes}" var="level1node" varStatus="status">

			<c:choose>
				<c:when test="${level1node.title eq 'Manufacturer Stores'}">
					<c:set var="footerTitle" value="nav-footer-section--manufacturer-stores" />
				</c:when>
				<c:when test="${level1node.title eq 'Most searched'}">
					<c:set var="footerTitle" value="nav-footer-section--most-searched" />
				</c:when>
				<c:otherwise>
					<c:set var="footerTitle" value="" />
				</c:otherwise>
			</c:choose>

			<section class="nav-footer-section nav-footer-section-OCI col-md-4 col-lg  ${footerTitle} ">
				<c:set var="nrOfLevel2nodes" value="${fn:length(level1node.children)}" />
				<c:set var="level2node_max" value="${fn:length(level1node.children)}" />
				<c:if test="${wrapAfter > 0}">
					<c:set var="level2node_max" value="${wrapAfter-1}" />
				</c:if>
				<c:set var="level1entry" value="${level1node.entries[0]}" />
				<c:choose>
					<c:when test="${level1entry.item.itemtype eq 'ContentPage'}">
						<c:url value="${ycommerce:contentPageUrl(level1entry.item, request)}" var="level1url" />
					</c:when>
					<c:when test="${level1entry.item.itemtype eq 'CMSLinkComponent'}">
						<c:url value="${ycommerce:cmsLinkComponentUrl(level1entry.item, request)}" var="level1url" />
					</c:when>
					<c:otherwise>
						<c:url var="level1url" value="" />
					</c:otherwise>
				</c:choose>
				<c:choose>
					<c:when test="${not empty level1url}">
						<c:set var="levelValueName" value="${fn:replace(level1node.title, ' ', '-')}" />
						<c:set var="levelValueNameLower" value="${fn:toLowerCase(levelValueName)}" />
						<h3 class="nav-title">
							<a href="${level1url}" title="${level1node.title}" class="link-nav-title" data-aainteraction='footer navigation' data-location='footer nav'  data-link-value='${fn:toLowerCase(level1node.getTitle(enLocale))}'>${level1node.title}</a>
						</h3>
					</c:when>
					<c:otherwise>
						<h3 class="nav-title">${level1node.title}</h3>
					</c:otherwise>
				</c:choose>
				<ul class="nav-footer-list">
					<c:forEach items="${level1node.children}" var="level2node" begin="0" end="${level2node_max}">
						<c:set var="level2entry" value="${level2node.entries[0]}" />
						<c:choose>
							<c:when test="${level2entry.item.itemtype eq 'ContentPage'}">
								<c:url var="level2url" value="${ycommerce:contentPageUrl(level2entry.item, request)}" />
							</c:when>
							<c:when test="${level2entry.item.itemtype eq 'CMSLinkComponent'}">
								<c:url var="level2url" value="${ycommerce:cmsLinkComponentUrl(level2entry.item, request)}" />
								<%-- Hack for disabling BOM-tool for OCI and Ariba customers --%>
								<c:if test="${fn:contains(level2url, 'bom-tool') or fn:contains(level2url, 'import-tool')}">
									<c:url var="level2url" value="" />
								</c:if>
							</c:when>
							<c:otherwise>
								<c:url var="level2url" value="" />
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${not empty level2url}">
								<c:set var="levelValueName" value="${fn:replace(level2node.title, ' ', '-')}" />
								<c:set var="levelValueNameLower" value="${fn:toLowerCase(levelValueName)}" />
								<li><a href="${level2url}" title="${level2node.title}" data-aainteraction='footer navigation' data-location='footer nav' data-parent-link='${fn:toLowerCase(level1node.getTitle(enLocale))}' data-link-value='${fn:toLowerCase(level2node.getTitle(enLocale))}'>
									<span>${level2node.title}</span>
								</a></li>
							</c:when>
							<c:otherwise>
								<c:set var="nrOfLevel2nodes" value="${nrOfLevel2nodes - 1}" />
							</c:otherwise>
						</c:choose>
					</c:forEach>
					<c:if test="${nrOfLevel2nodes gt (level2node_max + 1) && not empty level1url}">
						<li><a class="link-more" href="${level1url}">
							<i></i>
							<span>${sMore}</span>
						</a></li>
					</c:if>
				</ul>
			</section>
		</c:forEach>

	</c:otherwise>
</c:choose>
