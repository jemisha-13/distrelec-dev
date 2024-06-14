<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<spring:theme code="rma.guest.returnPage.confirmation" var="sConfirmationTitle"/>
<spring:theme code="feedback.nps.error" var="sErrorMsg"/>

<div class="skin-layout-return-or-repair md-system">
    <div class="md-content__holder">
        <div class="container">
            <div class="row">
                <div class="col-12">
                    <div class="data-response-holder">
                        <div class="data-response-holder__success hidden">
                            ${sConfirmationTitle}
                        </div>
                        <div class="data-response-holder__fail hidden">
                            ${sErrorMsg}
                        </div>
                    </div>
                    <mod:guest-returns-form />
                </div>
            </div>
        </div>
    </div>
</div>