<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:message code="vat.reg.codice" var="sCodice" />
<spring:message code="vat.reg.codice.error" var="sCodiceCupError" />
<spring:message code="vat.reg.codice.cig.error" var="sCodiceCigError" />
<spring:message code="vat.reg.codice.cert" var="sPostCert" />
<spring:message code="vat.reg.codice.dest" var="sCodiceDest" />
<spring:message code="vat.reg.codice.cert.label" var="sCodiceCertLabel" />
<spring:message code="vat.reg.error" var="sVatError" />
<spring:message code="vat.reg.codice.dest.title" var="sCodiceDestTitle" />
<spring:message code="vat.reg.codice.cert.title" var="sPostCertTitle" />
<spring:message code="vat.reg.label" var="sVatLabel" />
<spring:message code="vat.reg.confirm" var="sVatConfirm" />
<spring:message code="vat.reg.placeholder.options" var="sPlaceholderOptions"/>
<spring:message code="vat.reg.placeholder.cig" var="sPlaceholderCIG"/>
<spring:message code="vat.reg.placeholder.cup" var="sPlaceholderCUP"/>

<c:set var="customerType" value="${cartData.b2bCustomerData.customerType}" />
<c:set var="customerFlag" value="${customerType eq 'B2B_KEY_ACCOUNT' or customerType eq 'B2B' or customerType eq 'B2E'}" />

<div class="vat-reg <c:if test="${customerType eq 'B2C'}">vat-reg__b2c</c:if> <c:if test="${cartData.paymentMode.code eq 'CreditCard'}"> vat-reg__cc</c:if>
<c:if test="${cartData.paymentMode.code eq 'CreditCard' and not empty legalEmail or not empty vat4}"> existingCC</c:if>">

        <c:if test="${empty vat4 and not empty legalEmail}">
            <div class="vat-reg__codice-dest codiceActive hidden">
        </c:if>

         <c:if test="${empty legalEmail and not empty vat4}">
            <div class="vat-reg__codice-dest codiceActive">
         </c:if>

         <c:if test="${empty legalEmail and empty vat4}">
             <div class="vat-reg__codice-dest codiceActive">
         </c:if>

          <c:if test="${not empty legalEmail and not empty vat4}">
             <div class="vat-reg__codice-dest codiceActive">
          </c:if>

        <label class="vat-reg__label">${sCodiceDestTitle}</label>

        <c:if test="${empty vat4}">
            <label class="vat-reg__label__small">${sVatLabel}</label>
            <div class="vat-reg__input-holder">
                <input class="vat-reg__input vat-reg__input__active" id="vatcodice" type="text"  placeholder="e.g 0123456"/>
            </div>
        </c:if>

        <c:if test="${not empty vat4}">
            <div class="vat-reg__input-holder vat-reg__input-holder--disabled hasVAT">
                <input disabled="disabled" class="vat-reg__input vat-reg__input__active" id="vatcodice" type="text" value="${vat4}"/>
            </div>
        </c:if>

        <c:if test="${empty legalEmail and empty vat4}">
            <div class="vat-reg__toggle toggle-dest">
                <p class="vat-reg__toggle-default">${sPostCert}</p>
            </div>
        </c:if>

    </div>


    <c:if test="${empty vat4 and not empty legalEmail}">
        <div class="vat-reg__post-cert">
    </c:if>

     <c:if test="${empty legalEmail and not empty vat4}">
        <div class="vat-reg__post-cert hidden">
     </c:if>

      <c:if test="${empty legalEmail and empty vat4}">
         <div class="vat-reg__post-cert hidden">
      </c:if>

      <c:if test="${not empty legalEmail and not empty vat4}">
          <div class="vat-reg__post-cert hidden">
      </c:if>

        <label class="vat-reg__label">${sPostCertTitle}</label>

        <c:if test="${empty legalEmail}">
            <label class="vat-reg__label__small">${sCodiceCertLabel}</label>
            <div class="vat-reg__input-holder">
                <input class="vat-reg__input" id="vatemail" type="email" placeholder="esempio@legalmail.it"/>
            </div>
        </c:if>

        <c:if test="${not empty legalEmail}">
            <div class="vat-reg__input-holder vat-reg__input-holder--disabled hasEmail">
                <input disabled="disabled" class="vat-reg__input" id="vatemail" type="email" value="${legalEmail}"/>
            </div>
        </c:if>

        <c:if test="${empty legalEmail and empty vat}">
            <div class="vat-reg__toggle toggle-cert">
                <p class="vat-reg__toggle-default">${sCodiceDest}</p>
            </div>
        </c:if>

    </div>

      <c:if test="${customerFlag eq true}">
          <div class="vat-reg__checkbox <c:if test="${cartData.paymentMode.code eq 'CreditCard'}">hasCC</c:if>">
              <input id="vat-checkbox-input" class="checkbox-big" type="checkbox" />
              <label class="codice-label" for="vat-checkbox-input">${sCodice}</label>
          </div>

          <div class="vat-reg__flag <c:if test="${cartData.paymentMode.code eq 'CreditCard'}">vat-reg__flag__cc</c:if>">
              <div class="vat-reg__flag__item">
                  <label for="codiceCup" class="cupLabel">${sPlaceholderCUP}</label>
                  <input class="codice" id="codiceCup" placeholder="${sPlaceholderOptions}" />
                  <label class="error cupError hidden">${sCodiceCupError}</label>
              </div>
              <div class="vat-reg__flag__item">
                  <label for="codiceCig" class="cigLabel">${sPlaceholderCIG}</label>
                  <input class="codice" id="codiceCig" placeholder="${sPlaceholderOptions}" />
                  <label class="error cigError hidden">${sCodiceCigError}</label>
              </div>
          </div>
      </c:if>

    <div class="vat-reg__error hidden <c:if test="${cartData.paymentMode.code eq 'CreditCard'}">vat-reg__error__b2c</c:if>">
        <p>${sVatError}</p>
    </div>
          
     <c:if test="${cartData.paymentMode.code eq 'CreditCard'}">

         <c:if test="${not empty vat4}">
             <c:set var="confirmBtnState" value="hidden" />
         </c:if>

         <c:if test="${not empty legalEmail}">
             <c:set var="confirmBtnState" value="hidden" />
         </c:if>

         <div class="vat-reg__b2c__btn" data-class="${confirmBtnState}">
             <span>${sVatConfirm}</span>
         </div>

     </c:if>

</div>