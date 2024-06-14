<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-error skin-layout-error-not-authorise skin-layout-wide" >

	<section class="breadcrumb-section">
		<div id="breadcrumb" class="breadcrumb">
			<mod:breadcrumb template="product" skin="product" />
		</div>
	</section>

	<mod:error template="not-authorise" />

</views:page-default-md-full>