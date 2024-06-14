<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="namics" uri="/WEB-INF/tld/namicscommercetags.tld"%> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ attribute name="priceData" type="de.hybris.platform.commercefacades.product.data.PriceData" %>
<%@ attribute name="format" required="true" type="java.lang.String" %>
<%@ attribute name="displayValue" type="java.math.BigDecimal" %>
<%@ attribute name="fallBackCurrency" type="java.lang.String" %>
<%@ attribute name="explicitMaxFractionDigits" type="java.lang.Double" %>

<c:if test="${!empty currentSalesOrg.countryIsocode}"> <%-- should never happen --%>
	<c:set var="countryIso" value="${fn:toUpperCase(currentSalesOrg.countryIsocode)}"/>
	<c:choose>
		<c:when test="${empty currentLanguage.isocode}"> <%-- nor should this --%>
			<fmt:setLocale value="${countryIso}" scope="session"/>
		</c:when>
		<c:otherwise>
			<c:set var="languageIso" value="${fn:toLowerCase(currentLanguage.isocode)}"/>
			<c:choose>
				<c:when test="${countryIso eq 'CH'}">
					<fmt:setLocale value="'en'" scope="session"/>
				</c:when>
				<c:otherwise>
					<fmt:setLocale value="${languageIso}_${countryIso}" scope="session"/> <%-- Default setting --%>
					<c:if test="${languageIso eq 'en'}"> <%-- override default in special cases --%>
						<spring:eval expression="@configurationService.configuration.getString('distrelec.decimal.comma.countries')" var="decimalCommaCountries" scope="application" />
						<c:if test="${fn:contains(decimalCommaCountries, countryIso)}"> <%-- use decimal comma instead of dot --%>
							<fmt:setLocale value="de_${countryIso}" scope="session"/> <%-- using "de" as language code sets decimal delimiter to "," --%>
						</c:if>
					</c:if>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
</c:if>

<%--
 Make possible to set different value than in the priceData.
 Usefull for example in cart where the total value has to be displayed.
--%>
<c:set var="priceVal" value="${empty displayValue ? priceData.value : displayValue}" />

<c:choose>
	<c:when test="${empty priceVal}">
		<c:set var="priceVal" value="0" />
	</c:when>
	<c:otherwise>
		<%-- To make rounding always go up instead of to nearest even digit 1E-8 is added to the amount.See DISTRELEC-8549 --%>
		<c:set var="priceVal" value="${priceVal + 1E-8}" />
	</c:otherwise>
</c:choose>

<c:set var="currencyVal" value="${not empty priceData.currencyIso ? priceData.currencyIso : fallBackCurrency}" />

<%--
 Tag to render a currency formatted price.
 Includes the currency symbol for the specific currency.
