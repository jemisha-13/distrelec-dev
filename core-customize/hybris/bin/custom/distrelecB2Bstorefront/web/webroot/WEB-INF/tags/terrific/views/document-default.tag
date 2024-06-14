<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ attribute name="pageTitle" required="false" rtexprvalue="true"%>
<%@ attribute name="bodyCSSClass" required="false" rtexprvalue="true"%>
<%@ taglib prefix="ldjson" uri="http://www.atg.com/taglibs/json" %>
<%@ taglib prefix="oscache" uri="http://www.opensymphony.com/oscache" %>
<%@ taglib prefix="smartedit" tagdir="/WEB-INF/tags/desktop/smartedit" %>
<%@ taglib tagdir="/WEB-INF/tags/shared/analytics" prefix="analytics" %>

<c:set var="env" value="${jalosession.tenant.config.getParameter('environment')}"/>
<c:set var="isStaging" value="${not empty env and env eq 'staging'}"/>

<spring:eval expression="@configurationService.configuration.getString('isLocal')" var="isLocal" scope="application" />
<%-- Hardcoded values for prod during migration as the property files are now used for Headless --%>
<c:set var="gtmId" value=""/>
<c:set var="gtmAuth" value=""/>
<c:set var="gtmPreview" value=""/>
<c:set var="gtmCookies" value=""/>
<c:choose>
    <c:when test="${isProd}">
        <c:set var="gtmId" value="GTM-K7Z2SKD" />
        <c:set var="gtmPreview" value="env-1" />
        <c:set var="gtmAuth" value="5SmGNjY6cmByPZk5m3Z3Pw"/>
        <c:set var="gtmCookies" value="x"/>
    </c:when>
    <c:when test="${isStaging}">
    
       	<spring:eval expression="@configurationService.configuration.getString('gtm.tag.id')" var="gtmId" scope="application" />
		<spring:eval expression="@configurationService.configuration.getString('gtm.tag.auth')" var="gtmAuth" scope="application" />
		<spring:eval expression="@configurationService.configuration.getString('gtm.tag.preview')" var="gtmPreview" scope="application" />
		<spring:eval expression="@configurationService.configuration.getString('gtm.tag.cookies_win')" var="gtmCookies" scope="application" />
	 </c:when>
</c:choose>

<!DOCTYPE html>
<!--[if lt IE 7]><html class="no-js lt-ie10 lt-ie9 lt-ie8 lt-ie7" lang="${currentLanguage.isocode}"><![endif]-->
<!--[if IE 7]><html class="no-js lt-ie10 lt-ie9 lt-ie8 isie7" lang="${currentLanguage.isocode}"><![endif]-->
<!--[if IE 8]><html class="no-js lt-ie10 lt-ie9 isie8" lang="${currentLanguage.isocode}"><![endif]-->
<!--[if IE 9]><html class="no-js ie9 lt-ie10" lang="${currentLanguage.isocode}"><![endif]-->
<!--[if gt IE 9]><!-->
<html class="no-js" lang="${currentLanguage.isocode}" prefix="og: http://ogp.me/ns# fb: http://ogp.me/ns/fb#" data-gtm-id="${gtmId}" data-gtm-auth="${gtmAuth}" data-gtm-preview="${gtmPreview}" data-gtm-cookies_win="${gtmCookies}">
<!--<![endif]-->
<!--[if (lt IE 10) & (IEMobile)]><html class="no-js ieMobile lt-ie10" lang="${currentLanguage.isocode}"><![endif]-->

<%-- View port initialer for responsive --%>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">

<%-- needed for nexus ensighten per country localized urls --%>
<c:set var="replacedSiteUid" value="${fn:replace(siteUid, '_', '')}" />

<%-- set isOCI var --%>
<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<c:set var="isOCI" value="true" />
</sec:authorize>

<%-- Flush all cache if page is edited from Smartedit --%>
<c:if test="${not empty param.cmsTicketId}">
	<oscache:flush scope="application" />
