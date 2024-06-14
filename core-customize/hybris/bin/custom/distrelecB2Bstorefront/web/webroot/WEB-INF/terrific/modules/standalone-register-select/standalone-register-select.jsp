<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:message code="checkoutregister.radio.yes" var="sRadioYes" />
<spring:message code="checkoutregister.radio.no" var="sRadioNo" />
<spring:message code="register.account.type.business" var="sB2BoptionYes" />
<spring:message code="register.account.type.private" var="sB2BoptionNo" />
<spring:message code="standalone.reg.step1.title" var="sBisTitle" text="Account Type" />

<c:set var="isInter" value="${siteUid eq 'distrelec_FR'}" />


<div class="card-wrapper">
    <h3>1. ${sBisTitle}</h3>
    <div class="col-12">
        <input type="hidden" class="error-handling-class" value="${formID}">
        <div class="main-radio-options">
            <div class="col-12">
                <div class="row">
                    <c:if test="${allChannels.stream().anyMatch(channel -> channel.getType() == 'B2B').get()}">
                        <div class="col-12 col-md-6 col-lg-6">
                            <div class="form-check-inline" data-aainteraction="customer type selection" data-value="B2B">
                                <input type="radio" class="form-check-input js-ensighten-user-radio" name="user_type_select_1" id="inlineRadio1" checked value="true">
                                <label for="inlineRadio1">
                                    ${sB2BoptionYes}
                                </label>
                            </div>
                        </div>
                    </c:if>
                    <c:if test="${allChannels.stream().anyMatch(channel -> channel.getType() == 'B2C').get()}">
                        <div class="col-12 col-md-6 col-lg-6">
                            <div class="form-check-inline" data-aainteraction="customer type selection" data-value="B2C">
                                <input type="radio" class="form-check-input js-ensighten-user-radio" name="user_type_select_1" id="inlineRadio2" value="false" ${allChannels.size()==1 ? 'checked' : ''}>
                                <label for="inlineRadio2">
                                    ${sB2BoptionNo}
                                </label>
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>