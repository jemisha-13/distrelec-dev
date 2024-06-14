<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>

<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<cms:slot var="logo" contentSlot="${slots.Logo}">
	<cms:component component="${logo}"/><%-- simplebannercomponent --%>
</cms:slot>
