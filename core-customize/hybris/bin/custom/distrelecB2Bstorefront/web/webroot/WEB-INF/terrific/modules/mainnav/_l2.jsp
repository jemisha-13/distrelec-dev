<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="level1nodeTitle" value="${fn:toLowerCase(level1node.getTitle(enLocale))}"/>

<c:if test="${not empty level1node.children}">

	<ul class="l2 cf hidden">
		<c:set var="l2SizeLimit" value="8"/>
		<c:set var="level2nodeIndex" value="0" />

		<li class="holder">
			<ul class="holder__item">
				<c:forEach items="${level1node.children}" var="level2node" varStatus="l2Count">
					<c:if test="${l2Count.index < l2SizeLimit}"	>
						<c:set var="level2entry" value="${level2node.entries[0]}" />
						<c:if test="${ycommerce:hasNotOnlyPunchedOutCategories(level2node, request)}">
							<c:set var="clazz" value="e2"/>
							<c:if test="${level2nodeIndex == 0}">
								<li class="l2__border"></li>
							</c:if>
							<c:choose>
								<c:when test="${level2entry.item.itemtype eq 'ContentPage'}">
									<c:url value="${ycommerce:addNavigationSourceToUrl(ycommerce:contentPageUrl(level2entry.item, request), level2node)}" var="level2url" />
									<c:set var="dataInteraction" value="navigation"/>
									<c:set var="dataLinkLocation" value="top nav"/>
									<c:set var="dataParentLink" value="${level1node.uid}"/>
									<c:set var="dataLinkValue" value="${fn:toLowerCase(level2node.getTitle(enLocale))}"/>
									<c:if test="${l2Count.last}">
										<c:set var="clazz" value="e2 promo"/>
									</c:if>
								</c:when>
								<c:when test="${level2entry.item.itemtype eq 'CMSLinkComponent'}">
									<c:url value="${ycommerce:addNavigationSourceToUrl(ycommerce:cmsLinkComponentUrl(level2entry.item, request), level2node)}" var="level2url" />
									<c:set var="dataInteraction" value="navigation"/>
									<c:set var="dataLinkLocation" value="top nav"/>
									<c:set var="dataParentLink" value="${level1node.uid}"/>
									<c:set var="dataLinkValue" value="${fn:toLowerCase(level2node.getTitle(enLocale))}"/>
									<c:if test="${l2Count.last}">
										<c:set var="clazz" value="e2 promo"/>
									</c:if>
								</c:when>
								<c:when test="${level2entry.item.itemtype eq 'Category'}">
									<c:url value="${ycommerce:categoryUrl(level2entry.item, request)}" var="level2url" />
									<c:set var="dataInteraction" value="navigation"/>
									<c:set var="dataLinkLocation" value="top nav"/>
									<c:set var="dataParentLink" value=""/>
									<c:set var="dataLinkValue" value=""/>
								</c:when>
								<c:otherwise>
									<c:url value="#" var="level2url" />
									<c:set var="dataInteraction" value="navigation"/>
									<c:set var="dataLinkLocation" value="top nav"/>
									<c:set var="dataParentLink" value=""/>
									<c:set var="dataLinkValue" value=""/>
								</c:otherwise>
							</c:choose>
							<li class="${clazz}">
								<c:set var="titleValueWt" value="${fn:replace(level2node.title, ' ', '-')}" />
								<c:set var="titleValueWtLower" value="${fn:toLowerCase(titleValueWt)}" />
								<a class="a2" title="${level2node.title}" href="${level2url}"
										<c:if test="${ycommerce:isNotBlank(dataInteraction)}"> data-aainteraction="${dataInteraction}" </c:if>
										<c:if test="${ycommerce:isNotBlank(dataLinkLocation)}"> data-location="${dataLinkLocation}" </c:if>
										<c:if test="${ycommerce:isNotBlank(dataParentLink)}"> data-parent-link="${dataParentLink}" </c:if>
										<c:if test="${ycommerce:isNotBlank(dataLinkValue)}"> data-link-value="${dataLinkValue}"</c:if>

								>${level2node.title}</a>
									<%--<%@ include file="_l3.jsp" %>--%>
							</li>
							<c:set var="level2nodeIndex" value="${level2nodeIndex + 1}" />
						</c:if>
					</c:if>
				</c:forEach>
			</ul>
		</li>
	</ul>
</c:if>
