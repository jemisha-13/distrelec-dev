<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>


<c:choose>
	<c:when test="${cssCompression}"><g:compress>
		<%@ include file="compressables/css.tag" %>
	</g:compress></c:when>
	<c:otherwise>
		<%@ include file="compressables/css.tag" %>
	</c:otherwise>
</c:choose>

<%-- <link rel="stylesheet" href="<c:url value="/_ui/desktop/blueprint/print.css"/>" type="text/css" media="print" /> --%>
<style type="text/css" media="print">
	<c:url value="/_ui/desktop/blueprint/print.css" var="blueprintPrintCssLink" />
	@IMPORT url("${blueprintPrintCssLink}");
</style>

<!--[if IE 7]> <link type="text/css" rel="stylesheet" href="<c:url value="/_ui/desktop/common/css/lte_ie7.css"/>" media="screen, projection" /> <![endif]-->

<%-- our site css --%>
<!--[if IE 8]> <link type="text/css" rel="stylesheet" href="<c:url value="/_ui/desktop/common/css/ie_8.css"/>" media="screen, projection" /> <![endif]-->
<!--[if IE 7]> <link type="text/css" rel="stylesheet" href="<c:url value="/_ui/desktop/common/css/ie_7.css"/>" media="screen, projection" /> <![endif]-->

<%-- theme specific css --%>
<!--[if IE 8]> <link type="text/css" rel="stylesheet" href="<c:url value="${cssIE8Path}"/>" media="screen, projection" /> <![endif]-->
<!--[if IE 7]> <link type="text/css" rel="stylesheet" href="<c:url value="${cssIE7Path}"/>" media="screen, projection" /> <![endif]-->
