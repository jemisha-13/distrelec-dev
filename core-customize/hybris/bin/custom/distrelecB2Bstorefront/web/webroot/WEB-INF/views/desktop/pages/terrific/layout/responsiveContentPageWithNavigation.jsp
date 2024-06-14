<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>

<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-content-with-navigation skin-layout-wide cmscontentpage cmscontentpage__with-navigation">

	<div class="container page-wrapper">
		<div class="row">

			<div class="col-12">
				<div id="breadcrumb" class="breadcrumb">
					<mod:breadcrumb />
				</div>
				<mod:global-messages />
				<mod:nav-content />
			</div>

            <div class="left-content col-lg-9">
				<cms:slot var="feature" contentSlot="${slots.Content}">
					<cms:component component="${feature}" />
				</cms:slot>
			</div>

            <div class="right-content col-lg-3">
                <cms:slot var="feature" contentSlot="${slots['RightNavigation']}">
                    <cms:component component="${feature}"/>
                </cms:slot>
            </div>

		</div>

	</div>

</views:page-default-md-full>
