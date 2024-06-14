<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- set isOCI var --%>
<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<c:set var="isOCI" value="true" />
</sec:authorize>

<spring:message code="checkoutregister.goBack" var="sGoBack" />
<spring:message code="checkoutregister.continue" var="sContinue" />

<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-wide mod-layout--standalone">
	<section class="standalone-register-holder">
		<div class="container p-relative">
			<div class="row">
				<section class="standalone-register-holder__title col-12">
					<h1><spring:message code="checkoutregister.title" /></h1>
					<p><spring:message code="standalone.reg.step1.subTitle" /></p>
				</section>

				<div class="col-12 col-lg-9 order-1">
					<div class="row">
						<mod:global-messages htmlClasses="col p-none" />
						<mod:standalone-register-select />
					</div>
				</div>
				<div class="standalone-register-holder__steps standalone-register-holder__steps--b2b col-12 col-lg-3 order-2">
					<ul class="standalone-register-holder__steps__items">
						<li class="standalone-register-holder__steps__items__item standalone-register-holder__steps__items__item--1">
							<span class="number number--active">1</span>
							<span class="text"><spring:message code="standalone.reg.step1.title" /></span>
						</li>
						<li class="standalone-register-holder__steps__items__item standalone-register-holder__steps__items__item--2">
							<span class="number">2</span>
							<span class="text"><spring:message code="standalone.reg.step2.title" /></span>
						</li>
						<li class="standalone-register-holder__steps__items__item standalone-register-holder__steps__items__item--3">
							<span class="number">3</span>
							<span class="text"><spring:message code="standalone.reg.step2.b2c.title" /></span>
						</li>
					</ul>
				</div>
				<div class="standalone-register-holder__steps standalone-register-holder__steps--b2c hidden col-12 col-lg-3 order-2">
					<ul class="standalone-register-holder__steps__items">
						<li class="standalone-register-holder__steps__items__item standalone-register-holder__steps__items__item--1">
							<span class="number number--active">1</span>
							<span class="text"><spring:message code="standalone.reg.step1.title" /></span>
						</li>
						<li class="standalone-register-holder__steps__items__item standalone-register-holder__steps__items__item--2">
							<span class="number">2</span>
							<span class="text"><spring:message code="standalone.reg.step2.b2c.title" /></span>
						</li>
					</ul>
				</div>
				<div class="col-12 col-lg-9 order-3">
					<div class="row">
						<c:if test="${isOCI == false}">
							<mod:standalone-register skin="b2c" htmlClasses="hidden" />
							<mod:standalone-register template="b2b" skin="b2b" htmlClasses="show" />
						</c:if>
					</div>
					<div class="col-xs-12">
					<c:choose>
  						<c:when test="${!empty param.checkout}">
  							<a class="return-btn" href="/cart/checkout"><i class="fa fa-angle-left" aria-hidden="true"></i>${sGoBack}</a>
  						</c:when>
  						<c:otherwise>
  							<a class="return-btn" href="/"><i class="fa fa-angle-left" aria-hidden="true"></i>${sGoBack}</a>
  						</c:otherwise>
  					</c:choose>
					</div>
				</div>
			</div>
		</div>
	</section>
</views:page-default-md-full>
