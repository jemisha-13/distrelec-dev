<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

 <form:form  method="post" modelAttribute="quotationdata" cssClass="quote-form"  id="quotationdata"  action="requestQuotation/requestQuotation">
        <div class="quote-form__customer">
            <div class="quote-form__customer--left">

                <div class="grouper__name">
                    <span class="helper-label"><spring:message code="quote.request.helper.name" /></span>

                    <div class="grouper__name__grouped">
                    	<formUtil:formSelectBox idKey="titleCode" path="title" mandatory="true" skipBlank="false" selectCSSClass="validate-empty mandatory" items="${titles}" selectedValue="${titleCode}"/>

                        <label for="firstName"></label>
                        <formUtil:formInputBox idKey="firstName" path="firstName" placeHolderKey="quote.request.helper.first.name" inputCSS="validate-empty mandatory" value="${contactAddress.firstName}" />

                        <label for="lastName"></label>
                        <formUtil:formInputBox idKey="lastName" path="lastName" placeHolderKey="quote.request.helper.last.name" inputCSS="validate-empty mandatory"  value="${contactAddress.lastName}" />
                    </div>
                </div>

                <div class="grouper">
                    <label for="email"><spring:message code="quote.request.helper.email"/></label>
                    <formUtil:formInputBox idKey="email" path="email" placeHolderKey="quote.request.helper.email" inputCSS="validate-empty mandatory" value="${contactAddress.email}" />
                </div>

                <div class="grouper grouper__tel">
                    <label for="telephone"><spring:message code="quote.request.helper.tel"/></label>
                    <formUtil:formInputBox idKey="telephone" path="telephone" placeHolderKey="quote.request.helper.tel" inputCSS="validate-empty mandatory" value="${contactAddress.phone1}" />
                </div>

            </div>
            <div class="quote-form__customer--right">
                <div class="grouper company">
                    <label for="companyName"><spring:message code="quote.request.helper.company"/></label>
                    <formUtil:formInputBox idKey="companyName" path="companyName" placeHolderKey="checkoutregister.companyName" inputCSS="validate-empty mandatory ${not empty companyName ? 'disabled' : ''}"  value="${companyName}" />
                </div>

                <div class="grouper">
                    <label for="customerNo"><spring:message code="quote.request.helper.customer.num"/></label>
                    <formUtil:formInputBox idKey="customerNo" path="customerNo" placeHolderKey="customerNo" inputCSS="validate-empty mandatory ${not empty customerNo ? 'disabled' : ''}"  value="${customerNo}" />
                </div>
            </div>

            <div class="error error-message-customer hidden">
                <p><spring:message code="quote.request.error.msg"/></p>
            </div>

        </div>

        <div class="quote-form__requirements">

            <div class="quote-form__requirements__title"><spring:message code="quote.request.requirements.title"/></div>

            <ul class="title-list">
                <li class="title-list__xs">&nbsp;</li>
                <li class="title-list__sm"><spring:message code="quote.request.qty"/></li>
                <li class="title-list__md"><spring:message code="quote.request.art"/></li>
                <li class="title-list__md"><spring:message code="quote.request.manufacturer"/></li>
                <li class="title-list__l"><spring:message code="quote.request.description"/></li>
                <li class="title-list__sm"><spring:message code="quote.request.target"/></li>
            </ul>

            <div class="quote-form__requirements-list">
                <c:forEach items="${quotationdata.rows}" varStatus="status">

                <div class="quote-form__requirements__row">
                    <div class="grouped empty">
                        <formUtil:formInputBox idKey="rows[${status.index}].rowNumber" path="rows[${status.index}].rowNumber" placeHolderKey="rowNumber" inputCSS="validate-empty grouped__rowxs disabled valueId" value="${status.index + 1}" />
                        <formUtil:formInputBox idKey="rows[${status.index}].quantity" path="rows[${status.index}].quantity" placeHolderKey="Quantity" inputCSS="validate-empty grouped__rowsm mandatory qty"  />
                        <formUtil:formInputBox idKey="rows[${status.index}].article" path="rows[${status.index}].article" placeHolderKey="article" inputCSS="validate-empty grouped__rowmd alternate-mandatory article-field"  />
                        <formUtil:formInputBox idKey="rows[${status.index}].mpn" path="rows[${status.index}].mpn" placeHolderKey="mpn" inputCSS="validate-empty grouped__rowmd  alternate-mandatory mpn-field"  />
                        <formUtil:formInputBox idKey="rows[${status.index}].description" path="rows[${status.index}].description" placeHolderKey="description" inputCSS="validate-empty grouped__rowl"  />
                        <formUtil:formInputBox idKey="rows[${status.index}].price" path="rows[${status.index}].price" placeHolderKey="price" inputCSS="validate-empty grouped__rowsm"  />
                        <i class="fa fa-trash remove-item grouped__rowxxs" v-on:click="removeRow"></i>
                    </div>
                </div>
            </c:forEach>
            </div>

            <div class="error error-message hidden">
                <p><spring:message code="quote.request.error.msg"/></p>
            </div>

            <div class="add-row">
                <a href="#" v-on:click="addRow" title="add row"><spring:message code="quote.request.add.row"/></a>
            </div>

        </div>

        <div class="quote-form__comments">
            <div class="grouper">
                <label for="requiredDeliveryDate"><spring:message code="quote.request.delivery.date"/></label>
                <formUtil:formTextarea idKey="requiredDeliveryDate" path="requiredDeliveryDate" inputCSS="validate-empty" />
            </div>
            <div class="grouper">
                <label for="reference"><spring:message code="quote.request.project"/></label>
                <formUtil:formTextarea idKey="reference" path="reference" inputCSS="validate-empty"  />
            </div>

            <div class="grouper">
                <label><spring:message code="quote.request.is.tender" /></label>

                <div class="grouper__grouped">
                    <input type="radio" id="isTenderProcess" name="isTenderProcess" value="true">
                    <label for="isTenderProcess"><spring:message code="quote.request.tender.yes"/></label>
                </div>

                <div class="grouper__grouped">
                    <input type="radio" id="quote-no" name="isTenderProcess" value="false" checked>
                    <label for="quote-no"><spring:message code="quote.request.tender.no"/></label>
                </div>
            </div>

        </div>

        <div class="quote-form__submit">
            <button type="submit" disabled="disabled" data-aainteraction="request quotation" class="mat-button mat-button--action-green requestQuotes <c:if test="${isCustomerOverQuotationLimit eq true and currentBaseStore.quotationsEnabled}"> limitPopUp</c:if>" title="Request Quotation"><spring:message code="quote.request.btn"/></button>
        </div>

</form:form>

<input id="maxRows" value="${rowlimit}" type="hidden" />