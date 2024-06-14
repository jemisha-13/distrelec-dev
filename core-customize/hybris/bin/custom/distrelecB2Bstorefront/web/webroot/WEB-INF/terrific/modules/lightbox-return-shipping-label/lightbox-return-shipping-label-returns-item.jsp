<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<c:if test="${empty lightboxDenyButtonText}">
	<spring:theme code="lightboxYesNo.cancel" text="Cancel" var="lightboxDenyButtonText" />
</c:if>
<spring:theme code="lightboxYesNo.close" text="Close" var="closeButton" />
<spring:message code="rma.success.printLabel" var="sPrintLabelText" />
<spring:message code="rma.success.ID" var="sRmaID" />
<spring:message code="rma.success.return" var="sReturn" />
<spring:message code="toolsitem.print" var="sPrintLabel" />

<div class="modal base" id="returnModal" tabindex="-1">
    <div class="hd">
        <div class="hd__left">
            <h3 class="title">
                ${sPrintLabelText}
            </h3>
        </div>
        <div class="hd__right">
            <a title="${closeButton}" class="btn btn-close octagon" href="#" data-dismiss="modal" aria-hidden="true">
                <div class="octagon__inner">
                    <i class="fa fa-times" aria-hidden="true"></i>
                </div>
            </a>
        </div>
    </div>
    <div class="bd">
        <div class="bd__body">
            <div class="bd__body__item">
                <p class="rmaAddress">
                    <b class="rmaAddress__text">
                        ${rmaReturnAddress}
                    </b>
                </p>

                <span class="returnText">
                    ${sReturn}
                </span>
            </div>
            <div class="bd__body__item">
                <p class="rmaID">${sRmaID}<span class="rmaNumber-text">${rmaNumber}</span></p>
            </div>
        </div>
    </div>
    <div class="ft">
        <mod:toolsitem template="toolsitem-print-button" skin="print" tag="div" />
        <a class="cancel-btn" title="${closeButton}">
            ${closeButton}
        </a>
    </div>
</div>