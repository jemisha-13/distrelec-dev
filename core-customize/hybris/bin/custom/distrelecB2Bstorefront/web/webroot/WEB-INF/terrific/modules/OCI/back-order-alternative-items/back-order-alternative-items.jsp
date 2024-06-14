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
<spring:message code='buyingSection.error.min.order.text' arguments="" var="sMinOrderText"/>
<spring:message code="product.tabs.specificationTitle" var="sSpecificationTitle" />

<div id="alternative-items-${entryNumber}" class="col-12 col-lg-4 card-item ${hasAlternate} isOCI">
    <div class="card-item__items">
        <div class="card-item__items__image imageParent">
            <img title="" width="61" height="46" class="img-responsive imageResponse" src="">
        </div>

        <div class="card-item__items__modal">
            <a class="modalDownload" href="#" data-toggle="modal">${sViewDetails}</a>
            <!-- Modal -->
            <div class="modal fade card-item__items__modal card-item__items__modal--inner modalDownalodSibling" tabindex="-1" role="dialog"
                 aria-hidden="true">

                <div class="modal-dialog" role="document">
                    <div class="modal-content card-item__items__modal__content">
                        <div class="modal-content card-item__items__modal__content__close">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">${sCloseModal}</span>
                            </button>
                        </div>
                        <div class="modal-content card-item__items__modal__content__body modalBody">
                            <div class="modal-content card-item__items__modal__content__body__title modalTitle">
                                {{item.name}}
                            </div>
                            <div class="modal-content card-item__items__modal__content__body__product-information modalInformation">
                                <p class="modalArticle">${sArticleNumberText}: <strong class="ArticleResponse"></strong></p>
                                <p class="modalMPN">${sTypeNumberText}: <strong class="typeNameResponse"></strong></p>
                                <p class="modalBrand">${sBrandTitle}: <a class="modalManufacturer" href="#" title="Etri"><strong class="distManufacturerResponse"></strong></a></p>

                                <div class="modal-content card-item__items__modal__content__body__product-information__brand">
                                    <img class="imageResponseBrand" src="#" alt="#"/>
                                </div>
                                <div class="modal-content card-item__items__modal__content__body__product-information__product-image">
                                    <img class="imageResponse" src="#" alt="#"/>
                                </div>
                                <div class="modal-content card-item__items__modal__content__body__product-information__illustrate-text">
                                    ${sIllustrative}
                                </div>
                                <div class="modal-content__classifications card-item__items__modal__content__body__product-information__classification">
                                    <span class="modal-content__classifications__title card-item__items__modal__content__body__product-information__classification__title">${sSpecificationTitle}</span>
                                </div>
                                <div class="modal-content card-item__items__modal__content__body__product-information__download card-item__items--download-section hidden">
                                    <span class="card-item__items__modal__content__body__product-information__download__title">${sDownloadTitle}</span>
                                    <span class="card-item__items__modal__content__body__product-information__download__subtitle">${sDataSheetsTitle}</span>
                                    <div class="modal-content card-item__items__modal__content__body__product-information__download__item">
                                        <a class="downloadLink" href="#">
                                            <i class="far fa-file-pdf"></i>
                                            <div class="card-item__items__modal__content__body__product-information__download__item__grouped">
                                                <span class="downloadName"></span>
                                                <span class="downloadWrapper"><span class="downloadMime"></span><span>,&nbsp;</span><span class="downloadLanguage"></span></span>
                                            </div>
                                        </a>
                                    </div>
                                </div>
                                <div class="modal-content card-item__items__modal__content__body__product-information__download">
                                    <span class="modalDownload__title">${sTechnicalAttributeTitle}</span>
                                    <span class="modalDownload__subtitle">${sFamilyInformationTitle}</span>
                                    <span class="modalDownload__list__title">${sSeriesDescriptionTitle}</span>
                                    <ul class="modalDownload__list">
                                        <li class="familyDescOne hidden"></li>
                                        <li class="familyDescTwo hidden"></li>
                                        <li class="familyDescThree hidden"></li>
                                    </ul>
                                    <span class="modalDownload__subtitle">${sArticleInformationTitle}</span>
                                    <ul class="modalDownload__unlist">
                                        <li class="modalDownload__lists__paper"><spring:message code="detailtabsproductinformation.article.paperCatalogPageNumber.label" text="Paper catalogues" /> <strong class="type"> 2016/17 p. </strong><strong class="paperCatalogOne"></strong></li>
                                        <li class="modalDownload__lists__paper"><spring:message code="detailtabsproductinformation.article.paperCatalogPageNumber.label" text="Paper catalogues" /> <strong class="type"> 2013/14 p. </strong><strong class="paperCatalogTwo"></strong></li>
                                    </ul>
                                    <span class="modalDownload__subtitle">${sEnvironmentalInformationTitle}</span>
                                    <ul class="modalDownload__unlist">
                                        <li><strong>${sRohsTitle}</strong><span class="rohs hidden"></span></li>
                                    </ul>
                                    <span class="modalDownload__subtitle">${sAdditionalInformationTitle}</span>
                                    <ul class="modalDownload__unlist">
                                        <li><strong>${sCountryOfOriginTitle}</strong><span class="countryOrigin"></span></li>
                                        <li><strong>${sDetailsTabsManuTitle}</strong><span class="manufacturer"></span></li>
                                        <li><strong>${sGrossWeightTitle}</strong><span class="grossWeight hidden"></span></li>
                                        <li><strong>${sDimensionsTitle}</strong><span class="dimensions hidden"></span></li>
                                        <li><strong>${sCustomsCodeTitle}</strong><span class="customs hidden"></span></li>
                                        <li><strong>${sUnspscsTitle}</strong><span class="unspc hidden"></span></li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>

        <div class="card-item__items__content itemContent">
            <a class="card-item-anchor" title="itemproductImageAltText" href="#itemurl" data-aainteraction="product click" data-section-title="Customers also bought" data-link-type="component" data-location="pdp" data-position="index" data-product-id="itemcode">
                <h3 class="productNameResponse"></h3>
            </a>

            <div class="card-item__items__content__product-info productInfo">
                <p class="card-item__items__content__product-info__item manufacturerInfo" id="distManufacturerName"><strong>${sManufacturer}:</strong><span class="manufacturerResponse"></span></p>
                <p class="card-item__items__content__product-info__item articleInfo" id="distItem"><strong>${sArticle}:</strong><span class="articleNrResponse"></span></p>
                <p class="card-item__items__content__product-info__item articleInfo" id="distMpn"><strong>${sMPN}:</strong><span class="mpnResponse"></span></p>
            </div>

            <div class="card-item__items__content__stock parent-stock">
                <span class="hidden stock-id"></span>
                <p><span id="rightColumn2" class="holder-s"></span> ${sInStock}</p>
            </div>
            <p class="card-item__items__content__price">
                <span class="card-item__items__content__price__item" id="itemCurrency"></span>
                <span class="card-item__items__content__price__item" id="itemCodePrice"></span>
                <span class="card-item__items__content__price__item--small">(${sExclVat})</span>
            </p>
            <div class="card-item__items__holder">
                <div class="card-item__items__content__input btn-wrapper">
                    <div class="numeric-popover popover top hidden">
                        <div class="arrow"></div>
                        <div class="popover-content"><spring:message code="buyingSection.error.min.order.quantity" /></div>
                    </div>
                    <button class="card-item__items__content__input__btn card-item__items__content__input__btn--down qtyDecrement">-</button>
                    <input data-position${count}="${count}" class="itemCode inputResponse" value="${qtyValue}" type="text" />
                    <button class="card-item__items__content__input__btn card-item__items__content__input__btn--up qtyIncrement">+</button>
                </div>
                <button type="submit" class="card-item__items__content__add-to-cart addCartFN">
                    <i data-cart-count="${count}" class="fas fa-cart-plus cartIcon"></i>
                </button>
                <div class="min-order-${count}">
                    <span class="min-order">
                        ${sMinOrderText}
                    </span>
                </div>
            </div>

        </div>
    </div>
    <div class="hidden joinStrings" id="hiddenResponse${count}"></div>
</div>
