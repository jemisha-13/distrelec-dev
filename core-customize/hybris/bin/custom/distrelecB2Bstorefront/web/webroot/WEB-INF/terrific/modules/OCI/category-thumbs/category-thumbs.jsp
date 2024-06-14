<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<ul class="categories">
    <c:forEach items="${categories}" var="category">

        <c:set var="categoryCode" value="${category.code}" />

        <c:if test="${!ycommerce:isCategoryPunchedout(categoryCode, request)}">
            <li class="col-sm-6 col-md-3 col-lg-2 categories__category">
        
                <c:set var="categoryName" value="${fn:replace(category.name, ' ', '-')}" />
    
                <a title="<c:out value="${category.name}" />" class="thumb-link categories__link" href="${category.url}" name="${wtTeaserTrackingId}.${fn:toLowerCase(categoryName)}.-">
					<div class="categories__image">
						<c:set var="productImage" value="${category.images}"/>
						<c:set var="portraitSmallJpg" value="${not empty productImage.portrait_small.url ? productImage.portrait_small.url : '/_ui/all/media/img/missing_portrait_small.png' }"/>
						<c:set var="portraitSmallWebP" value="${not empty productImage.portrait_small_webp.url ? productImage.portrait_small_webp.url : portraitSmallJpg}"/>
						<picture>
							<source srcset="${portraitSmallWebP}">
							<img alt="<c:out value="${category.name}" />" src="${portraitSmallJpg}"/>
						</picture>
					</div>
					<div class="categories__name" >
						<c:set var="categoryNameDisplay" value="${category.name}" />
						<c:if test="${fn:length(categoryName) > 70}">
							<c:set var="categoryNameDisplay" value="${fn:substring(categoryNameDisplay, 0, 65)}..." />
						</c:if>

						<span class="categories__title" title="<c:out value="${category.name}" />">${categoryNameDisplay}</span>
						<span class="categories__arrow"><i class="angle-right"></i></span>
					</div>
                </a>
            </li>
        </c:if>
		<c:set var="categoryLevel" value="${category.level eq 2}" />

    </c:forEach>
</ul>

<spring:message code="category.message.showlistview" var="showlistview" />
<spring:message code="category.message.hidelistview" var="hidelistview" />

<c:if test="${categoryLevel}">

	<div id="sub-category" class="accordion sub-category" data-isoci="true">
		<div class="accordion__trigger">
			<a href="#" title="show more" class="closed mat-button mat-button__solid--action-green col-12 col-md-4 col-lg-2" data-show-message="${showlistview}" data-hide-message="${hidelistview}"
			   data-aainteraction="show hide list" data-link-text="show list view" data-location="category page">
				<span class="accordion__trigger-label">${showlistview}</span>
				<i class="fa fa-angle-down"></i>
			</a>

		</div>
		<ul class="row accordion__content sub-category__content sub-category__content-tile-vm" v-cloak>
			<!-- Add a new Tag file here to display sub-category Grid-view -->

			<mod:loading-state skin="loading-state" />

			<li id="#" class="col-sm-6 col-md-6 col-lg-3 category-js-template hidden">

				<div class="sub-category__content-tile">

					<a href="#" class="sub-category__content-tile__title" title="category.name">
						<img src="category.image.portrait_small.url" class="title-image">
						<span  class="title-name"></span>
					</a>

					<div class="sub-category__content-tile__links">
						<a href="#" title="" class="subcategory-js-template">  <span  class="title-name "> </span> (<span  class="title-count ">0</span>)</a>
					</div>

				</div>

			</li>

		</ul>

	</div>

</c:if>




