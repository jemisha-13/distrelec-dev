<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<spring:message code="product.image.noImage" text="This product has no images" var="sImageNoImages"/>

<div class="product-family">
    <div class="product-family--oci-wrap">
    <div class="product-family__aside">
        <c:choose>
            <c:when test="${not empty productFamily.familyImage}">
                <c:set var="familyCollectionLength" value="${fn:length(productFamily.familyImage)}" />
                    <div class="mod mod-product-image-gallery product-zoom-wrapper">
                        <div class="message-container-gallery" data-i18n-m360-loading="Loading..." data-i18n-m360-hint-text="Drag to Spin" data-i18n-m360-mobile-hint-text="Swipe to Spin"></div>
                            <div class="zoom-gallery">
                                <div class="imagery-holder">
                                    <div data-slide-id="zoom-item" class="zoom-gallery-slide active">
                                        <c:forEach items="${productFamily.familyImage}" var="item" varStatus="index">
                                            <c:choose>
                                                <c:when test="${familyCollectionLength == 5}">
                                                    <c:if test="${index.count == 5}">
                                                        <a class="MagicZoom" id="zoom-item-v"
                                                            data-options="zoomMode: magnifier; zoomPosition: inner; textHoverZoomHint: Hover to zoom"
                                                            href="${item.value.url}">
                                                        <img class="family-image-large images-in-lightbox" src="${item.value.url}" alt="${item.value.name}" arguments="" argumentSeparator="kkkkkkkkk"/>
                                                    </c:if>
                                                </c:when>
                                                <c:when test="${familyCollectionLength > 1 and familyCollectionLength < 5}">
                                                    <c:if test="${index.last}">
                                                        <a class="MagicZoom" id="zoom-item-v"
                                                            data-options="zoomMode: magnifier; zoomPosition: inner; textHoverZoomHint: Hover to zoom"
                                                            href="${item.value.url}">
                                                        <img class="images-in-lightbox" src="${item.value.url}" alt="${item.value.name}" arguments="" argumentSeparator="kkkkkkkkk"/>
                                                    </c:if>
                                                </c:when>
                                                <c:otherwise>
                                                    <a class="MagicZoom" id="zoom-item-v"
                                                        data-options="zoomMode: magnifier; zoomPosition: inner; textHoverZoomHint: Hover to zoom"
                                                        href="${item.value.url}">
                                                     <img class="images-in-lightbox" src="${item.value.url}" alt="${item.value.name}" arguments="" argumentSeparator="kkkkkkkkk"/>
                                                </c:otherwise>
                                            </c:choose>
                                         </c:forEach>
                                      </a>
                            </div>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
               <!--  DISTRELEC-21819 - To  remove comment when we have new Images
               	<c:choose>
                    <c:when test="${currentCountry.isocode eq 'DK' || currentCountry.isocode eq 'FI' || currentCountry.isocode eq 'NO' || currentCountry.isocode eq 'SE'
									 || currentCountry.isocode eq 'LT' || currentCountry.isocode eq 'LV' || currentCountry.isocode eq 'EE' || currentCountry.isocode eq 'PL'}" >
                        <img alt="${sImageNoImages}" title="${sImageNoImages}" src="/_ui/all/media/img/missing_landscape_medium-elfa.png" />
                    </c:when>
                    <c:when test="${currentCountry.isocode eq 'FR'}" >
                        <img alt="${sImageNoImages}" title="${sImageNoImages}" src="/_ui/all/media/img/missing_landscape_medium-fr.png" />
                    </c:when>
                    <c:otherwise>
                        <img alt="${sImageNoImages}" title="${sImageNoImages}" src="/_ui/all/media/img/missing_landscape_medium.png" />
                    </c:otherwise>
                </c:choose>--> 
            </c:otherwise>
        </c:choose>
        <a class="product-family__aside--brand" href="${manufacturerUrl}"><spring:message code="product.family.view.brand" /></a>
    </div>

    <div class="product-family__main">
        <div class="product-family__main--header">
            <c:if test="${not empty productFamily.familyManufacturerImage}">
                <c:forEach items="${productFamily.familyManufacturerImage}" var="item">
                    <c:set var = "imageName" value = "${item.value.name}"/>
                    <c:if test = "${fn:endsWith(imageName, '_bl')}">
                        <div class="img-wrap">
                            <img src="${item.value.url}" alt="${imageName}" />
                        </div>
                    </c:if>
                </c:forEach>
            </c:if>
            <div class="title-wrap">
                <span>${productFamily.name}</span>
            </div>
        </div>

        <div class="jump-to">
            <span><spring:message code="product.family.jump.link" /></span>
            <ul>
                <c:if test="${not empty productFamily.familyBullets}">
                    <li class="scroll-to" data-scroll="feature"><spring:message code="product.family.features" /></li>
                </c:if>
                <c:if test="${not empty productFamily.familyApplications}">
                    <li class="scroll-to" data-scroll="applications"><spring:message code="product.family.applications" /></li>
                </c:if>
                <c:if test="${not empty productFamily.familyMedia}">
                    <li class="scroll-to" data-scroll="schematics"><spring:message code="product.family.schematics" /></li>
                </c:if>
                <c:if test="${not empty searchPageData}">
                    <li class="scroll-to" data-scroll="listing"><spring:message code="product.family.product.list" /></li>
                </c:if>
            </ul>
        </div>

        <c:if test="${not empty productFamily.introText}">
            <div class="product-family__main--intro-text">
                <p>${productFamily.introText}</p>
            </div>
        </c:if>

        <c:if test="${not empty productFamily.familyBullets}">
            <div class="product-family__main--features" id="feature">
                <span class="title"><spring:message code="product.family.features" /></span>
                <ul>
                    <c:forEach var="item" items="${productFamily.familyBullets}">
                        <li>
                            <span>${item.value}</span>
                        </li>
                    </c:forEach>
                </ul>
                <c:if test="${not empty productFamily.familyDatasheet}">
                    <c:forEach items="${productFamily.familyDatasheet}" var="datasheet">
                        <a href="${datasheet.value.url}" target="_blank" title="${datasheet.value.name}"><spring:message code="product.family.view.datasheet" /></a>
                    </c:forEach>
                </c:if>
            </div>
        </c:if>

        <c:if test="${not empty productFamily.familyApplications}">
            <div class="product-family__main--features" id="applications">
                <span class="title"><spring:message code="product.family.applications" /></span>
                <ul>
                <c:forEach items="${productFamily.familyApplications}" var="item">
                    <li>
                        <span>${item}</span>
                    </li>
                </c:forEach>
                </ul>
            </div>
        </c:if>

        <c:if test="${not empty productFamily.familyMedia}">
            <div class="product-family__main--features" id="schematics">
                <span class="title"><spring:message code="product.family.schematics" /></span>
                <c:forEach items="${productFamily.familyMedia}" var="item">
                    <c:forEach items="${item}" var="itemChild" varStatus="index">
                        <c:if test="${index.count eq 1}">
                            <img src="${itemChild.value.url}" alt="${itemChild.value.name}" />
                        </c:if>
                    </c:forEach>
                </c:forEach>

                <c:if test="${not empty productFamily.familyVideo}">
                    <div class="video-container">
                        <c:forEach items="${productFamily.familyVideo}" var="productVideo" varStatus="status">
                            <mod:video videoId="${productVideo.youtubeUrl}" />
                        </c:forEach>
                    </div>
                </c:if>

            </div>
        </c:if>

    </div>
    </div>

    <div class="product-family__main--productlist" id="listing">
        <span class="title-listing"><spring:message code="product.family.product.list" /></span>

        <div class="plp-content md-system">
            <mod:productlist template="structure" skin="structure" searchPageData="${searchPageData}"/>
        </div>

    </div>

</div>
