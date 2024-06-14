<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<spring:message code="text.remove" var="sRemoveText" />

<a class="ico ico-list-rm" title="${sRemoveText}" data-product-code="${productId}" data-aainteraction="remove from list">
    <span>
        ${sRemoveText}
    </span>
</a>
