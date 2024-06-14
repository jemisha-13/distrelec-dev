<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<a class="ico ico-share popover-special hidden" title="<spring:message code="toolsitem.share.emailOnly" />" data-content-id="productlist_tools_share" data-original-title="<spring:message code="toolsitem.share.emailOnly" />">
	<spring:message code="toolsitem.share" />
</a>

<div id="productlist_tools_share">
	<a class="share_email" data-aainteraction="share">
		<i class="far fa-envelope" title="<spring:message code="toolsitem.share.emailOnly" />"></i>
	</a>
</div>