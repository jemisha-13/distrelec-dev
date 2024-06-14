<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:message code="product.eol.alternative" var="alternativeTitle" />
<spring:message code="text.view.more.alternatives" var="viewMorealternativeText" />

<div id="alternativesVm" class="alternatives-holder product-alternatives-vm carousel-alt newClass" data-product-code="${product.codeErpRelevant}" v-cloak
     data-action-url="${product.url}"
     data-ajax-url-postfix="${detailPageShowMorePostfix}"
>
    <h3 class="base alternatives-holder__heading" title="${alternativeTitle}">${alternativeTitle}</h3>

    <div class="product-alternatives-vm__list">

        <product-alternatives v-for="(item, index) in alternativesData"
                              v-bind:index="index"
                              v-bind:itemcode="item.code"
                              v-bind:itempimalternatecategory="item.pimAlternateCategory"
                              v-bind:itemurl="item.url"
                              v-bind:itemproductimagealttext="item.productImageAltText"
                              v-bind:itempromotiontext="item.promotiontext"
                              v-bind:itemproductimageurl="item.productImageUrl"
                              v-bind:itemname="item.name"
                              v-bind:itempricecurrency="item.price.currency"
                              v-bind:itempriceformattedvalue="item.price.formattedValue"></product-alternatives>
    </div>


    <button class="mat-button mat-button__normal--action-blue alternatives-holder__all-alternatives">
        <span class="">${viewMorealternativeText} <i class="fas fa-angle-right"></i></span>
    </button>

</div>

<script type="text/x-template" id="product-alternatives-template">

    <div class="product-alternatives-vm__list-item">

        <a class="card-item-anchor" :title="itemproductimagealttext" :href="itemurl" data-aainteraction="product click" data-section-title="Top Alternatives" data-link-type="component" data-location="pdp" :data-position="index + 1" :data-product-id="itemcode">
            <div class="card-item">

                <span class="itempimalternatecategory" v-if="itempimalternatecategory === 'DIS_Alternative_Similar'">
                    <spring:message code='itempimalternatecategory.similar'/>
                </span>
                <span class="itempimalternatecategory" v-else-if="itempimalternatecategory === 'DIS_Alternative_BetterValue'">
                    <spring:message code='itempimalternatecategory.bettervalue'/>
                </span>
                <span class="itempimalternatecategory" v-else-if="itempimalternatecategory === 'DIS_Alternative_Upgrade'">
                    <spring:message code='itempimalternatecategory.upgrade'/>
                </span>
                <span class="itempimalternatecategory" v-else-if="itempimalternatecategory === 'DIS_Alternative_DE'">
                    <spring:message code='itempimalternatecategory.directequivalent'/>
                </span>
                <span class="itempimalternatecategory" v-else>
                   <spring:message code='itempimalternatecategory.bettervalue'/>
                </span>

                <div class="card-item__image">
                    <img :title="itemproductimagealttext" :alt="itemproductimagealttext" :src="itemproductimageurl" width="46" height="26" class="img-responsive" >
                </div>
                <div class="card-item__content">
                    <h3 v-html="itemname"></h3>
                    <div id="wrapper3" :class="['stock-' + itemcode]">
                        <span class="hidden stock-id">{{itemcode}}</span>
                        <p class="stockvalue"><spring:message code='product.stock' text='Stock' /> <span id="rightColumn2" class="holder-s"></span></p>
                    </div>
                    <p class="price">
                        {{ itempricecurrency }}
                        <span v-html="itempriceformattedvalue"></span>
                    </p>
                </div>
            </div>
        </a>
    </div>
</script>
