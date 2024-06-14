<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<c:set var="productCalibrationService" value="${productCalibrationService}" />

<div class="modal" id="modalImage" tabindex="-1">
    <div class="hd">
        <div class="-left">
            <h3 class="title">${productName}</h3>
        </div>
        <div class="-right">
            <a class="btn btn-close" href="#" data-dismiss="modal" aria-hidden="true"><spring:message code="lightboxImage.close" /></a>
        </div>
    </div>
    <div class="bd">
        <div class="box">

            <c:if test="${productCalibrationService}">
                <div class="lightbox-image-label">
                    <div class="bd calibrationService">
                        <div class="iso">ISO</div>
                        <div class="calibrated">calibrated</div>
                    </div>
                </div>
            </c:if>

            <div class="image-offset">
                <div class="image"></div>
            </div>

            <div class="navi">
                <a class="prev"><i></i></a>
                <a class="next"><i></i></a>
            </div>

        </div>
        <div class="title"></div>
        <div class="images-counter">1 of 3</div>
    </div>
</div>