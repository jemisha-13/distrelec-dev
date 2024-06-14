<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>

<c:set var="bannerClasses" value="${component.styleClass} ${dropDownLayout}" />
<c:set var="level3node_max" value="${component.wrapAfter-1}" />
<c:set var="level0entry" value="${component.navigationNode.entries[0]}" />


<!-- Level 1 Start -->
	<c:if test="${not empty component.navigationNode.children}">
		<ul class="">
			<c:forEach items="${component.navigationNode.children}"	var="level1node">


				<li class="La ${bannerClasses}">
					<c:set var="level1entry" value="${level1node.entries[0]}" />
					<c:choose>
						<c:when test="${level1entry.item.itemtype eq 'ContentPage'}">
							<c:url value="${level1entry.item.label}" var="level1url" />
						</c:when>
						<c:when test="${level1entry.item.itemtype eq 'CMSLinkComponent'}">
							<c:url value="${ycommerce:cmsLinkComponentUrl(level1entry.item, request)}" var="level1url" />
						</c:when>
						<c:when test="${level1entry.item.itemtype eq 'Category'}">
							<c:url value="c/${level1entry.item.code}" var="level1url" />
						</c:when>
						<c:otherwise>
							<c:url value="#" var="level1url" />
						</c:otherwise>
					</c:choose>
					<a href="${level1url}" title="${level1node.title}">${level1node.title}</a>
					
					<!-- Level 2 Start -->
					<c:if test="${not empty level1node.children}">
						<ul class="Lb">
							<c:forEach items="${level1node.children}"	var="level2node">
								<li class="Lb">
									<c:set var="level2entry" value="${level2node.entries[0]}" /> 
									<c:choose>
										<c:when test="${level2entry.item.itemtype eq 'ContentPage'}">
											<c:url value="${level2entry.item.label}" var="level2url" />
										</c:when>
										<c:when test="${level2entry.item.itemtype eq 'CMSLinkComponent'}">
											<c:url value="${ycommerce:cmsLinkComponentUrl(level2entry.item, request)}" var="level2url" />
										</c:when>
										<c:when test="${level2entry.item.itemtype eq 'Category'}">
											<c:url value="c/${level2entry.item.code}" var="level2url" />
										</c:when>
										<c:otherwise>
											<c:url value="#" var="level2url" />
										</c:otherwise>
									</c:choose>
									<a href="${level2url}" title="${level2node.title}">${level2node.title}</a>
									
									<!-- Level 3 Start -->
									<c:if test="${not empty level2node.children}">
										<c:set var="nrOfLevel3nodes" value="${fn:length(level2node.children)}" />
										<ul class="">
											<c:forEach items="${level2node.children}" var="level3node"  begin="0" end="${level3node_max}" >
												<li class="">
													<c:set var="level3entry" value="${level3node.entries[0]}" /> 
													<c:choose>
														<c:when test="${level3entry.item.itemtype eq 'ContentPage'}">
															<c:url value="${level3entry.item.label}" var="level3url" />
														</c:when>
														<c:when test="${level3entry.item.itemtype eq 'CMSLinkComponent'}">
															<c:url value="${ycommerce:cmsLinkComponentUrl(level3entry.item, request)}" var="level3url" />
														</c:when>
														<c:when test="${level3entry.item.itemtype eq 'Category'}">
															<c:url value="c/${level3entry.item.code}" var="level3url" />
														</c:when>
														<c:otherwise>
															<c:url value="#" var="level3url" />
														</c:otherwise>
													</c:choose>
													<a href="${level3url}" title="${level3node.title}">${level3node.title}</a>
											</c:forEach>
											<c:if test="${nrOfLevel3nodes gt level3node_max}">
												<spring:theme htmlEscape="yes" var="moreLabel" code="mainnav.link.more" text="more"/>
												<li><a href="${level2url}" title="${moreLabel}">${moreLabel}</a></li>
											</c:if>
										</ul>
									</c:if>
									<!-- Level 3 End-->
								</li>
							</c:forEach>
						</ul>
					</c:if>
					<!-- Level 2 End-->
				</li>

			</c:forEach>
		</ul>
	</c:if>
	<!-- Level 1 End-->
