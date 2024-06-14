<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>


<c:forEach var="individualSearch" items="${searchPageData.singleWordSearchItems}">

    <spring:theme code="zeroResults.result.title" text="We found 0 results for" arguments="${individualSearch.count},${individualSearch.singleTerm}" argumentSeparator="," var="sZeroResultsTitle" />
    <spring:theme code="zeroResults.result.button" text="Show all results" var="sZeroResultsButtonTitle" />
    <c:set var="sZeroResultsUrl" value="/search?q=${individualSearch.singleTerm}"/>

    <section class="mod-zeroresults__container">
        <div class="mod-zeroresults__container__term">
            <h3 class="title">${sZeroResultsTitle}</h3>
            <a href="${sZeroResultsUrl}">${sZeroResultsButtonTitle}</a>
        </div>
        <div class="mod-zeroresults__container__products">
            <c:forEach var="searchTermItems" items="${individualSearch.items}" varStatus="status">
               <c:if test="${status.count <= 4}">
                   <div class="item">
                       <a href="${searchTermItems.url}">
                           <div class="item__image">
                               <c:set var="productImage" value="${searchTermItems.productImages[0]}"/>
                               <c:set var="landscapeSmallJpg" value="${not empty productImage.landscape_small.url ? productImage.landscape_small.url : '/_ui/all/media/img/missing_landscape_small.png' }"/>
                               <c:set var="landscapeSmallWebP" value="${not empty productImage.landscape_small_webp.url ? productImage.landscape_small_webp.url : landscapeSmallJpg}"/>
                               <picture>
                                   <source srcset="${landscapeSmallWebP}">
                                   <img alt="${searchTermItems.name}" src="${landscapeSmallJpg}"/>
                               </picture>
                           </div>
                           <h4 class="item__name">${searchTermItems.name}</h4>
                           <span class="item__price">${searchTermItems.price.formattedValue}</span>
                       </a>
                   </div>
               </c:if>
            </c:forEach>
        </div>
        <button class="mod-zeroresults__container__button" onclick="window.location.href='${sZeroResultsUrl}';"> ${sZeroResultsButtonTitle} <i class="fa fa-angle-right"></i> </button>
    </section>

</c:forEach>

<section class="mod-zeroresults__container">
    <cms:slot var="feature" contentSlot="${slots['BannerContent']}">
        <cms:component component="${feature}"/>
    </cms:slot>
</section>
