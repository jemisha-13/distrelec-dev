<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<p id="productlist_tools_download">
    <span><spring:message code="toolsitem.download" />:</span>
    <a data-aainteraction="download cart" href="${downloadUrl}/csv/${exportId}">CSV</a>
    <b>/</b>
    <a data-aainteraction="download cart" href="${downloadUrl}/xls/${exportId}">XLS</a>
</p>