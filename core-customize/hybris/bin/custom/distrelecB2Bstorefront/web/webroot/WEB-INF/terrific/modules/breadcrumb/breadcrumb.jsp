<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:url value="/" var="homeUrl" />
<div class="ct">
	<div class="bd">
		<ul class="bc-list js-hide-items" data-max-length="80">
			<li class="bc-item">
				<a class="bc-link" href="${homeUrl}">
					<span class="breadcrumb-text 1">
						<spring:message code="breadcrumb.home" />
					</span>
				</a>
			</li>
			
			<c:set value="${siteBaseURL}" var="baseUrl" />
			<c:forEach items="${breadcrumbs}" var="breadcrumb" varStatus="status">
				<c:choose>
					<c:when test="${breadcrumb.linkClass ne 'return'}">
					 	<li class="bc-item ${breadcrumb.linkClass}">
		                    <span class="breadcrumb-separator"><i class="fa fa-angle-right" aria-hidden="true"></i></span>
					 		<c:choose>
			                    <c:when test="${breadcrumb.url eq '#'}">
									<c:set var="urlLink" value="${breadcrumb.url}" />
			                        <span class="bc-link bc-link--nohover">
			                        	<span class="breadcrumb-text 2">
			                        		${fn:escapeXml(breadcrumb.name)}
			                        	</span>
			                        </span>
			                    </c:when>
			                    <c:otherwise>
			                        <c:url value="${breadcrumb.url}" var="breadcrumbUrl"/>
			                        <c:choose>
										<c:when test="${fn:contains(breadcrumbUrl , baseUrl)}">
											<c:set var="urlLink" value="${breadcrumbUrl}" />
										</c:when>
										<c:otherwise>
											<c:set var="urlLink" value="${baseUrl}${fn:startsWith(breadcrumbUrl, '/') ? '' : '/'}${breadcrumbUrl}" />
										</c:otherwise>
									</c:choose>			
			                        <a class="bc-link" href="${urlLink}">
			                        	<c:choose>
			                        		<c:when test="${fn:contains(pageContext.request.requestURI , 'manufacturerStoreDetailPage.jsp') && status.last}">
												<h1 class="breadcrumb-text special-h1">
													${fn:escapeXml(breadcrumb.name)} 
												</h1>				                        		
			                        		</c:when>
			                        		<c:otherwise>
												<span class="breadcrumb-text 3">
													${fn:escapeXml(breadcrumb.name)} 
												</span>			                        		
			                        		</c:otherwise>
			                        	</c:choose>
								    </a>
			                    </c:otherwise>
		                    </c:choose>
	            	    </li>
					</c:when>
					<c:otherwise>
						<c:set var="returnUrl" value="${breadcrumb.url}" />
						<c:set var="returnText" value="${breadcrumb.name}" />
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</ul>
		<c:if test="${returnUrl ne null}">
			<c:url var="breadcrumbUrl" value="${returnUrl}" />
			<c:choose>
				<c:when test="${fn:contains(breadcrumbUrl , baseUrl)}">
					<c:set var="urlLink" value="${breadcrumbUrl}" />
				</c:when>
				<c:otherwise>
					<c:set var="urlLink" value="${baseUrl}${fn:startsWith(breadcrumbUrl, '/') ? '' : '/'}${breadcrumbUrl}" />
				</c:otherwise>
			</c:choose>			
			<div class="return">
				<a class="bc-link" href="${urlLink}">
					<span class="breadcrumb-text 4">
						${fn:escapeXml(returnText)}
					</span>
			    </a>
			</div>
		</c:if>
	</div>
</div>

<mod:schema template="breadcrumb" />
