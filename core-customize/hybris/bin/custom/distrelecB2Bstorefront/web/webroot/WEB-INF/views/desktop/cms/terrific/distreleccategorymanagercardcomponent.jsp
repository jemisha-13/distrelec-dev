<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="false"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- DistrelecCategoryManagerCard -->

<c:if test="${not empty categoryManagerCard}">
	<mod:category-manager-card name="${categoryManagerCard.name}" jobTitle="${categoryManagerCard.jobTitle}" 
		organisation="${categoryManagerCard.organisation}" quote="${categoryManagerCard.quote}" tipp="${categoryManagerCard.tipp}" image="${categoryManagerCard.image}" 
		ctaText="${categoryManagerCard.ctaText}" rightFloat="${categoryManagerCard.rightFloat}" ctaLink="${categoryManagerCard.ctaLink}" />
</c:if>