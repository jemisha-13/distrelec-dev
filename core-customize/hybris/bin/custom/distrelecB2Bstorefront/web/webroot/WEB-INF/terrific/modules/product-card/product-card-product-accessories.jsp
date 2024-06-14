<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:message code="product.tabs.accessories" var="sAccessoriesTitle" />
<spring:message code="plp.helpprompts.facetlistactionbar.detailview.label1" var="viewMoreProductsText"/>

<c:set var="maxVisibleItems" value="4"/>

<div class="accessories-holder"
     data-action-url="${product.url}"
     data-ajax-url-postfix="${detailPageShowMorePostfix}"
>
    <h3 class="base" title="${sAccessoriesTitle}">${sAccessoriesTitle}</h3>

    <ul id="accessories" class="row">
            <li v-if="index < itemsToShow" v-for="(item, index) in items" class="col-12 col-sm-6 col-lg-3">
            <a :href="item.url" class="card-item-anchor" v-on:click="setStorage" :title="item.name" data-aainteraction="product click" data-section-title="Accessories" data-link-type="component" data-location="pdp" :data-position="index + 1" :data-product-id="item.code">
                <div class="card-item">
                    <div class="card-item__image">
                        <img :title="item.productImageAltText" :alt="item.productImageAltText" width="46" height="26" class="img-responsive" :src="item.productImageUrl">
                    </div>
                    <div class="card-item__content">
                        <h3>{{ item.name }}</h3>
                        <div id="wrapper3" :class="['stock-' + item.code]">
                            <span class="hidden stock-id">{{item.code}}</span>
                            <p><spring:message code='product.stock' text='Stock' /> <span id="rightColumn2" class="holder-s"></span></p>
                        </div>
                        <p class="price">
                            {{ item.price.currency }}
                            <span v-html="item.price.formattedValue"></span>
                        </p>
                    </div>
                </div>
            </a>
        </li>
        <button v-if="items.length > itemsToShow" class="mat-button mat-button--action-green" @click="itemsToShow += 8">${viewMoreProductsText}</button>
    </ul>

</div>
