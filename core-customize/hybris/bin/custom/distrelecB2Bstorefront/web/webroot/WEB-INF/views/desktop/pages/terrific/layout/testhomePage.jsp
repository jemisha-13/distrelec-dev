	<%@ page trimDirectiveWhitespaces="true" %>
	<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
	<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>

	<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
	<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

	<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod-layout skin-layout-wide" >
		<h1 class="vh">Distrelec</h1>

		<mod:global-messages/>

		<mod:testhomepage/>

		<div class="ct">
			<cms:slot var="feature" contentSlot="${slots['TitleContent']}">
				<cms:component component="${feature}"/>
			</cms:slot>
			<cms:slot var="feature" contentSlot="${slots['HeroContent']}">
				<cms:component component="${feature}"/>
			</cms:slot>
			<cms:slot var="feature" contentSlot="${slots['Content']}">
				<cms:component component="${feature}"/>
			</cms:slot>
		</div>
	</views:page-default>
