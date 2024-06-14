<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %>

<c:set var="product" value="${orderEntry.product}" />
<c:set var="entryNumber">${orderEntry.entryNumber}</c:set>

<spring:message code="article-number.brand" var="sBrandTitle" />
<spring:message code="product.articleNumberNew" var="sArticleNumberText" />
<spring:message code="product.typeNameNew" var="sTypeNumberText" />
<spring:message code="product.tabs.download" var="sDownloadTitle" />
<spring:message code="product.downloads.datasheets" var="sDataSheetsTitle" />
<spring:message code="product.tabs.technical.attributes" var="sTechnicalAttributeTitle" />
<spring:message code="detailtabsproductinformation.family.information" text="Family Information" var="sFamilyInformationTitle" />
<spring:message code="detailtabsproductinformation.series.description" text="Series Description" var="sSeriesDescriptionTitle" />
<spring:message code="detailtabsproductinformation.article.information" text="Article Information" var="sArticleInformationTitle" />
<spring:message code="detailtabsproductinformation.environmental.information" var="sEnvironmentalInformationTitle" />
<spring:message code="detailtabsproductinformation.additional.information" var="sAdditionalInformationTitle" />
<spring:message code="detailtabsproductinformation.rohs" var="sRohsTitle" />
<spring:message code="detailtabsproductinformation.country.of.origin" var="sCountryOfOriginTitle" />
<spring:message code="detailtabsproductinformation.manufacturer" var="sDetailsTabsManuTitle" />
<spring:message code="detailtabsproductinformation.grossWeight" var="sGrossWeightTitle" />
<spring:message code="detailtabsproductinformation.dimensions" var="sDimensionsTitle" />
<spring:message code="detailtabsproductinformation.customsCode" var="sCustomsCodeTitle" />
<spring:message code="detailtabsproductinformation.unspsc5" var="sUnspscsTitle" />
<spring:message code="backorder.item.manufacturer" var="sManufacturer" text="Manufacturer" />
<spring:message code="backorder.item.article" var="sArticle" text="Article Number" />
<spring:message code="backorder.item.instock" var="sInStock" text="In Stock" />
<spring:message code="backorder.item.excl.vat" var="sExclVat" text="Excl VAT" />
<spring:message code="backorder.item.view.details" var="sViewDetails" text="View Details" />
<spring:message code="backorder.item.modal.close" var="sCloseModal" text="Close" />
<spring:message code="product.gallery.image.illustrativeTitle" var="sIllustrative" text="Image is for illustrative purposes only" />
<spring:message code="cart.list.typeName" var="sMPN" text="MPN"/>
<spring:message code="product.tabs.specificationTitle" var="sSpecificationTitle" />
<spring:message code='buyingSection.error.min.order.text' arguments="" var="sMinOrderText"/>
<spring:message code="product.image.noImage" text="This product has no images" var="sImageNoImages"/>

