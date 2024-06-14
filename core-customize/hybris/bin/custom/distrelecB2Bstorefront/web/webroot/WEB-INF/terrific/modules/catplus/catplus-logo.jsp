<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<%-- <spring:message code="product.catplus.image.missing" text="Image not found" var="sImageMissing"/> --%>

<img class="catplus-logo" alt="${catplusLogoAltText == null ? sImageMissing : catplusLogoAltText}" src="${catplusLogoUrl}" />
