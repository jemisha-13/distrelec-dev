<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<ul class="row accordion__content sub-category__content sub-category__content-tile-vm" >
    <c:forEach items="${categories}" var="category">

        <c:set var="categoryCode" value="${category.code}" />

        <c:if test="${!ycommerce:isCategoryPunchedout(categoryCode, request)}">
            <li class="col-sm-6 col-md-6 col-lg-3">
				<div class="sub-category__content-tile" category-code="${categoryCode}" category-level="${category.level}">

                <c:set var="categoryName" value="${fn:replace(category.name, ' ', '-')}" />

					<a title="<c:out value="${category.name}" />" class="sub-category__content-tile__title" href="${category.url}" name="${wtTeaserTrackingId}.${fn:toLowerCase(categoryName)}.-">
						<c:set var="productImage" value="${category.images}"/>
						<c:set var="portraitSmallJpg" value="${not empty productImage.portrait_small.url ? productImage.portrait_small.url : '/_ui/all/media/img/missing_portrait_small.png' }"/>
						<c:set var="portraitSmallWebP" value="${not empty productImage.portrait_small_webp.url ? productImage.portrait_small_webp.url : portraitSmallJpg}"/>
						<picture>
							<source srcset="${portraitSmallWebP}">
							<img class="title-image " width="52" height="70" alt="<c:out value="${category.name}" />" src="${portraitSmallJpg}"/>
						</picture>
						<c:set var="categoryNameDisplay" value="${category.name}" />
						<c:if test="${fn:length(categoryName) > 70}">
							<c:set var="categoryNameDisplay" value="${fn:substring(categoryNameDisplay, 0, 65)}..." />
						</c:if>

						<span class="title-name " title="<c:out value="${category.name}" />">${categoryNameDisplay}</span>
					</a>
				</div>
            </li>
        </c:if>

    </c:forEach>
</ul>


	<div id="sub-category" class="accordion sub-category hidden" data-isoci="false">
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

			<li :id="category.code" class="col-sm-6 col-md-6 col-lg-3" v-for="(category, index) in categoriesData"
				v-show="category.count > 0">

				<div class="sub-category__content-tile">

					<a :href="category.url" class="sub-category__content-tile__title" :title="category.name">

						<template v-if="category.image !== null ">
							<img :src="category.image.portrait_small.url "  class="title-image ">
						</template>
						<template v-else>
							<img src="/_ui/all/media/img/missing_portrait_small.png"  class="title-image ">
						</template>

						<span  class="title-name ">{{category.name }}</span>
					</a>

					<div class="sub-category__content-tile__links">

						<template v-if="category.subcategoryDisplayDataList.length > 0 ">
							<a  v-for="(subCategory, index) in category.subcategoryDisplayDataList" :id="subCategory.code" :href="subCategory.url" :title="subCategory.name" >  <span  class="title-name "> {{subCategory.name}} </span> (<span  class="title-count ">{{subCategory.count}}</span>)</a>
						</template>
						<template v-else>
							<a  :href="category.url" :title="category.name" >  <span  class="title-name "> {{category.name}} </span> (<span  class="title-count ">{{category.count}}</span>)</a>
						</template>

					</div>

				</div>
			</li>

		</ul>

	</div>




