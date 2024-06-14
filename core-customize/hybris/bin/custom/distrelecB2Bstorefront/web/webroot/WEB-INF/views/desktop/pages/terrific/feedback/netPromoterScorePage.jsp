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

<views:page-default-seo-nps pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-feedback skin-layout-wide skin-layout-feedback-nps">

	<mod:global-messages/>

	<mod:checkout-success skin="nps" template="nps" />

</views:page-default-seo-nps>