<div id="app${count}" class="row" v-cloak>
    <div v-for="(item, index) in items" v-if="index < 10000" class="col-12 col-sm-4 col-lg-4 card-item">
            <div class="card-item__items">
                <div class="card-item__items__image">
					<img :title="item.name" width="61" height="46" class="img-responsive" :src="item.productImageUrl">
                   
                </div>
                <div class="card-item__items__modal">
                    <a v-on:click="modalDownload" href="#" data-toggle="modal" :data-target="'#modal' + item.code" :class="item.code">${sViewDetails}</a>

                    <!-- Modal -->
                    <div class="modal fade card-item__items__modal card-item__items__modal--inner" :id="'modal' + item.code" tabindex="-1" role="dialog"
                         aria-hidden="true">
                        <div class="modal-dialog" role="document">
                            <div class="modal-content card-item__items__modal__content">
                                <div class="modal-content card-item__items__modal__content__close">
                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                        <span aria-hidden="true">${sCloseModal}</span>
                                    </button>
                                </div>
                                <div class="modal-content card-item__items__modal__content__body">
                                    <div class="modal-content card-item__items__modal__content__body__title">
                                        {{item.name}}
                                    </div>
                                    <div class="modal-content card-item__items__modal__content__body__product-information modalInformation">
                                        <p class="modalArticle">${sArticleNumberText}: <strong class="ArticleResponse">{{item.code}}</strong></p>
                                        <p class="modalMPN">${sTypeNumberText}: <strong class="typeNameResponse">{{item.typeName}}</strong></p>
                                        <p class="modalBrand">${sBrandTitle}: <a class="modalManufacturer" :href="downloadManufacturer.url" :title="downloadManufacturer.name"><strong class="distManufacturerResponse">{{downloadManufacturer.name}}</strong></a></p>


                                        <div v-if="downloadManufacturer.image !== null" class="modal-content card-item__items__modal__content__body__product-information__brand">
                                            <img class="imageResponseBrand" :src="productImageBrand.url" :alt="productImageBrand.altText"/>
                                        </div>

                                        <div class="modal-content card-item__items__modal__content__body__product-information__product-image">
                                            <img class="imageResponse" :src="productImage.url" :alt="productImage.altText"/>
                                        </div>

                                        <div class="modal-content card-item__items__modal__content__body__product-information__illustrate-text">
                                            ${sIllustrative}
                                        </div>

                                        <template v-for="classifications in download.classifications">
                                            <div v-if="classifications.features.length" class="modal-content__classifications card-item__items__modal__content__body__product-information__download">
                                                <span class="modal-content__classifications__title card-item__items__modal__content__body__product-information__download__title">${sSpecificationTitle}</span>
                                                <div v-for="features in classifications.features" class="modal-content__classifications__item">
                                                    <span class="title">
                                                        {{features.name}}:
                                                    </span>
                                                        <span v-for="featuredValue in features.featureValues" class="value">
                                                        {{featuredValue.value}}
                                                    </span>
                                                </div>
                                            </div>
                                        </template>

                                        <div class="modal-content card-item__items__modal__content__body__product-information__download" v-if="pdfDownload.length">
                                            <span class="card-item__items__modal__content__body__product-information__download__title">${sDownloadTitle}</span>

                                            <div v-for="(item, index) in pdfDownload" class="downloads-body">
                                                <div class="downloads-body__title">
                                                    {{item.title}}
                                                </div>
                                                <div v-for="downloads in item.downloads" class="downloads-body__content">
                                                    <a target="_blank" class="downloadlink" :href="downloads.downloadUrl">
                                                        <div class="content-holder">
                                                            <div class="content-holder__image">
                                                                <i class="far fa-file-pdf"></i>
                                                            </div>
                                                            <div class="content-holder__content">
                                                                 <span class="downloadName">{{downloads.name}}</span>
                                                                 <div class="downloadwrap">
                                                                   (<span class="downloadwrap__type">{{downloads.mimeType}},</span>
                                                                    <div v-for="languages in downloads.languages" class="downloadwrap__lang">
                                                                       {{languages.name}}
                                                                    </div>)
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </a>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="modal-content card-item__items__modal__content__body__product-information__download">

                                            <span class="modalDownload__title">${sTechnicalAttributeTitle}</span>

                                            <template v-if="seriesDescription.length">
                                                <span class="modalDownload__list__title">${sSeriesDescriptionTitle}</span>
                                                <ul v-for="(item, index) in seriesDescription"  class="modalDownload__lists">
                                                    <li class="modalDownload__lists__item">
                                                        {{item}}
                                                    </li>
                                                </ul>
                                            </template>

                                            <template v-if="productDescription.deliveryNote !== null">
                                                <span class="modalDownload__subtitle"><spring:message code="detailtabsproductinformation.notes" text="Notes" /></span>
                                                <span class="modalDownload__list__subTitle"><spring:message code="detailtabsproductinformation.notes.delivery" text="Delivery Note" /></span>
                                                <div  class="modalDownload__para">
                                                    <p class="modalDownload__para__item">
                                                        {{productDescription.deliveryNote}}
                                                    </p>
                                                </div>
                                            </template>

                                            <span class="modalDownload__subtitle">${sFamilyInformationTitle}</span>

                                            <template v-if="titleDescription.length">
                                                <ul v-for="(item, index) in titleDescription"  class="modalDownload__lists">
                                                    <li class="modalDownload__lists__item">
                                                        {{item}}
                                                    </li>
                                                </ul>
                                            </template>

                                            <template v-if="familyDescription.length">
                                                <div  v-for="(item, index) in familyDescription" class="modalDownload__para">
                                                    <p class="modalDownload__para__item">
                                                        {{item}}
                                                    </p>
                                                </div>
                                            </template>

                                            <span class="modalDownload__subtitle">${sArticleInformationTitle}</span>

                                            <template v-if="productDescription.articleDescription !== null">
                                                <div class="modalDownload__para" v-for="test in productDescription.articleDescription">
                                                    <p class="modalDownload__para__item">{{test}}</p>
                                                </div>
                                            </template>

                                            <template v-if="productDescription.paperCatalogPageNumber > 0">
                                                <ul  class="modalDownload__lists">
                                                    <li class="modalDownload__lists__paper"><spring:message code="detailtabsproductinformation.article.paperCatalogPageNumber.label" text="Paper catalogues" /> <strong class="type">2016/17 p. </strong><strong class="paperCatalogOne">{{productDescription.paperCatalogPageNumber}}</strong></li>
                                                </ul>
                                            </template>

                                            <template v-if="productDescription.paperCatalogPageNumber > 0">
                                                <ul  class="modalDownload__lists">
                                                    <li class="modalDownload__lists__paper"><spring:message code="detailtabsproductinformation.article.paperCatalogPageNumber.label" text="Paper catalogues" /> <strong class="type">2013/14 p. </strong><strong class="paperCatalogTwo">{{productDescription.paperCatalogPageNumber_16_17}}</strong></li>
                                                </ul>
                                            </template>

                                            <span class="modalDownload__subtitle">${sEnvironmentalInformationTitle}</span>

                                            <template v-if="download.rohs !== null">
                                                <ul  class="modalDownload__lists">
                                                    <li class="modalDownload__lists__paper">${sRohsTitle} <strong class="type">{{download.rohs}}</strong></li>
                                                </ul>
                                            </template>

                                            <template v-if="download.formattedSvhcReviewDate !== null">
                                                <ul  class="modalDownload__lists">
                                                    <li class="modalDownload__lists__paper"><spring:message code="detailtabsproductinformation.svhc.reviewDate" text="SVHC Review Date" /> <strong class="type">{{download.formattedSvhcReviewDate}}</strong></li>
                                                </ul>
                                            </template>

                                            <span class="modalDownload__subtitle">${sAdditionalInformationTitle}</span>

                                            <ul class="modalDownload__lists">
                                                <li v-if="countryOrigin.name !== null" class="modalDownload__lists__paper"><strong>${sCountryOfOriginTitle} </strong><span class="type">{{countryOrigin.name}} ({{countryOrigin.isocode}})</span></li>
                                                <li v-if="downloadManufacturer.name !== null" class="modalDownload__lists__paper"><strong>${sDetailsTabsManuTitle} </strong><span class="type">{{downloadManufacturer.name}}</span></li>
                                                <li v-if="download.grossWeight !== null" class="modalDownload__lists__paper"><strong>${sGrossWeightTitle} </strong><span class="type">{{download.grossWeight}}&nbsp;{{download.grossWeightUnit}}</span></li>
                                                <li v-if="download.dimensions !== null" class="modalDownload__lists__paper"><strong>${sDimensionsTitle} </strong><span class="type">{{download.dimensions}}</span></li>
                                                <li v-if="download.customsCode !== null" class="modalDownload__lists__paper"><strong>${sCustomsCodeTitle} </strong><span class="type">{{download.customsCode}}</span></li>
                                                <li v-if="download.unspsc5 !== null" class="modalDownload__lists__paper"><strong>${sUnspscsTitle} </strong><span class="type">{{download.unspsc5}}</span></li>
                                            </ul>

                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
                <div class="card-item__items__content">
                    <a class="card-item-anchor" :title="item.productImageAltText" :href="item.url" data-aainteraction="product click" data-section-title="Customers also bought" data-link-type="component" data-location="pdp" :data-position="index + 1" :data-product-id="item.code">
                        <h3 v-html="item.name"></h3>
                    </a>

                    <div class="card-item__items__content__product-info">
                        <p class="card-item__items__content__product-info__item"><strong>${sManufacturer}:</strong> {{item.distManufacturer.name}}</p>
                        <p class="card-item__items__content__product-info__item"><strong>${sArticle}:</strong> {{item.code}}</p>
                        <p class="card-item__items__content__product-info__item"><strong>${sMPN}:</strong> {{item.typeName}}</p>
                    </div>

                    <div  :class="['stock-' + item.code] + ' card-item__items__content__stock'">
                        <span class="hidden stock-id">{{item.code}}</span>
                        <p><span id="rightColumn2" class="holder-s"></span> ${sInStock}</p>
                    </div>
                    <p class="card-item__items__content__price">
                        <span class="card-item__items__content__price__item">{{ item.price.currency }}</span>
                        <span class="card-item__items__content__price__item" v-html="item.price.formattedValue"></span>
                        <span class="card-item__items__content__price__item--small">(${sExclVat})</span>
                    </p>
                    <div class="card-item__items__holder">
                        <div class="card-item__items__content__input btn-wrapper">
                            <div class="numeric-popover popover top hidden">
                                <div class="arrow"></div>
                                <div class="popover-content"><spring:message code="buyingSection.error.min.order.quantity" /></div>
                            </div>
                            <button class="card-item__items__content__input__btn card-item__items__content__input__btn--down" v-on:click="decrement(item)">-</button>
                            <input data-position${count}="${count}" :data-min-qty="item.orderQuantityMinimum" :data-input="item.code" :class="'inp-' + item.code +  ' item-input'" value="${qtyValue}" type="text" :ref="item.code" v-on:blur="qtyBlur(item)"/>
                            <button class="card-item__items__content__input__btn card-item__items__content__input__btn--up" v-on:click="increment(item)">+</button>
                        </div>
                        <button :data-product-id="item.code" type="submit" class="card-item__items__content__add-to-cart" v-on:click="addCart">
                            <i :data-product-id="item.code" data-cart-count="${count}" class="fas fa-cart-plus cartIcon"></i>
                        </button>

                        <c:set var="minOrderText" value="${fn:replace(sMinOrderText, '{0}', '')}" />

                        <div :data-min-qty="item.orderQuantityMinimum" class="min-order-${count}">
                            <span class="min-order">
                                {{item.orderQuantityMinimum}} ${minOrderText}
                            </span>
                        </div>
                    </div>

                </div>
            </div>
    </div>
    <div class="hidden joinStrings" id="hiddenResponse${count}"></div>
</div>