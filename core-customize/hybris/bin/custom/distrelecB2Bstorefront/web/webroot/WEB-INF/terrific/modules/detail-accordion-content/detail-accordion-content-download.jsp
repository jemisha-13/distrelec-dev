<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<h4>${downloadSection.title}</h4>
<p>
	${downloadSection.description}
</p>
<div class="row link-download-pdf">
	<c:forEach items="${downloadSection.downloads}" var="download">
		<a href="<c:url value="${download.downloadUrl}" />" name="${download.name}" target="_blank">
			<i></i>
			${download.name}<br />
			<span>
				${download.mimeType}
				<c:if test="${not empty download.languages}">
					,&nbsp;&nbsp;
					<c:forEach items="${download.languages}" var="language">${language.name}&nbsp;</c:forEach>
				</c:if>
			</span>
		</a>
	</c:forEach>
</div>
<c:if test="${not empty downloadSection.alternativeDownloads}">
	<div class="row show-more show-more-downloads">
		<a href="/" class="show-more-link popover-toggle"><span><spring:message code="product.accordion.download.more.lang" /></span></a>
		<div class="popover-languages hidden">
			<ul>
				<c:forEach items="${downloadSection.alternativeDownloads}" var="download" varStatus="varStatus">
					<li><a href="<c:url value="${download.downloadUrl}" />" name="${download.name}" target="_blank">${download.name}&nbsp;&nbsp;(${fn:toUpperCase(download.languages[0].isocode)})</a></li>
				</c:forEach>
			</ul>
		</div>
	</div>
</c:if>
