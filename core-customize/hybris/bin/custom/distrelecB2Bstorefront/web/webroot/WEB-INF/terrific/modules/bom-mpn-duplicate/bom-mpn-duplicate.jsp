<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %>

<spring:theme code="bomnomatches.unavailableTitle" var="sMainTitle" />
<spring:theme code="bomnomatches.title" var="sSubTitle" />

<spring:theme code="bomnomatches.quantity" var="sQuantity" />
<spring:theme code="bomnomatches.reference" var="sReference" />

<spring:message code="product.product.details" var="sProductDetail" />
<spring:message code="product.typeName" var="sMPN" />


<spring:theme code="bomnomatches.manufacturer" var="sManufacturer" />
<spring:message code="product.articleNumber" var="sArticle" />
<spring:message code="backorder.item.excl.vat" var="sExclVat" text="Excl VAT" />

<spring:message code="basket.select.product" var="sSelect" text="Select" />
<spring:message code="text.selected" var="sSelected" text="Selected" />
<spring:message code="backorder.item.view.details" var="sViewDetails" text="View Details" />

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



<spring:message code='bom.duplicatempn.message.info' arguments="" var="duplicateMpnMessageInfo"/>


<c:if test="${not empty duplicateMpnProductList}">

    <c:forEach items="${duplicateMpnProductList}" var="unavailableProduct">

        <div class="bom-mpn-product">

            <mod:global-messages template="component" skin="component info bom-product"  headline='' body='${duplicateMpnMessageInfo}' type="info"/>

            <div class="bom-mpn-product__info">

                <div>
                    <div class="bom-mpn-product__info-box bom-product">
                        <span class="bom-mpn-product__original-request"><strong><spring:message code="text.original.request" />:</strong></span>
                        <span class="bom-mpn-product__title">${unavailableProduct.quantity}, ${unavailableProduct.mpn}, ${unavailableProduct.reference}</span>
                    </div>

                    <button class="mat-button mat-button--action-blue bom-mpn-product__show-alternative"
                            data-aainteraction="show product alternatives" data-aabutton="show" data-aabuttonshow="show" data-aabuttonhide="hide" data-aabuttonchange="change"
                            data-product-code="${unavailableProduct.productCode}" data-show-text="<strong><spring:message code="text.showmultipleproducts" />"
                            data-hide-text="<spring:message code="text.hidemultipleproducts" />" data-change-text="<spring:message code="text.changeselectedproducts" />">
                        <spring:message code="text.showmultipleproducts" />
                    </button>

                </div>
            </div>


            <c:set var="duplicateMpnProductLenght" value="${fn:length(unavailableProduct.duplicateMpnProducts)}" />

            <c:forEach items="${unavailableProduct.duplicateMpnProducts}" var="mpnproduct" varStatus="lStatus">
                <div class="bom-mpn-product__alternatives-selection bom-mpn-product__item bom-mpn-product__item--${mpnproduct.code}">
                    <mod:product template="bom" skin="bom" tag="div" product="${mpnproduct}" producttype="alternative" carriedReference="${unavailableProduct.reference}" carriedQuantity="${unavailableProduct.quantity}"/>
                </div>
            </c:forEach>

            <div class="row bom-mpn-product__alternatives hidden">

                <c:forEach items="${unavailableProduct.duplicateMpnProducts}" var="product" varStatus="lStatus">

                    <div class="col-12 col-sm-4 col-lg-3 card-item bom-mpn-product__item bom-mpn-product__item--${product.code}" data-status-code="${product.salesStatus}">

                        <div class="card-item__items">
                            <div class="card-item__items__image">
                                <c:set var="productImage" value="${product.productImages[0]}"/>
                                <c:set var="portraitSmallJpg" value="${not empty productImage.portrait_small.url ? productImage.portrait_small.url : '/_ui/all/media/img/missing_portrait_small.png' }"/>
                                <c:set var="portraitSmallWebP" value="${not empty productImage.portrait_small_webp.url ? productImage.portrait_small_webp.url : portraitSmallJpg}"/>
                                <c:set var="portraitSmallAlt" value="${not empty productImage.portrait_small_webp.altText ? productImage.portrait_small_webp.altText : not empty productImage.portrait_small.altText == null ? productImage.portrait_small.altText : sImageMissing }"/>
                                <picture>
                                    <source srcset="${portraitSmallWebP}">
                                    <img width="61" height="46" alt="${portraitSmallAlt}" src="${portraitSmallJpg}"/>
                                </picture>
                            </div>
                            <div class="card-item__items__modal">


                                <button type="button" id="viewdetails-${product.code}" data-toggle="modal" data-product-code="${product.code}"
                                        data-target="#modal-${product.code}"
                                        class="bom-mpn-product__view-detail mat-button mat-button__normal--action-blue" >
                                        ${sViewDetails}</button>

                                <!-- Modal -->
                                <div id="modal-${product.code}" class="modal fade modal-${product.code}" role="dialog">
                                    <div class="modal-dialog">

                                        <!-- Modal content-->
                                        <div class="modal-content">

                                            <div class="modal-body">
                                                <div class="modal-dialog" role="document">
                                                    <div class="modal-content card-item__items__modal__content">
                                                        <button type="button" class="close modal-content modal__close" data-dismiss="modal" aria-label="Close">
                                                            <span aria-hidden="true">${sCloseModal}</span>
                                                        </button>
                                                        <div class="modal-content modal__body">
                                                            <div class="modal-content modal__title">
                                                                    ${product.name}
                                                            </div>
                                                            <div class="modal-content modal__product-information modalInformation">
                                                                <p class="modalArticle">${sArticleNumberText}: <strong class="ArticleResponse">${product.code}</strong></p>
                                                                <p class="modalMPN">${sTypeNumberText}: <strong class="typeNameResponse">${product.typeName}</strong></p>
                                                                <p class="modalBrand">${sBrandTitle}: <a class="modalManufacturer" :href="downloadManufacturer.url" :title="downloadManufacturer.name"><strong class="distManufacturerResponse">{{downloadManufacturer.name}}</strong></a></p>


                                                                <div v-if="downloadManufacturer.image !== null" class="modal-content modal__product-brand">
                                                                    <img class="imageResponseBrand" :src="productImageBrand.url" :alt="productImageBrand.altText"/>
                                                                </div>

                                                                <div class="modal-content modal__product-image">
                                                                    <img class="imageResponse" :src="productImage.url" :alt="productImage.altText"/>
                                                                </div>

                                                                <div class="modal-content modal__illustrate-text">
                                                                        ${sIllustrative}
                                                                </div>

                                                                <template v-for="classifications in download.classifications">
                                                                    <div v-if="classifications.features.length" class="modal-content__classifications modal__download">
                                                                        <span class="modal-content__classifications__title modal__download-title">${sSpecificationTitle}</span>
                                                                        <div v-for="features in classifications.features" class="modal-content__classifications__item">
                                                                            <span class="title">
                                                                                {{features.name}}:
                                                                            </span>
                                                                            <span v-for="featuredValue in features.featureValues" class="value">
                                                                                {{featuredValue.value}}  <span v-if="features.featureUnit !== null">{{features.featureUnit.name}}</span>
                                                                            </span>
                                                                        </div>
                                                                    </div>
                                                                </template>

                                                                <div class="modal-content modal__download" v-if="pdfDownload.length">
                                                                    <span class="modal__download-title">${sDownloadTitle}</span>

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

                                                                <div class="modal-content modal__download">

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

                                    </div>
                                </div>
                            </div>
                            <div class="card-item__items__content">
                                <a class="card-item-anchor" href="${product.url}" data-aainteraction="bom alternative click" data-product-id="${product.code}" data-position="${lStatus.count}" data-pagination="Grid" data-component="${componentId}" data-scenarioID="${recoType}" data-stock="${stockLevels[product.code]}">
                                    <c:set var="productName" value="${fn:substring(product.name, 0, 60)}" />
                                    <h3>${fn:escapeXml(productName)}</h3>
                                </a>

                                <div class="card-item__items__content__product-info">
                                    <p class="card-item__items__content__product-info__item"><strong>${sManufacturer}:</strong> ${product.distManufacturer.name}</p>
                                    <p class="card-item__items__content__product-info__item"><strong>${sArticle}:</strong> ${product.codeErpRelevant} </p>
                                </div>

                                <div  class="stock-${product.code} card-item__items__content__stock">
                                    <mod:erp-sales-status productArtNo="${product.code}" productStatusCode="${product.salesStatus}"/>
                                </div>
                                <p class="card-item__items__content__price">
                                    <span class="card-item__items__content__price__item"><format:fromPrice priceData="${product.price}"/></span>
                                    <span class="card-item__items__content__price__item--small">(${sExclVat})</span>
                                </p>

                                <button class="bom-mpn-product__alternatives-select mat-button mat-button__solid--action-green ellipsis"
                                        data-aainteraction="select alternative" data-product-code="${product.code}" title='${sSelect}' text="Select">
                                    <span class="select-text"> ${sSelect} </span>
                                    <span class="selected-text"> ${sSelected} </span>
                                </button>

                            </div>
                        </div>

                    </div>
                </c:forEach>



            </div>

        </div>

    </c:forEach>


</c:if>