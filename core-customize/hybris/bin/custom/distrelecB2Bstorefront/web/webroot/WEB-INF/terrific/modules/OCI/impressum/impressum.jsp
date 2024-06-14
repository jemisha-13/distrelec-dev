<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<jsp:useBean id="dateOCI" class="java.util.Date"/>
<c:set var="currentYear">
    <fmt:formatDate value="${dateOCI}" pattern="yyyy"/>
</c:set>
<c:set var="currentdate">
    <fmt:formatDate value="${dateOCI}" pattern="yyyy/MM/dd"/>
</c:set>
<c:set var="currentdatetimestamp">
    <fmt:formatDate value="${dateOCI}" pattern="HH:mm"/>
</c:set>
<spring:theme code="footer.copyright.label" text="© ${currentYear} Distrelec" var="sCopyrightLabel"
              arguments="${currentYear}"/>
<spring:theme code="footer.copyright.url" text="/copyright" var="sCopyrightUrl"/>
<spring:theme code="footer.current.date" text="Todays Date" var="sTodaysDateLabel"
              arguments="${currentdate},${currentdatetimestamp}"/>
<spring:eval expression="new java.util.Locale('en')" var="enLocale" />

<div class="col-12 footer__impressum-links">
	<ul class="col-12">
		<c:forEach items="${impressumLinkList}" var="impressumLink" >
			<c:url value="${ycommerce:cmsLinkComponentUrl(impressumLink, request)}" var="url"/>
			<li class="footer__impressum-link">
				<a href="${url}" title="${impressumLink.linkName}"
				   data-aainteraction="footer navigation"
				   data-location="footer nav"
				   data-parent-link="legal links"
				   data-link-value="${fn:toLowerCase(impressumLink.getLinkName(enLocale))}">
					<span>${impressumLink.linkName}</span>
				</a>
			</li>
		</c:forEach>
	</ul>
</div>

<div class="col-12 footer__impressum-copyright">
	<span class="text">${sCopyrightLabel}</span>
	<br><span class="text">${sTodaysDateLabel}</span>
</div>
