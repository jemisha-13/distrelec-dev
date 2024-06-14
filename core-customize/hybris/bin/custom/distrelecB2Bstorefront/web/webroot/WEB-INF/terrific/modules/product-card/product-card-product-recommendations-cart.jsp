<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:message code="product.card.related.title" var="sRelatedTitle"/>
<c:set var="maxVisibleItems" value="4"/>


<spring:eval expression="@configurationService.configuration.getString('factfinder.json.reco.url')" var="ffrecoUrl" scope="application" />

<div class="recommendations-holder"
     data-ajax-url="${productFFCarouselDataPath}"
     data-ff-search-url="${ffrecoUrl}"
     data-ff-search-channel="${ffsearchChannel}"
>
    <h3 title="${title}">${sRelatedTitle}</h3>

    <ul id="app" class="row">
        <li v-for="(item, index)  in items" class="recommendations-item">
            <div  :data-position="index + 1" :data-product-id="item.id">
                <div class="card-item">
                    <div class="card-item__image">
                        <a :href="item.record.ProductURL" :title="item.record.Title">
                            <img :title="item.record.Title" :alt="item.record.Title" width="61" height="46" class="img-responsive" :src="item.record.ImageURL">
                        </a>
                    </div>
                    <div class="card-item__content">
                        <a :href="item.record.ProductURL" :title="item.record.Title">
                            <h3 v-html="item.record.Title" class="card-item__title"></h3>
                            <div id="wrapper3" :class="['stock-' + item.id]">
                                <span class="hidden stock-id">{{item.id}}</span>
                                <p><spring:message code='product.stock' text='Stock' /> <span id="rightColumn2" class="holder-s"></span></p>
                            </div>
                            <p class="price">
                                ${currentCurrency.isocode}
                                <span v-html="item.record.singleMinPrice"></span>
                            </p>
                        </a>
                        <button :data-product-id="item.id" data-aainteraction="buy now" type="submit" class="btn btn-buy ellipsis mat-button mat-button--action-green reloadPage" v-on:click="addCart">
                            <spring:message code='carpet.add.cart' text='Buy' />
                        </button>
                    </div>
                </div>
            </div>
        </li>
    </ul>

    <a id="recommendations-holder-prev" class="recommendations-holder__prev recommendations-holder-nav hidden" href="#" ><i class="fas fa-angle-left"></i></a>
    <a id="recommendations-holder-next" class="recommendations-holder__next recommendations-holder-nav hidden" href="#" ><i class="fas fa-angle-right"></i></a>

</div>