</c:if>

<c:set var="headTitle" value="${not empty metaData.title ? metaData.title : not empty pageTitle ? pageTitle : not empty cmsPage.title ? cmsPage.title : 'Accelerator Title'}" />

<head>
	<meta name="google" content="notranslate">
	<%-- First escaping removes markup, second escapes ">" and "<" characters --%>
	<title>${headTitle}</title>
<%-- Google site verification --%>
<c:if test="${(currentCountry.isocode eq 'EX' || currentCountry.isocode eq 'HU' || currentCountry.isocode eq 'NL')}">
     <meta name="google-site-verification" content="<spring:message code="google.site.console.verification" />" />
</c:if>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="description" content="${not empty metaData.description ? metaData.description : metaDescription}" />
<meta name="robots" content="${not empty metaData.robots ? metaData.robots : metaRobots}" />
<%-- DISTRELEC-10602: Turn off telephone number detection. --%>
<meta name="format-detection" content="telephone=no">
<c:if test="${not empty ogProductTitle}">
	<%-- Open Graph Meta Data --%>
	<meta property="og:site_name" content="${ogSiteName}" />
	<meta property="og:locale" content="${currentLanguage.isocode}" />
	<meta property="og:title" content="${ogProductTitle}" />
	<meta property="og:image" content="${ogProductImage}" />
	<meta property="og:description" content="${ogProductDescription}" />
	<meta property="og:type" content="website" />
	<%-- Open Graph Meta Data --%>
</c:if>
<c:if test="${!fn:contains(bodyCSSClass, 'skin-layout-home')}">
	<meta name="google" content="nositelinkssearchbox" />
</c:if>

<meta name="_csrf" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>

<script src="https://www.googleoptimize.com/optimize.js?id=OPT-MRRJ7X4"></script>

<c:if test="${not empty digitaldatalayer}">
    <script type="text/javascript">
        var digitalData = ${digitaldatalayer};
    </script>
</c:if>

<c:if test="${siteUid ne 'distrelec_FR'}">
	<c:if test="${not empty headerLinksLangTags}">
		<c:forEach var="link" items="${headerLinksLangTags}">
			<c:if test="${link.href ne '/'}">
				<c:if test="${link.type.code eq 'HEADER' or link.type.code eq 'ALL'}">
					<link rel="${link.rel}" href="${link.href}" hreflang="${link.hreflang}" />
				</c:if>
			</c:if>
		</c:forEach>
	</c:if>
</c:if>

<c:if test="${not empty headerMobileLinksTags}">
	<c:forEach var="mobileLink" items="${headerMobileLinksTags}">
		<c:if test="${currentCountry.isocode ne 'BE'}">
			<link rel="${mobileLink.rel}" href="${mobileLink.href}" media="${mobileLink.mediaQuery}" />
		</c:if>
	</c:forEach>
</c:if>

<c:if test="${not empty headerCanonicalLinksTags}">
	<c:forEach var="link" items="${headerCanonicalLinksTags}">
		<link rel="${link.rel}" href="${link.href}" />
	</c:forEach>
</c:if>

<c:if test="${not empty categoryPaginationLinksTag}">
	<c:forEach var="link" items="${categoryPaginationLinksTag}">
		<link rel="${link.rel}" href="${link.href}"/>
	</c:forEach>
</c:if>
	<%-- Google Material Icons --%>
	<link rel="preconnect" href="//fonts.googleapis.com">
	<link rel="preconnect" href="//fonts.gstatic.com">
	<link href="https://fonts.googleapis.com/icon?family=Material+Icons+Round&display=swap" rel="stylesheet">

	<link rel="apple-touch-icon prefetch" as="image" type="image/x-icon" href="/_ui/all/media/apple-touch-icon-precomposed.png">
	<link rel="icon prefetch" as="image" type="image/x-icon" href="/_ui/all/media/favicon.ico" sizes="16X16">
	<!--[if IE]><link rel="icon prefetch" as="image" type="image/x-icon" href="/_ui/all/media/favicon.ico" sizes="16X16"><![endif]-->
