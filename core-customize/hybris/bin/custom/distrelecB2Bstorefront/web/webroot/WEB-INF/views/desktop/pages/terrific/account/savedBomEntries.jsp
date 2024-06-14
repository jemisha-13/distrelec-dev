<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<c:set var="sContactLink" value="/cms/contact" />


<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-wide skin-layout-bom-entries">

	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb template="product" skin="product" />
	</div>

	<mod:bom-saved-entries skin=""/>

</views:page-default-md-full>