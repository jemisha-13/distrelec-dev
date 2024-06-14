<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:theme code="product.list.read.more" text="Read More" var="sShowMore"/>
<spring:theme code="product.list.read.less" text="Read Less" var="sShowLess"/>


<div class="categoryseo">
    <c:if test="${ not empty categoryPageData.sourceCategory.seoMetaDescription}">
        <div class="categoryseo__description">
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

        <button class="categoryseo__show-more" data-more-msg="${sShowMore}" data-less-msg="${sShowLess}">${sShowMore}</button>
    </c:if>

</div>
