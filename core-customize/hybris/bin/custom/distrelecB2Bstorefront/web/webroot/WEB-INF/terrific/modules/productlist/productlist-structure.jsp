<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oscache" uri="http://www.opensymphony.com/oscache" %>
<%@ taglib prefix="nam" uri="/WEB-INF/tld/namicscommercetags.tld" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div id="productsCount" class="productlistpage">

    <div id="plp-filter-product-list" class="container productlistpage__filter-product-list">

        <mod:productlist-controllbar pageData="${searchPageData}" template="plp" skin="plp"/>

        <mod:productlist template="search-tabular" skin="plp-new standard-view" searchPageData="${searchPageData}"/>

        <mod:productlist-controllbar pageData="${searchPageData}" template="plp-bottom" skin="plp"/>

        <cms:slot var="feature" contentSlot="${slots['Content']}">
            <cms:component component="${feature}"/>
        </cms:slot>

    </div>

</div>
