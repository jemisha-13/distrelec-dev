<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-import-tool skin-layout-bom-tool-review skin-layout-wide skin-layout-nonavigation" >

	<div class="md-system">

		<div class="container">
			<input class="isNewSalesStatusEnabled hidden" value="true"/>

			<div id="breadcrumb" class="breadcrumb">
				<mod:breadcrumb/>
			</div>
			<mod:global-messages />
			<mod:bom-tool-review skin=""/>
			<mod:product template="bom-controllbar" skin="bom-controllbar"/>
			<mod:bom-no-available unavailableProducts="${unavailableProducts}" />
			<mod:bom-mpn-duplicate duplicateMpnProducts="${duplicateMpnProductList}" />
			<mod:productlist template="bom" skin="bom" matchingProducts="${matchingProducts}" />
			<mod:bom-toolbar />
		</div>

	</div>

</views:page-default>	