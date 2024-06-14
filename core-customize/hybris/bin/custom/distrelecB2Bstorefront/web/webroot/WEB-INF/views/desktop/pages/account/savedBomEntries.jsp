<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<template:page pageTitle="${pageTitle}">
	<jsp:attribute name="pageScripts">
		<script type="text/javascript" src="${commonResourcePath}/js/accmob.account.js"></script>
	</jsp:attribute>
	<jsp:body>
		<div class="item_container_holder">
			<div id="globalMessages" data-theme="b">
				<common:globalMessages />
			</div>
			<cms:slot var="feature" contentSlot="${slots['Section1']}">
				<cms:component component="${feature}" />
			</cms:slot>
			<div data-role="content">

			</div>
			<div id="bottom-banner" class="homebanner">
				<cms:slot var="feature" contentSlot="${slots.Section2}">
					<div class="span-24 section1 cms_banner_slot">
						<cms:component component="${feature}" />
					</div>
				</cms:slot>
			</div>
		</div>
	</jsp:body>
</template:page>
