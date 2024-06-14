<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>



<div class="categoryseo">
    <c:if test="${ not empty categoryPageData.sourceCategory.seoMetaDescription}">
        <div class="categoryseo__description lessText">
            ${categoryPageData.sourceCategory.introText}
        </div>
    </c:if>
    <c:if test="${ not empty categoryPageData.sourceCategory.seoSections}">
        <div class="categoryseo__content hidden">
            <c:forEach var="ceoSection" items="${categoryPageData.sourceCategory.seoSections}">
                <div class="categoryseo__content-title">${ceoSection.header}</div>
                <div class="categoryseo__content-description" >${ceoSection.text}</div>
            </c:forEach>
        </div>

        <i class="fas fa-chevron-down categoryseo__show-more"></i>
        <i class="fas fa-chevron-up categoryseo__show-more hidden"></i>
    </c:if>

</div>
