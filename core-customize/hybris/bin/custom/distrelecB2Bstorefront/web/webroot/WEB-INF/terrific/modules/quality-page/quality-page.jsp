<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<spring:theme code="qualityandlegal.fileSizeDeniedMessage" var="sFileSizeDeniedMessage"/>
<spring:theme code="qualityandlegal.fileSizeMessage" var="sFileSizeMessage"/>
<spring:theme code="qualityandlegal.fileTypeDeniedMessage" var="sFileTypeDeniedMessage"/>
<spring:theme code="qualityandlegal.fileTypeMessage" var="sFileTypeMessage"/>
<spring:theme code="qualityandlegal.introduction" var="sIntroduction"/>
<spring:theme code="qualityandlegal.reachScipDocumentation" var="sReachScipDocumentation"/>
<spring:theme code="qualityandlegal.docsDownload" var="sDocsDownload"/>
<spring:theme code="qualityandlegal.bulkDownload" var="sBulkDownload"/>
<spring:theme code="qualityandlegal.excelTemplate" var="sExcelTemplate"/>
<spring:theme code="qualityandlegal.pdfTemplate" var="sPdfTemplate"/>
<spring:theme code="qualityandlegal.fileWarning" var="sFileArticleWarning"/>
<spring:theme code="qualityandlegal.fileInvalid" var="sFileInvalid"/>
<spring:theme code="qualityandlegal.fileProductCodes" var="sFileProductCodes"/>
<spring:url value="${commonResourcePath}/images/loader.gif" var="spinnerUrl" />

<div id="qualityPage" class="mod mod-quality-page-upload">
    <div id="qualityPageBanner" class="content-banner">
        <h1 id="qualityPageBannerText">${sReachScipDocumentation}</h1>
    </div>
    <h3 id="qualityPageDownloadText">${sDocsDownload}</h3>
    ${sIntroduction}
    <br><br>
    <form:form action="/environmental-documentation-download/upload-file?${_csrf.parameterName}=${_csrf.token}" modelAttribute="file"
               id="upload-form-text" enctype="multipart/form-data" method="post">
        <div id="qualityPageContent" class="content">

            <div id="qualityPageContentItem" class="content__item">
                <form:input id="file" type="file" path="file"
                       data-file-types="xls,xlsx,csv,txt"
                       data-max-file-size="1048576"
                       data-file-size-denied-message="${sFileSizeDeniedMessage}"
                       data-file-size-message="${sFileSizeMessage}"
                       data-file-type-denied-message="${sFileTypeDeniedMessage}"
                       data-file-type-message="${sFileTypeMessage}"
                       data-file-article-warning="${sFileArticleWarning}"
                       data-file-invalid="${sFileInvalid}"
                       data-file-invalid-product-codes="${sFileProductCodes}"
                />

                <div id="qualityPageAdvancedUpload" class="advanced-upload">

                    <a id="qualityPageUploadFile" href="#" class="upload-file boxy">
                        <div id="qualityPageUploadFileItem" class="upload-file__item">

                            <div id="qualityPageBrowser" class="browser-other">
                                        <span id="qualityPageBrowserIcon" class="icon">
                                            <i class="fas fa-cloud-upload-alt" aria-hidden="true"></i>
                                        </span>
                                <span id="qualityPageBrowserText" class="text">
                                            <spring:theme code="qualityandlegal.uploaddescription"/>
                                        </span>

                            </div>

                            <div id="qualityPageBrowserButton" class="browser-ie mat-button mat-button__solid--action-blue" href="/welcome">
                                <i id="qualityPageBrowserButtonIcon"  class="fas fa-cloud-upload-alt" aria-hidden="true"></i>
                                <spring:theme code="bomdataimport.uploaddescription"/>
                            </div>

                            <span id="qualityPageBrowserFilename" class="filename">sampledata.xlsx</span>

                        </div>

                    </a>
                </div>
                <div id="qualityPageErrors" class="errors">
                    <span id="qualityPageErrorsIcon" class="errors__icon">
                            <i class="fa fa-exclamation-triangle" aria-hidden="true"></i>
                        </span>
                    <p></p>
                </div>
                <div id="qualityPageWarnings" class="warnings">
                    <span id="qualityPageWarningsIcon" class="warnings__icon">
                            <i class="fa fa-info-circle" aria-hidden="true"></i>
                        </span>
                    <p></p>
                </div>
            </div>
        </div>
    </form:form>

    <c:url value="/environmental-documentation-download/excel" var="excelDownloadUrl"/>
    <div id="qualityPageDownloadExcel" class="download initialMarginNoErrors">
        <form:form action="${excelDownloadUrl}" id="download-excel-report" method="post" modelAttribute="downloadForm">
            <form:hidden id="qualityPageExcelProductCodes" path="productCodes" value=""/>
            <span id="qualityPageExcelDownload" class="download__excel"><i class="fa fa-download" aria-hidden="true"></i><input
                    id="qualityPageExcelInput"  class="download__excel" type="submit" value="${sExcelTemplate}"/></span><br/>
        </form:form>
    </div><br>

    <c:url value="/environmental-documentation-download/pdf" var="pdfDownloadUrl"/>
    <div id="qualityPageDownloadPDF" class="download">
        <form:form action="${pdfDownloadUrl}" id="download-pdf-report" method="post" modelAttribute="downloadForm">
            <form:hidden id="qualityPagePDFProductCodes" class="productCodesPDF" path="productCodes" value=""/>
            <span id="qualityPagePDFDownload" class="download__excel"><i class="fa fa-download" aria-hidden="true"></i><input
                    id="qualityPagePDFInput" class="download__excel" type="submit" value="${sPdfTemplate}"/></span><br/>
        </form:form>
    </div>



    <div class="divider"></div>
    <c:url value="/environmental-documentation-download/bulk-certificates" var="bulkDownloadUrl"/>
    <div id="qualityPageDownloadBulk" class="download">
        <form:form action="${bulkDownloadUrl}" id="download-bulk-download" method="post" modelAttribute="downloadForm">
            <form:hidden id="qualityPageBulkDownloadProductCodes" class="productCodesBulkDownload" path="productCodes" value=""/>
           <button disabled id="qualityPageBulkDownload" class="download-bulk"><input
                    id="qualityPageBulkInput" class="download__excel" type="submit" value="${sBulkDownload}"/></button><br/>
        </form:form>
    </div>

</div>

<div id="qualityPageLoader" class="ajax-product-loader">
    <div id="qualityPageLoaderOverlay" class="background-overlay"></div>
    <div id="qualityPageMessage" class="message-wrapper">
        <div id="qualityPageLoadingMessage" class="loading-message">
            <img id="spinner" src="${spinnerUrl}" alt="spinner" class="loading-message__icon"/>
        </div>
    </div>
</div>
