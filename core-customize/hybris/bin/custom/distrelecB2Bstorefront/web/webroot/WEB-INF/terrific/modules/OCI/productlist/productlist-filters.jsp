
<c:set value="${searchPageData.pagination.totalNumberOfResults}" var="matchedProductsCount" />

<c:set value="" var="singleResultProductCount" />

<div data-matchedproductscount="${matchedProductsCount}">

    <c:if test="${matchedProductsCount eq '1' }">
        <c:set value="hidden" var="singleResultProductCount" />
    </c:if>

    <div class="productlistpage__filter-view-search hide ${singleResultProductCount}">
        <div class="col-lg-3 col-xl-2 productlistpage__filter-view">
            <mod:facets template="plp-filter-view" skin="plp-filter-view" searchPageData="${searchPageData}" />
            <cms:slot var="feature" contentSlot="${slots['TeaserContent']}">
                <cms:component component="${feature}"/>
            </cms:slot>
        </div>

        <div class="col-lg-9 col-xl-10 productlistpage__filter-search">
            <mod:facets template="plp-filter-search" skin="plp-filter-search" searchPageData="${searchPageData}" />
        </div>
    </div>

    <div id="plp-filter-action-bar" class="productlistpage__filter-action-bar hidden">
        <div class="container">
            <mod:facets template="plp-filter-controllbar" skin="plp-filter-controllbar" searchPageData="${searchPageData}" />
        </div>

    </div>

    <c:if test="${matchedProductsCount != '1'}">
        <button type="submit" class="productlistpage__show-hide-filter mat-button mat-button__solid--action-blue" data-show-text="<spring:message code="text.plp.filters.show" />" data-hide-text="<spring:message code="text.plp.filters.hide" />"> <spring:message code="text.plp.filters.show" /> </button>
    </c:if>

    <div class="container">
        <div class="row">
            <div class="plp-content__filter-mobile-switch">
                <mod:mobile-filter-switch />
            </div>
        </div>
    </div>

</div>

