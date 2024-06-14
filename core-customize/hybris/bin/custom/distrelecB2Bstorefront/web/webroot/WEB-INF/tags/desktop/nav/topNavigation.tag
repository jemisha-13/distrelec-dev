<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>

<div id="nav_main" class="nav_main">
	
		<cms:slot var="component" contentSlot="${slots['MainNav']}">
			<cms:component component="${component}"/>
		</cms:slot>
	
</div>