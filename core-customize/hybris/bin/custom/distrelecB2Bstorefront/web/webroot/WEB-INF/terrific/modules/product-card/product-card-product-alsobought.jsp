
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="maxVisibleItems" value="4"/>


<spring:eval expression="@configurationService.configuration.getString('factfinder.json.reco.url')" var="ffrecoUrl" scope="application" />
<spring:message code="plp.helpprompts.facetlistactionbar.detailview.label1" var="viewMoreProductsText"/>

<div class="recommendations-holder"
     data-ajax-url="${productFFCarouselDataPath}"
     data-ff-search-url="${ffrecoUrl}"
     data-ff-search-channel="${ffsearchChannel}"
     data-ff-producterp="${product.codeErpRelevant}"
>
    <h3 class="base" title="${title}">${title}</h3>

    <ul id="appfirst" class="row">
        <li v-if="index < itemsToShow" v-for="(item, index) in items" class="col-12 col-sm-6 col-lg-3">
            <a class="card-item-anchor" v-on:click="setStorage" :title="decode(item.productImageAltText)" :href="item.url" data-aainteraction="product click" data-section-title="Customers also bought" data-link-type="component" data-location="pdp" :data-position="index + 1" :data-product-id="item.code">
                <div class="card-item">
                    <div class="card-item__image">
                        <div class="promo-holder">
                            {{ item.promotiontext }}
                        </div>
                        <img :title="decode(item.productImageAltText)" :alt="decode(item.productImageAltText)" width="46" height="26" class="img-responsive" :src="item.productImageUrl">
                    </div>
                    <div class="card-item__content">
                        <h3 v-html="item.name"></h3>
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
