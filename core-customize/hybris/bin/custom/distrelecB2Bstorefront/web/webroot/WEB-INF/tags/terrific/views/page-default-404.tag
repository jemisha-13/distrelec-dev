<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="header" tagdir="/WEB-INF/tags/desktop/common/header" %>
<%@ taglib prefix="footer" tagdir="/WEB-INF/tags/desktop/common/footer" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="namics" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="nam" uri="/WEB-INF/tld/namicscommercetags.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ attribute name="pageTitle" required="false" rtexprvalue="true"%>
<%@ attribute name="bodyCSSClass" required="false" rtexprvalue="true"%>

<c:set var="isInterBodyClass" value="${siteUid eq 'distrelec_FR' ? 'inter' : ''}"/>

<views:document-default pageTitle="${pageTitle}" bodyCSSClass="${bodyCSSClass} ${isInterBodyClass}">

	<c:set var="isInitialVisit" value="false" />
	<c:if test="${shopSettingsCookie.cookieMessageConfirmed == false || empty shopSettingsCookie}">
		<c:set var="isInitialVisit" value="true" />
	</c:if>

	<header>
		<div class="logo-holder">
			<mod:logo skin="error-not-found"/>
		</div>
	</header>

	<div class="body-container">
		<div role="main" id="main" class="ct main-content-right main-content-right--404">
			<jsp:doBody />
		</div>
		<div class="category-nav">
			<cms:slot var="feature" contentSlot="${slots['MainCategoryNav']}">
				<cms:component component="${feature}"/>
			</cms:slot>
		</div>
	</div>

	<footer class="footer" id="footer">
		<c:if test="${not empty footerLinksLangTags}">
			<div class="footer-background">
				<div class="ct">
					<div class="crossWebsiteLinks">
						<c:forEach var="link" items="${footerLinksLangTags}">
							<c:if test="${link.type.code eq 'FOOTER' or link.type.code eq 'ALL'}">
								<a href="${link.href}">${link.countryName}</a> <span>|</span>
							</c:if>
						</c:forEach>
					</div>
				</div>
			</div>
		</c:if>
	</footer>
</views:document-default>
