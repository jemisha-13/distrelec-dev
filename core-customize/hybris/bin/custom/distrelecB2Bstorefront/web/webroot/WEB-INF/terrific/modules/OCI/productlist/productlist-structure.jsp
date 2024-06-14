<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oscache" uri="http://www.opensymphony.com/oscache" %>
<%@ taglib prefix="nam" uri="/WEB-INF/tld/namicscommercetags.tld" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div id="productsCount" class="productlistpage">

            <c:set value="${searchPageData.pagination.totalNumberOfResults}" var="matchedProductsCount" />

            <c:set value="" var="singleResultProductCount" />

        <div id="productsCount" class="productlistpage" data-matchedproductscount="${matchedProductsCount}">

            <c:if test="${matchedProductsCount eq '1' }">
                <c:set value="hidden" var="singleResultProductCount" />
            </c:if>

            <div class="row productlistpage__filter-view-search ${singleResultProductCount}">
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
                <mod:facets template="plp-filter-controllbar" skin="plp-filter-controllbar" searchPageData="${searchPageData}" />
            </div>

            <div class="container">
                <div class="row">
                    <div class="plp-content__filter-mobile-switch">
                        <mod:mobile-filter-switch />
                    </div>
                </div>
            </div>

            <mod:toolsitem template="toolsitem-compare-popup-plp" skin="compare-popup-plp" tag="div" productId="${productId}"/>

    <div id="plp-filter-product-list" class="container productlistpage__filter-product-list">

        <mod:productlist-controllbar pageData="${searchPageData}" template="plp" skin="plp" />

        <mod:productlist template="search-tabular" skin="plp-new standard-view" searchPageData="${searchPageData}"/>

        <mod:productlist-controllbar pageData="${searchPageData}" template="plp-bottom" skin="plp" />

        <cms:slot var="feature" contentSlot="${slots['Content']}">
            <cms:component component="${feature}"/>
        </cms:slot>

    </div>

</div>
