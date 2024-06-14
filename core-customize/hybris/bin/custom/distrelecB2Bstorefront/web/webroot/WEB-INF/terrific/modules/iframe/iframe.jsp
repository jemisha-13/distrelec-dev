<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<iframe scrolling="yes" src="${link}"<c:if test="${not empty width}"> width="${width}"</c:if><c:if test="${not empty height}"> height="${height}"</c:if>></iframe>
<c:if test="${not empty title}">
	<p>${title}</p>
</c:if>