<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:theme code="formfeedback.title" var="sFeedbackTitle" />

<views:page-default-seo pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-feedback skin-layout-wide">

	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb/>
	</div>

	<mod:global-messages/>

	<div class="md-system">

		<div class="container">
			<div class="row">
				<div class="col-xs-12 col-lg-4 order-2">
					<mod:nav-content />
					<cms:slot var="feature" contentSlot="${slots.TeaserContent}">
						<cms:component component="${feature}"/>
					</cms:slot>
				</div>

				<div class="col-xs-12 col-lg-8">
					<h1 class="base">${sFeedbackTitle}</h1>

					<cms:slot var="feature" contentSlot="${slots.TopContent}">
						<cms:component component="${feature}"/>
					</cms:slot>

					<mod:form-feedback/>

					<cms:slot var="feature" contentSlot="${slots.BottomContent}">
						<cms:component component="${feature}"/>
					</cms:slot>
				</div>
			</div>
		</div>

	</div>

</views:page-default-seo>
