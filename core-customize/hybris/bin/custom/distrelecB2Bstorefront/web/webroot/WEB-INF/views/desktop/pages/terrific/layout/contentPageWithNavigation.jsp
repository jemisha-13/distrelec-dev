<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-content-with-navigation skin-layout-wide">
	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb />
	</div>
	<mod:global-messages />
	<div class="ct">
		<div class="row">
			<div class="gu-4 gu-padding-left cf nav-align-right">
				<mod:nav-content />
				<cms:slot var="feature" contentSlot="${slots['TeaserContent']}">
					<cms:component component="${feature}" />
				</cms:slot>
			</div>
			<div class="gu-8 content-with-nav">
				<cms:slot var="feature" contentSlot="${slots.Content}">
					<cms:component component="${feature}" />
				</cms:slot>
			</div>
		</div>
	</div>

	<c:if test="${not empty email}">
		<script id="unsubBar" type="text/javascript">
			var wally = ${email};
		</script>
	</c:if>
</views:page-default>