<c:choose>
	<c:when test="${isOCI}">
		<link rel="preload" as="font" href="/_ui/all/fonts/fa-brands-400.ttf" type="font/ttf" crossorigin>
		<link rel="preload" as="font" href="/_ui/all/fonts/fa-regular-400.ttf" type="font/ttf" crossorigin>
		<link rel="preload" as="font" href="/_ui/all/fonts/fa-solid-900.ttf" type="font/ttf" crossorigin>
	</c:when>
	<c:otherwise>
		<link rel="preload" as="font" href="/_ui/all/fonts/fa-brands-400.woff2" type="font/woff2" crossorigin>
		<link rel="preload" as="font" href="/_ui/all/fonts/fa-regular-400.woff2" type="font/woff2" crossorigin>
		<link rel="preload" as="font" href="/_ui/all/fonts/fa-solid-900.woff2" type="font/woff2" crossorigin>
	</c:otherwise>
</c:choose>

    <link rel="preload" as="font" href="/_ui/all/fonts/Montserrat-Regular.woff2" type="font/woff2" crossorigin>
    <link rel="preload" as="font" href="/_ui/all/fonts/Montserrat-Medium.woff2" type="font/woff2" crossorigin>
    <link rel="preload" as="font" href="/_ui/all/fonts/Montserrat-SemiBold.woff2" type="font/woff2" crossorigin>
    <link rel="preload" as="font" href="/_ui/all/fonts/Montserrat-Bold.woff2" type="font/woff2" crossorigin>

<%-- Due to IE7/IE8 tag imports issue, this scripts needs to be loaded here before any CSS file import --%>
<c:choose>
	<c:when test="${isOCI}">
		<!--[if IE ]>
			<script type="text/javascript" src="/_ui/all/cache/dyn-html5shiv-3.7.4-with-printshiv.min.js"></script>
			<script src="/_ui/all/matterhorn/js/gistfile1.min.js"></script>
		<![endif]-->
	</c:when>
	<c:otherwise>
		<!--[if lte IE 9]><script src="/_ui/all/cache/dyn-html5shiv-3.7.4-with-printshiv.min.js"></script><![endif]-->
	</c:otherwise>
</c:choose>
<c:if test="${isProd}">
<!-- CHEQ invocation tag - DISTRELEC-27323 -->
	<script async src="https://eraser.thesmilingpencils.com/i/8a1b68c74fac63154312522bef9ff4e7.js" data-ch="cheq4ppc" class="ct_clicktrue_17772" data-jsonp="onCheqResponse"></script>
