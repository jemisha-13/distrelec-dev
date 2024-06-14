<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-newsletter skin-layout-content-with-navigation skin-layout-wide">

	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb/>
	</div>

	<mod:global-messages/>
	<mod:nav-content />
	<cms:slot var="feature" contentSlot="${slots.TeaserContent}">
		<cms:component component="${feature}"/>
	</cms:slot>

	<cms:slot var="feature" contentSlot="${slots.TitleContent}">
		<cms:component component="${feature}"/>
	</cms:slot>

	<cms:slot var="feature" contentSlot="${slots.TextImage}">
		<cms:component component="${feature}"/>
	</cms:slot>

	<mod:account-login-data template="success-newsletter"/>


</views:page-default>
