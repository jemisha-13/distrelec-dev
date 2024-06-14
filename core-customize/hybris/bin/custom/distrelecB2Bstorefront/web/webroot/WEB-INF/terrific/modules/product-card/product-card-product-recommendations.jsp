<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="maxVisibleItems" value="4"/>


<spring:eval expression="@configurationService.configuration.getString('factfinder.json.reco.url')" var="ffrecoUrl" scope="application" />

<div class="recommendations-holder"
     data-ajax-url="${productFFCarouselDataPath}"
     data-ff-search-url="${ffrecoUrl}"
     data-ff-search-channel="${ffsearchChannel}"
     data-ff-producterp="${product.codeErpRelevant}"
>
    <h3 class="base" title="${title}">${title}</h3>

    <ul id="app" class="row">
        <li v-for="(item, index)  in items" class="col-12 col-sm-6 col-lg-3">
            <a :href="item.record.ProductURL" :title="item.record.Title" data-aainteraction="product click" data-section-title="${title}" data-link-type="component" data-location="pdp" :data-position="index + 1" :data-product-id="item.id">
                <div class="card-item">
                    <div class="card-item__image">
                        <img :title="item.record.Title" :alt="item.record.Title"  width="61" height="46" class="img-responsive" :src="item.record.ImageURL">
                    </div>
                    <div class="card-item__content">
                        <h3 v-html="item.record.Title"></h3>
                        <div id="wrapper3" :class="['stock-' + item.id]">
                            <span class="hidden stock-id">{{item.id}}</span>
                            <p><spring:message code='product.stock' text='Stock' /> <span id="rightColumn2" class="holder-s"></span></p>
                        </div>
                        <p class="price">
                            ${currentCurrency.isocode}
                            <span v-html="item.record.singleMinPrice"></span>
                        </p>
                    </div>
                </div>
            </a>
        </li>
    </ul>

</div>