<!-- End CHEQ invocation tag -->
</c:if>
<c:choose>
	<c:when test="${isProd or isStaging}">
        <c:choose>
            <c:when test="${isOCI}">
				<link href="/_ui/all/cache/base.min.css?ck=${currentKey}" media="all" rel="stylesheet" as="style" type="text/css" />
				<link href="/_ui/all/matterhorn-oci/main.min.css?ck=${currentKey}" rel="stylesheet" type="text/css" />
            </c:when>
            <c:otherwise>
				<link href="/_ui/all/cache/base.min.css?ck=${currentKey}" media="all" rel="stylesheet preload" as="style" type="text/css" />
				<c:choose>
					<c:when test="${fn:contains(bodyCSSClass, 'skin-layout-home')}">
						<link href="/_ui/all/matterhorn/layout-core.min.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
						<link href="/_ui/all/matterhorn/layout-homepage.min.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
					</c:when>
					<c:when test="${fn:contains(wtPageId, 'productDetails')}">
						<link href="/_ui/all/matterhorn/layout-core.min.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
						<link href="/_ui/all/matterhorn/layout-pdp.min.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
					</c:when>
					<c:when test="${fn:contains(bodyCSSClass, 'skin-layout-product-list')}">
						<link href="/_ui/all/matterhorn/layout-core.min.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
						<link href="/_ui/all/matterhorn/layout-product-list.min.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
					</c:when>
					<c:when test="${fn:contains(bodyCSSClass, 'skin-layout-category')}">
						<link href="/_ui/all/matterhorn/layout-core.min.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
						<link href="/_ui/all/matterhorn/layout-category.min.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
					</c:when>
					<c:when test="${fn:contains(bodyCSSClass, 'skin-layout-manufacturer')}">
						<link href="/_ui/all/matterhorn/layout-core.min.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
						<link href="/_ui/all/matterhorn/layout-manufacturer.min.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
					</c:when>
					<c:when test="${fn:contains(bodyCSSClass, 'skin-layout-cart')}">
						<link href="/_ui/all/matterhorn/layout-core.min.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
						<link href="/_ui/all/matterhorn/layout-cart.min.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
					</c:when>
					<c:when test="${fn:contains(bodyCSSClass, 'mod-layout--checkout')}">
						<link href="/_ui/all/matterhorn/layout-core.min.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
						<link href="/_ui/all/matterhorn/layout-checkout.min.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />

						<c:if test="${fn:contains(bodyCSSClass, 'mod-layout--checkout-registration')}">
							<link href="/_ui/all/matterhorn/layout-registration.min.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
						</c:if>
					</c:when>
					<c:when test="${fn:contains(wtPageId, 'checkout-registration') || fn:contains(wtPageId, 'registration')}">
						<link href="/_ui/all/matterhorn/layout-core.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
						<link href="/_ui/all/matterhorn/layout-registration.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
					</c:when>
					<c:otherwise>
						<link href="/_ui/all/matterhorn/main.min.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
					</c:otherwise>
				</c:choose>

				<c:if test="${fn:contains(bodyCSSClass, 'skin-layout-home')}">
					<link rel="stylesheet prefetch" as="style" href="/_ui/all/matterhorn/css/jquery.bxslider.min.css">
				</c:if>

				<c:choose>
					<c:when test="${isProd}">
						<script rel="preload" src="//ensighten.distrelec.com/distrelec/prod/Bootstrap.js"></script>
					</c:when>
					<c:otherwise>
						<script rel="preload" src="//ensighten.distrelec.com/distrelec/stage/Bootstrap.js"></script>
					</c:otherwise>
				</c:choose>

				<c:choose>
					<c:when test="${isProd}">
						<!-- Google Tag Manager -->
							<script>
								(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
								new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
								j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
								'https://www.googletagmanager.com/gtm.js?id='+i+dl+ '&gtm_auth=5SmGNjY6cmByPZk5m3Z3Pw&gtm_preview=env-1&gtm_cookies_win=x';f.parentNode.insertBefore(j,f);
								})(window,document,'script','dataLayer','GTM-K7Z2SKD');
							</script>
						<!-- End Google Tag Manager -->
					</c:when>
					<c:when test="${isStaging}">
						<!-- Google Tag Manager -->
							<script>
								(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
								new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
								j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
								'https://www.googletagmanager.com/gtm.js?id='+i+dl+ '&gtm_auth=5SmGNjY6cmByPZk5m3Z3Pw&gtm_preview=env-1&gtm_cookies_win=x';f.parentNode.insertBefore(j,f);
								})(window,document,'script','dataLayer','GTM-K7Z2SKD');
							</script>
						<!-- End Google Tag Manager -->
					</c:when>
				</c:choose>

            </c:otherwise>
        </c:choose>
    </c:when>
	<c:otherwise>
        <c:choose>
            <c:when test="${isOCI}">
				<link href="/_ui/all/cache/base.css?ck=${currentKey}" media="all" rel="stylesheet" as="style" type="text/css" />
				<link href="/_ui/all/matterhorn-oci/main.css?ck=${currentKey}" rel="stylesheet" type="text/css" />
            </c:when>
            <c:otherwise>
				<link href="/_ui/all/cache/base.css?ck=${currentKey}" media="all" rel="stylesheet preload" as="style" type="text/css" />
				<c:choose>
					<c:when test="${fn:contains(bodyCSSClass, 'skin-layout-home')}">
						<link href="/_ui/all/matterhorn/layout-core.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
						<link href="/_ui/all/matterhorn/layout-homepage.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
					</c:when>
					<c:when test="${fn:contains(wtPageId, 'productDetails')}">
						<link href="/_ui/all/matterhorn/layout-core.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
						<link href="/_ui/all/matterhorn/layout-pdp.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
					</c:when>
					<c:when test="${fn:contains(bodyCSSClass, 'skin-layout-product-list')}">
						<link href="/_ui/all/matterhorn/layout-core.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
						<link href="/_ui/all/matterhorn/layout-product-list.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
					</c:when>
					<c:when test="${fn:contains(bodyCSSClass, 'skin-layout-category')}">
						<link href="/_ui/all/matterhorn/layout-core.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
						<link href="/_ui/all/matterhorn/layout-category.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
					</c:when>
					<c:when test="${fn:contains(bodyCSSClass, 'skin-layout-manufacturer')}">
						<link href="/_ui/all/matterhorn/layout-core.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
						<link href="/_ui/all/matterhorn/layout-manufacturer.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
					</c:when>
					<c:when test="${fn:contains(bodyCSSClass, 'skin-layout-cart')}">
						<link href="/_ui/all/matterhorn/layout-core.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
						<link href="/_ui/all/matterhorn/layout-cart.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
					</c:when>
					<c:when test="${fn:contains(bodyCSSClass, 'mod-layout--checkout')}">
						<link href="/_ui/all/matterhorn/layout-core.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
						<link href="/_ui/all/matterhorn/layout-checkout.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />

						<c:if test="${fn:contains(bodyCSSClass, 'mod-layout--checkout-registration')}">
							<link href="/_ui/all/matterhorn/layout-registration.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
						</c:if>
					</c:when>
					<c:when test="${fn:contains(wtPageId, 'checkout-registration') || fn:contains(wtPageId, 'registration')}">
						<link href="/_ui/all/matterhorn/layout-core.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
						<link href="/_ui/all/matterhorn/layout-registration.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
					</c:when>
					<c:otherwise>
						<link href="/_ui/all/matterhorn/main.css?ck=${currentKey}" rel="preload stylesheet" as="style" type="text/css" />
					</c:otherwise>
				</c:choose>

				<script rel="preload" src="//ensighten.distrelec.com/distrelec/stage/Bootstrap.js"></script>
				<c:if test="${fn:contains(bodyCSSClass, 'skin-layout-home')}">
					<link rel="stylesheet prefetch" as="style" href="/_ui/all/matterhorn/css/jquery.bxslider.min.css">
				</c:if>
            </c:otherwise>
        </c:choose>
	</c:otherwise>