--%>
<c:choose>
	<c:when test="${not empty explicitMaxFractionDigits}">
		<c:set var="maxFractionDigits" value="${explicitMaxFractionDigits}" />
	</c:when>
	<c:when test="${(not empty priceData && priceData.value < 1 && namics:getScale(priceData.value) > 2) || (not empty displayValue && displayValue < 1 && namics:getScale(priceData.value) > 2)}">
		<c:set var="maxFractionDigits" value="6" />
	</c:when>
	<c:otherwise>
		<c:set var="maxFractionDigits" value="2" />
	</c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${format == 'price'}">
    	<c:choose>
    		<c:when test="${not empty priceData.currencyIso}">
		    	<c:set var="formattedValue">
		    		<fmt:formatNumber minFractionDigits="2" maxFractionDigits="${maxFractionDigits}" value="${priceVal}" />
		    	</c:set>
		    	<c:if test="${priceData.currencyIso eq 'CHF'}">
		    		<c:set var="formattedValue" value="${fn:replace(formattedValue, ',', '\\'')}" />
		    	</c:if>
		    	${formattedValue} 			
    		</c:when>
    		<c:otherwise>
    			<c:out value="${priceData.formattedValue}" escapeXml="false" />
    		</c:otherwise>
    	</c:choose>
    </c:when>
    <%-- Specific price format for SEO MicroFormat: DISTRELEC-5122 --%>
    <c:when test="${format == 'price-microformat'}">
    	<c:choose>
    		<c:when test="${not empty priceData.currencyIso}">
    			<fmt:formatNumber minFractionDigits="2" maxFractionDigits="${maxFractionDigits}" pattern="####.##" value="${priceVal}" />
    		</c:when>
    		<c:otherwise>
    			<c:out value="${priceData.formattedValue}" escapeXml="false" />
    		</c:otherwise>
    	</c:choose>
    </c:when>    
    <c:when test="${format == 'currency'}">
        ${currencyVal}
    </c:when>
    <c:when test="${format == 'defaultSplit'}">
        <span class="currency">${currencyVal}</span>&nbsp;
        
    	<c:set var="formattedValue">
    		<fmt:formatNumber minFractionDigits="2" maxFractionDigits="${maxFractionDigits}" value="${priceVal}" />
    	</c:set>
    	<c:if test="${priceData.currencyIso eq 'CHF'}">
    		<c:set var="formattedValue" value="${fn:replace(formattedValue, ',', '\\'')}" />
    	</c:if>
    	${formattedValue} 
        
    </c:when>
     <c:when test="${format == 'reverseSplit'}">
         <%--
           Print out the price then the currency value
         --%>
        <span class="currency">
            <c:set var="formattedValue">
                <fmt:formatNumber minFractionDigits="2" pattern="####.##" maxFractionDigits="${maxFractionDigits}" value="${priceVal}" />
            </c:set>
            <c:if test="${priceData.currencyIso eq 'CHF'}">
                <c:set var="formattedValue" value="${fn:replace(formattedValue, ',', '\\'')}" />
            </c:if>
        </span>
            ${formattedValue}&nbsp${currencyVal}
        </c:when>
    <c:when test="${format == 'simple'}">
    	<%--
    		Print out the currency value followed by a whitespace followed by the price value. 
    		No HTML formatting is used, it uses only plain text.
    	--%>
    	${currencyVal}
    	<c:out value=" "/>
    	
    	
    	<c:set var="formattedValue">
    		<fmt:formatNumber minFractionDigits="2" maxFractionDigits="${maxFractionDigits}" value="${priceVal}" />
    	</c:set>
    	<c:if test="${priceData.currencyIso eq 'CHF'}">
    		<c:set var="formattedValue" value="${fn:replace(formattedValue, ',', '\\'')}" />
    	</c:if>
    	${formattedValue} 
    	
    	
    </c:when>
    <c:when test="${format == 'defaultOnlyNumbers'}">
    	<c:set var="formattedValue">
    		<fmt:formatNumber minFractionDigits="2" maxFractionDigits="${maxFractionDigits}" value="${priceVal}" />
    	</c:set>
    	<c:if test="${priceData.currencyIso eq 'CHF'}">
    		<c:set var="formattedValue" value="${fn:replace(formattedValue, ',', '\\'')}" />
    	</c:if>
    	&nbsp;${formattedValue}    	
    </c:when>
	<c:when test="${format == 'priceWithVat'}">
		<c:set var="priceWithVat" value="${empty displayValue ? priceData.priceWithVat : displayValue}" />

		<c:choose>
			<c:when test="${empty priceWithVat}">
				<c:set var="priceWithVat" value="0" />
			</c:when>
			<c:otherwise>
				<%-- To make rounding always go up instead of to nearest even digit 1E-8 is added to the amount.See DISTRELEC-8549 --%>
				<c:set var="priceWithVat" value="${priceWithVat + 1E-8}" />
			</c:otherwise>
		</c:choose>


		<c:set var="priceWithVat" value="${fn:replace(priceWithVat, ',', '=')}" />
		${currencyVal}&nbsp;

		<c:set var="formattedValue">
			<fmt:formatNumber minFractionDigits="2" maxFractionDigits="${maxFractionDigits}" value="${priceWithVat}" />
		</c:set>
		<c:if test="${priceData.currencyIso eq 'CHF'}">
			<c:set var="formattedValue" value="${fn:replace(formattedValue, ',', '\\'')}" />
		</c:if>
		${formattedValue}
	</c:when>
	<c:when test="${format == 'basePrice'}">
		<c:set var="basePrice" value="${empty displayValue ? priceData.basePrice : displayValue}" />

		<c:choose>
			<c:when test="${empty basePrice}">
				<c:set var="basePrice" value="0" />
			</c:when>
			<c:otherwise>
				<%-- To make rounding always go up instead of to nearest even digit 1E-8 is added to the amount.See DISTRELEC-8549 --%>
				<c:set var="basePrice" value="${basePrice + 1E-8}" />
			</c:otherwise>
		</c:choose>


		<c:set var="basePrice" value="${fn:replace(basePrice, ',', '=')}" />
		${currencyVal}&nbsp;

		<c:set var="formattedValue">
			<fmt:formatNumber minFractionDigits="2" maxFractionDigits="${maxFractionDigits}" value="${basePrice}" />
		</c:set>
		<c:if test="${priceData.currencyIso eq 'CHF'}">
			<c:set var="formattedValue" value="${fn:replace(formattedValue, ',', '\\'')}" />
		</c:if>
		${formattedValue}
	</c:when>
	<c:otherwise>
		<c:set var="priceVal" value="${fn:replace(priceVal, ',', '=')}" /> 
        ${currencyVal}&nbsp;
        
    	<c:set var="formattedValue">
    		<fmt:formatNumber minFractionDigits="2" maxFractionDigits="${maxFractionDigits}" value="${priceVal}" />
    	</c:set>
    	<c:if test="${priceData.currencyIso eq 'CHF'}">
    		<c:set var="formattedValue" value="${fn:replace(formattedValue, ',', '\\'')}" />
    	</c:if>
    	${formattedValue}
	</c:otherwise>
</c:choose>
