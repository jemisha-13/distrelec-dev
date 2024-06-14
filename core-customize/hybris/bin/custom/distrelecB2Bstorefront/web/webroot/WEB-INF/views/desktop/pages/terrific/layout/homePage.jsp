<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>

<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oscache" uri="http://www.opensymphony.com/oscache" %>
<%@ taglib prefix="nam" uri="/WEB-INF/tld/namicscommercetags.tld" %>

<views:page-default-home pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-home skin-layout-wide" >
	<div class="container">
		<div id="breadcrumb" class="breadcrumb">
			<div class="spacer"></div>
		</div>
		<mod:global-messages/>
	</div>

	<c:set var="cachekey" value="content-${cmsPage.pk}-${cachingKeyHomepage}" />
	<c:if test="${param.flush eq 'homepage'}">
		<oscache:flush key="${cachekey}" scope="application" language="${currentLanguage.isocode}" />
	</c:if>
	<oscache:cache key="${cachekey}" time="${cachingTimeHomepage}" scope="application" language="${currentLanguage.isocode}">
    	<!-- Render time (Homepage): ${currentDateTime}, cached for ${cachingTimeHomepage} seconds with key ${cachekey} -->
	    <div class="ct margin-top-20">
			<c:choose>
				<c:when test="${not empty slots['HomeDirectOrder']}">
					<div class="row">
						<div class="gu-12">
							<cms:slot var="feature" contentSlot="${slots['TitleContent']}">
								<cms:component component="${feature}"/>
							</cms:slot>
						</div>
					</div>
					<div class="row">
						<div class="gu-9">
							<cms:slot var="feature" contentSlot="${slots['HeroContent']}">
								<cms:component component="${feature}"/>
							</cms:slot>
						</div>
						<div class="gu-3">
							<cms:slot var="feature" contentSlot="${slots['HomeDirectOrder']}">
								<mod:cart-directorder template="component" skin="component"/>
							</cms:slot>
						</div>
					</div>
					<div class="row">
						<div class="gu-12">
							<cms:slot var="feature" contentSlot="${slots['CategoryGrid']}">
								<cms:component component="${feature}"/>
							</cms:slot>
							<cms:slot var="feature" contentSlot="${slots['Content']}">
								<cms:component component="${feature}"/>
							</cms:slot>
						</div>
					</div>
					<div class="row ManufacturerLinecardComponent">
						<div class="gu-12">
							<cms:slot var="feature" contentSlot="${slots['ManufacturerLinecardComponent']}">
								<cms:component component="${feature}"/>
							</cms:slot>
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<div class="row">
						<div class="gu-12">
							<cms:slot var="feature" contentSlot="${slots['TitleContent']}">
								<cms:component component="${feature}"/>
							</cms:slot>
							<cms:slot var="feature" contentSlot="${slots['HeroContent']}">
								<cms:component component="${feature}"/>
							</cms:slot>
							<cms:slot var="feature" contentSlot="${slots['CategoryGrid']}">
								<cms:component component="${feature}"/>
							</cms:slot>
							<cms:slot var="feature" contentSlot="${slots['Content']}">
								<cms:component component="${feature}"/>
							</cms:slot>
							<cms:slot var="feature" contentSlot="${slots['ManufacturerLinecardComponent']}">
								<cms:component component="${feature}"/>
							</cms:slot>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
	    </div>
	</oscache:cache>
</views:page-default-home>
