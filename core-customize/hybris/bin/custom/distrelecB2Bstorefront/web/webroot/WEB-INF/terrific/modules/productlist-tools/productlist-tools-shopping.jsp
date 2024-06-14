<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<ul class="tools-bar">
    <mod:toolsitem template="toolsitem-download" skin="download" tag="li" downloadUrl="/shopping/download" exportId="${exportId}" htmlClasses="${productsInListCount == 0 ? 'hidden' : ''}"/>
	<mod:toolsitem template="toolsitem-print" skin="print" tag="li" htmlClasses="tooltip-hover ${productsInListCount == 0 ? ' hidden' : ''}" />
</ul>
