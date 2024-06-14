<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="nam" uri="/WEB-INF/tld/namicscommercetags.tld" %>

<div class="md-system">

	<%-- Boolean ${footerComponentData.checkoutFooter} is only true on the page template checkout --%>
	<c:if test="${not footerComponentData.checkoutFooter}">
		<div class="footer__main-content footer__main-content-OCI">
			<div class="container">
					<%-- footer links --%>
				<div class="row footer__nav-row">
					<mod:footer-links tag="nav"  htmlClasses="footer__links row" navigationNodes="${footerComponentData.navigationNodes}" wrapAfter="${footerComponentData.wrapAfter}" />
				</div>

					<div class="container">
						<div class="row">
							<%-- social media --%>
							<mod:social-media htmlClasses="col-md" />
							<%-- payment methods --%>
							<c:if test="${not empty footerComponentData.paymentMethods}">
								<mod:footer-payment-methods htmlClasses="col-md payment-methods" paymentMethods="${footerComponentData.paymentMethods}" />
							</c:if>
						</div>
					</div>

				<c:if test="${not customFooterEnabled}">
					<div class="row">
						<div class="col-12">
							<mod:impressum htmlClasses="footer__impressum" impressumLinkList="${footerComponentData.impressumLinks}" />
						</div>
					</div>
				</c:if>

				<div class="row">
					<div class="col-12 cross-website-links">
						<c:forEach items="${footerComponentData.countryLinks}" var="countryLinks" >
							<c:url value="${ycommerce:cmsLinkComponentUrl(countryLinks, request)}" var="url"/>
							<a href="${countryLinks.url}" title="${countryLinks.linkName}"
							class="cross-website-links__link"
							data-aainteraction="footer navigation"
							data-location="footer nav"
							data-parent-link="legal links"
							data-link-value="${fn:toLowerCase(countryLinks.getLinkName(enLocale))}">
								<span>${countryLinks.linkName}</span>
							</a>
						</c:forEach>
					</div>
				</div>

<%--				<c:if test="${not footerComponentData.checkoutFooter and isInter eq false}">--%>
<%--					<div class="footer__background">--%>
<%--						<div class="ct">--%>
<%--							<mod:give-us-feedback htmlClasses="footer__feedback" />--%>
<%--						</div>--%>
<%--					</div>--%>
<%--				</c:if>--%>

			</div>
		</div>
	</c:if>

</div>