</c:choose>
<c:if test="${fn:contains(bodyCSSClass, 'mod-layout--standalone')}">
	<link rel="preconnect" crossorigin href="https://plus.dnb.com/" />
</c:if>

<spring:eval expression="@configurationService.configuration.getString('factfinder.address')" var="ffUrl" scope="application" />
<link rel="preconnect" crossorigin href="https://${ffUrl}/" />
</head>
<body class="${pageBodyCssClasses} ${bodyCSSClass} ${eProcurementCSSClass} ${user.userProfileData.userProfileName}" data-isocode="${currentCountry.isocode}">
	<c:choose>
		<c:when test="${isProd}">
			<!-- Google Tag Manager (noscript) -->
				<noscript>
					<iframe src="https://www.googletagmanager.com/ns.html?id=GTM-K7Z2SKD&gtm_auth=5SmGNjY6cmByPZk5m3Z3Pw&gtm_preview=env-1&gtm_cookies_win=x"
					height="0" width="0" style="display:none;visibility:hidden"></iframe>
				</noscript>
			<!-- End Google Tag Manager (noscript) -->
		</c:when>
		<c:when test="${isStaging}">
			<!-- Google Tag Manager (noscript) -->
			<noscript><iframe src="https://www.googletagmanager.com/ns.html?id=GTM-K7Z2SKD&gtm_auth=5SmGNjY6cmByPZk5m3Z3Pw&gtm_preview=env-1&gtm_cookies_win=x"
			height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
			<!-- End Google Tag Manager (noscript) -->
		</c:when>
	</c:choose>
	

	<!-- CHEQ invocation tag (noscript) -->
	<c:if test="${isProd}">
	<noscript>
		<iframe src="https://pen.thesmilingpencils.com/ns/8a1b68c74fac63154312522bef9ff4e7.html?ch=cheq4ppc" width="0" height="0" style="display:none"></iframe>
	</noscript>
	</c:if>
	<!-- End CHEQ invocation tag (noscript) -->
	<jsp:doBody />

	<div class="global">
		<mod:lightbox-login-required-import-tool />
		<mod:lightbox-login-required />
		<mod:lightbox-shoppinglist />
		<mod:lightbox-status />
		<mod:lightbox-yes-no />
		<mod:lightbox-video />
		<mod:backenddata attributes="id='backenddata'" />
        <mod:hotjar attributes="id='hotjardata'" />
        <c:if test="${marketingPopup == true and not fn:contains(request.getAttribute('javax.servlet.forward.request_uri'), '/cms/agb') and not fn:contains(request.getAttribute('javax.servlet.forward.request_uri'), '/cms/datenschutz')}">
            <mod:lightbox-login-consent-form />
        </c:if>
        <mod:lightbox-double-optin-notice />
        <mod:lightbox-double-optin-reminder />
		<mod:global-utils />
		<c:if test="${not isOCI && isShowSubscribePopup}">
			<mod:subscribe-pop-up />
		</c:if>

		<c:set var="isInitialVisit" value="false" />

		<c:if test="${(empty shopSettingsCookie || shopSettingsCookie.cookieMessageConfirmed == false) && (isOCI)}">
			<c:set var="isInitialVisit" value="true" />
			<mod:cookie-notice />
		</c:if>

	</div>

	<c:if test="${isOCI}">
		<script type="text/javascript">
			if(document.documentElement.style.overflow !== "auto") {
				document.documentElement.style.overflow = "auto";
			}
		</script>
	</c:if>

	<c:choose>
		<c:when test="${isProd or isStaging}">
			<script>
				if (!('IntersectionObserver' in window)) {
					var script = document.createElement("script");
					script.src = "/_ui/all/matterhorn/js/intersection-observer.js";
					document.getElementsByTagName('head')[0].appendChild(script);
				}
			</script>
			<script rel="preload" src="/_ui/all/cache/jquery-1.12.3.min.js?ck=${currentKey}"></script>
			<script type="text/javascript" src="/_ui/all/matterhorn/js/jquery.hoverIntent.min.js?ck=${currentKey}"></script>

			<c:if test="${not isOCI}">
				<script>
					// Lighthouse recommendation to not use passive listeners so scroll performance could be improved (should be loaded right after jQuery)
					// Passive event listeners
					jQuery.event.special.touchstart = {
						setup: function( _, ns, handle ) {
							this.addEventListener("touchstart", handle, { passive: !ns.includes("noPreventDefault") });
						}
					};
					jQuery.event.special.touchmove = {
						setup: function( _, ns, handle ) {
							this.addEventListener("touchmove", handle, { passive: !ns.includes("noPreventDefault") });
						}
					};

					// To avoid flicker of material-icons since we are loding them from Google
					$(window).load(function() {
						$('.material-icons-round').css('opacity', '1');
					});
				</script>

				<c:if test="${fn:contains(wtPageId, 'productDetails') || fn:contains(wtPageId, 'product-family-default')}">
					<script rel="prefetch" src="/_ui/all/matterhorn/js/magiczoom.min.js?ck=${currentKey}"></script>
				</c:if>
				<c:if test="${fn:contains(wtPageId, 'checkout-registration') || fn:contains(wtPageId, 'registration') || fn:contains(wtPageId, 'orderConfirmationPage')}">
					<script rel="prefetch" src="/_ui/all/matterhorn/js/password-strength.js?ck=${currentKey}" defer></script>
				</c:if>
				<script rel="preload" src="/_ui/desktop/common/js/qs.js?ck=${currentKey}" defer></script>
				<script rel="preload" src="/_ui/desktop/common/js/axios.min.js?ck=${currentKey}" defer></script>
				<script rel="preload" src="/_ui/desktop/common/js/vue.min.js?ck=${currentKey}" defer></script>
				<script rel="preload" src="/_ui/all/matterhorn/js/polyfill.min.js?ck=${currentKey}" defer></script>
				<script rel="preload" src="/_ui/desktop/common/js/loadash.js?ck=${currentKey}" defer></script>

				<c:choose>
					<c:when test="${isProd}">
						<script rel="preload" src="//ensighten.distrelec.com/distrelec/prod/Bootstrap.js"></script>
					</c:when>
					<c:otherwise>
						<script rel="preload" src="//ensighten.distrelec.com/distrelec/stage/Bootstrap.js"></script>
					</c:otherwise>
				</c:choose>

				<c:if test="${fn:contains(bodyCSSClass, 'skin-layout-home')}">
					<script rel="prefetch" src="/_ui/all/matterhorn/js/jquery.bxslider.min.js?ck=${currentKey}" async></script>
				</c:if>

				<c:if test="${fn:contains(bodyCSSClass, 'skin-layout-product') && !fn:contains(bodyCSSClass, 'list')}">
					<script rel="prefetch" src="https://cdnjs.cloudflare.com/ajax/libs/he/1.2.0/he.min.js" async></script>
				</c:if>

				<c:if test="${not empty digitaldatalayer}">
					<script rel="prefetch" type="text/javascript" src="/_ui/desktop/common/js/acc.datalayer-min.js?ck=${currentKey}"></script>
				</c:if>

				<c:if test="${fn:contains(wtPageId, 'checkout-registration') || fn:contains(wtPageId, 'registration') || fn:contains(bodyCSSClass, 'mod-layout--checkout')}">
					<script rel="preload" src="/_ui/all/matterhorn/js/libphonenumber-js.min.js?ck=${currentKey}"></script>
				</c:if>
			</c:if>

			<c:if test="${fn:contains(bodyCSSClass, 'mod-layout--checkout')}">
				<script rel="preload" src="/_ui/all/cache/dyn-jquery-ui-datepicker.min.js?ck=${currentKey}"></script>
				<script rel="preload" src="/_ui/all/cache/dyn-jquery-ui-datepicker-i18n.min.js?ck=${currentKey}"></script>
			</c:if>

			<script rel="preload" src="/_ui/all/cache/base-head.min.js?ck=${currentKey}"></script>
			<script rel="preload" src="/_ui/all/cache/base.min.js?ck=${currentKey}" async></script>
		</c:when>
		<c:otherwise>
			<script rel="preload" src="/_ui/all/cache/jquery-1.12.3.min.js"></script>
			<script type="text/javascript" src="/_ui/all/matterhorn/js/jquery.hoverIntent.min.js"></script>

			<c:if test="${not isOCI}">
				<script>
					// Lighthouse recommendation to not use passive listeners so scroll performance could be improved (should be loaded right after jQuery)
					// Passive event listeners
					jQuery.event.special.touchstart = {
						setup: function( _, ns, handle ) {
							this.addEventListener("touchstart", handle, { passive: !ns.includes("noPreventDefault") });
						}
					};
					jQuery.event.special.touchmove = {
						setup: function( _, ns, handle ) {
							this.addEventListener("touchmove", handle, { passive: !ns.includes("noPreventDefault") });
						}
					};

					// To avoid flicker of material-icons since we are loding them from Google
					$(window).load(function() {
						$('.material-icons-round').css('opacity', '1');
					});
				</script>
				<c:if test="${fn:contains(wtPageId, 'productDetails') || fn:contains(wtPageId, 'product-family-default')}">
					<script rel="prefetch" src="/_ui/all/matterhorn/js/magiczoom.min.js"></script>
				</c:if>
				<c:if test="${fn:contains(wtPageId, 'checkout-registration') || fn:contains(wtPageId, 'registration') || fn:contains(wtPageId, 'orderConfirmationPage')}">
					<script rel="prefetch" src="/_ui/all/matterhorn/js/password-strength.js" defer></script>
				</c:if>
                <c:if test="${fn:contains(wtPageId, 'checkout-registration') || fn:contains(wtPageId, 'registration') || fn:contains(bodyCSSClass, 'mod-layout--checkout')}">
                    <script rel="preload" src="/_ui/all/matterhorn/js/libphonenumber-js.min.js"></script>
                </c:if>

				<script rel="preload" src="/_ui/desktop/common/js/qs.js" defer></script>
				<script rel="preload" src="/_ui/desktop/common/js/axios.min.js" defer></script>
				<script rel="preload" src="/_ui/desktop/common/js/vue.min.js" defer></script>
				<script rel="preload" src="/_ui/desktop/common/js/loadash.js" defer></script>
				<script rel="preload" src="/_ui/all/matterhorn/js/polyfill.min.js" defer></script>
				<c:if test="${fn:contains(bodyCSSClass, 'skin-layout-home')}">
					<script rel="prefetch" src="/_ui/all/matterhorn/js/jquery.bxslider.min.js" async></script>
				</c:if>

				<c:if test="${fn:contains(bodyCSSClass, 'skin-layout-product') && !fn:contains(bodyCSSClass, 'list')}">
					<script rel="prefetch" src="https://cdnjs.cloudflare.com/ajax/libs/he/1.2.0/he.min.js" async></script>
				</c:if>

				<c:if test="${not empty digitaldatalayer}">
					<script rel="prefetch" type="text/javascript" src="/_ui/desktop/common/js/acc.datalayer.js?ck=${currentKey}"></script>
				</c:if>
			</c:if>

			<c:if test="${fn:contains(bodyCSSClass, 'mod-layout--checkout')}">
				<script rel="preload" src="/_ui/all/cache/dyn-jquery-ui-datepicker.min.js"></script>
				<script rel="preload" src="/_ui/all/cache/dyn-jquery-ui-datepicker-i18n.min.js"></script>
			</c:if>

			<script>
				if (!('IntersectionObserver' in window)) {
					var script = document.createElement("script");
					script.src = "/_ui/all/matterhorn/js/intersection-observer.js";
					document.getElementsByTagName('head')[0].appendChild(script);
				}
			</script>
			<script rel="preload" src="/_ui/all/cache/base-head.js?ck=${currentKey}" async></script>
			<c:choose>
				<c:when test="${isLocal eq true}">
					<script rel="preload" src="/_ui/all/cache/base.js?ck=${currentKey}" async></script>
				</c:when>
				<c:otherwise>
					<script rel="preload" src="/_ui/all/cache/base.min.js?ck=${currentKey}" async></script>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>

	<c:if test="${isReevooActivatedForWebshop and not empty trkrefid}">
		<script rel="prefetch" defer="defer" src="https://widgets.reevoo.com/loader/${trkrefid}.js" type="text/javascript" async></script>
	</c:if>
	<script src="/_ui/desktop/common/js/dataLayerPopulator.js"></script>
	<smartedit:smarteditStyles />
	<smartedit:smarteditScripts />
	<analytics:bing />
</body>

<!--
	<c:out value="${currentEnv}" />,
	<c:out value="${currentVersion}" />,
	<c:out value="${currentKey}" />
-->

</html>
