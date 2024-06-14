<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<c:set var="sContactLink" value="/cms/returnorrepair" />
<spring:message code="rma.returns.contact.text" text="Need to talk us about your return?" var="sReturnsContactText" />
<spring:message code="rma.returns.contact.link" text="Contact us" var="sReturnsContactLink" />

<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-default-full skin-layout-account-detail skin-layout-wide">
	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb template="new-account" skin="new-account" />
	</div>
	<div class="return-items-my-account-holder">
		<div class="container">
			<div class="row">
				<div class="col-12">
					<mod:global-messages />
				</div>
				<div class="col-12 col-lg-9 bs-o1">
					<h1 class="base page-title returns-item-title">${cmsPage.title}</h1>
					<p class="returns-contact">${sReturnsContactText}<a data-aainteraction="contact us" data-aalocation="return items" data-aaorder-id="${orderData.code}" class="returns-contact-link" href="${sContactLink}">${sReturnsContactLink}</a></p>
					<mod:cart-list template="return-items" skin="return-items" />
				</div>
				<div class="col-12 col-lg-3 bs-o2 d-print-none">
					<mod:nav-content template="myaccount" skin="myaccount" expandedNav="OrderManager" activeLink="OrderHistory" />
				</div>
			</div>
		</div>
	</div>
	<mod:print-footer/>
</views:page-default-md-full>
