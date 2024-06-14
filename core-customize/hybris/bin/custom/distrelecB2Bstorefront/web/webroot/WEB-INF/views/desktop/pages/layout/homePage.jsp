<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>

<template:page pageTitle="${pageTitle}">
	<div id="globalMessages">
		<common:globalMessages/>
	</div>
	<cms:slot var="feature" contentSlot="${slots.Content}">
		<div class="span-24 section1 cms_banner_slot">
			<cms:component component="${feature}"/>
		</div>
	</cms:slot>
	
</template:page>