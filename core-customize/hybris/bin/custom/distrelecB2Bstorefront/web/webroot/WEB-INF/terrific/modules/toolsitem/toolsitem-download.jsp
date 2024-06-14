<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<a class="ico ico-download popover-special" title="<spring:message code="product.accordion.download" />" data-content-id="productlist_tools_download${orderIndex}" href="#"><i></i></a>
<div class="hidden" id="productlist_tools_download${orderIndex}">
    <ul class="download-bar">
        <li><a href="${downloadUrl}/xls/${exportId}"><spring:message code="toolsitem.save.as" /> XLS</a></li>
        <li><a href="${downloadUrl}/csv/${exportId}"><spring:message code="toolsitem.save.as" /> CSV</a></li>
    </ul>
</div>
