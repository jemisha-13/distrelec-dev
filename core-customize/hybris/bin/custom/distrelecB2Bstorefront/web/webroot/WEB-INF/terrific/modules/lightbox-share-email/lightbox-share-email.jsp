<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<c:choose>
    <c:when test="${not empty customer && customer.name.toLowerCase() ne 'anonymous'}" >
        <c:set value="${customer.name}" var="custName" />
        <c:set value="${customer.email}" var="custMail" />
    </c:when>
    <c:otherwise>
        <c:set value="" var="custName" />
        <c:set value="" var="custMail" />
    </c:otherwise>
</c:choose>
<div class="modal modal-form-view base" tabindex="-1">
    <div class="hd">
        <div class="-left"> <h3 class="title"><spring:message code="toolsitem.share.send.to.friend" /></h3> </div>
        <div class="-right"> <a class="btn btn-close" href="#" data-dismiss="modal" aria-hidden="true"><spring:message code="toolsitem.share.close" /></a> </div>
    </div>
    <div class="bd">
        <div class="form-row">
            <label class="small" for="name"><spring:message code="toolsitem.share.your.name" /> *</label>
            <input name="name" id="name" maxlength="128" class="field ipt-big validate-empty" type="text" value="${custName}"/>
        </div>
        <div class="form-row"> <label class="small" for="email1"><spring:message code="toolsitem.share.your.email" /> *</label> </div>
        <div class="form-row"> <input name="email" id="email1" class="field ipt-big validate-email" type="text" value="${custMail}"/> </div>
        <div class="form-row"> <label class="small" for="receivername"><spring:message code="toolsitem.share.receiver.name" /> *</label>
        	<input name="receivername" id="receivername" maxlength="128" class="field ipt-big validate-empty" type="text" value="">
        </div>
        <div class="form-row"> <label class="small" for="email2"><spring:message code="toolsitem.share.receiver.email" /> *</label> </div>
        <div class="form-row"> <input name="email2" id="email2" class="field ipt-big validate-email" type="text" value=""> </div>
        <div class="form-row"> <label class="small" for="message"><spring:message code="toolsitem.share.message" /> *</label> </div>
        <div class="form-row"> <textarea name="message" id="message" rows="3" class="field ipt-big validate-empty"></textarea> </div>
		<div class="form-row"> </div>
		<div class="form-row"> <div class="recaptcha"> <mod:captcha/> </div> </div>
		<div class="required-text small"> * <spring:message code="toolsitem.share.required" /> </div>
    </div>
    <div class="ft">
        <input type="submit" class="btn btn-secondary" value="<spring:message code='toolsitem.share.cancel' />" data-dismiss="modal" aria-hidden="true" />
        <input type="submit" class="btn mat-button--action-green  btn-save" value="<spring:message code='toolsitem.share.ok' />" />
    </div>
</div>
<div class="modal modal-sent-view">
    <div class="hd">
        <div class="-left"> <h3 class="title"><spring:message code="toolsitem.share.sent" />!</h3> </div>
        <div class="-right"> <a class="btn btn-close" href="#" data-dismiss="modal" aria-hidden="true"><spring:message code="toolsitem.share.close" /></a> </div>
    </div>
    <div class="bd">
        <div class="success-box">
            <h4><spring:message code="toolsitem.share.success" />!</h4>
            <p><spring:message code="toolsitem.share.message.sent" /></p>
        </div>
        <p class="message-text"></p>
        <small><spring:message code="toolsitem.share.send.to" />&nbsp;<strong class="edit-email"></strong></small>
    </div>
    <div class="ft"> <input type="submit" class="btn mat-button--action-green btn-ok btn-save" value="<spring:message code='toolsitem.share.ok' />" /> </div>
</div>
<div class="modal modal-sent-view-error">
    <div class="hd">
        <div class="-left"> <h3 class="title"><spring:message code="toolsitem.share.sent" />!</h3> </div>
        <div class="-right"> <a class="btn btn-close" href="#" data-dismiss="modal" aria-hidden="true"><spring:message code="toolsitem.share.close" /></a> </div>
    </div>
    <div class="bd">
        <div class="error-box">
            <h4><spring:message code="toolsitem.share.error" />!</h4>
            <p><spring:message code="toolsitem.share.message.error" /></p>
        </div>
        <p class="message-text"></p>
    </div>
    <div class="ft"> <input type="submit" class="btn btn-primary btn-cancel" value="<spring:message code='toolsitem.share.ok' />" /> </div>
</div>
<script id="tmpl-lightbox-share-email-validation-error-empty" type="text/template">
	<spring:message code="validate.error.required" />
</script>
<script id="tmpl-lightbox-share-email-validation-error-email" type="text/template">
	<spring:message code="validate.error.email" />
</script>
<script id="tmpl-lightbox-share-email-validation-error-captcha" type="text/template">
	<spring:message code="validate.error.captcha" />
</script>