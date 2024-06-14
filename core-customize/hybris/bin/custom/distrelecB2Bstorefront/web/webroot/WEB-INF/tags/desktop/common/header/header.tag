<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="header" tagdir="/WEB-INF/tags/desktop/common/header"  %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<div id="header">
	<div class="siteLogo">
		<cms:slot var="logo" contentSlot="${slots.Logo}">
			<cms:component component="${logo}"/>
		</cms:slot>
	</div>
	
	
	<cms:slot var="cart" contentSlot="${slots.MiniCart}">
		<cms:component component="${cart}"/>
	</cms:slot>
	
	<div class="headerContent">
		<ul class="nav">
			<sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')">
				<li class="logged_in"><ycommerce:testId code="header_LoggedUser"><spring:theme code="header.welcome" arguments="${user.firstName},${user.lastName}" htmlEscape="true"/></ycommerce:testId></li>
			</sec:authorize>
			<sec:authorize access="!hasRole('ROLE_CUSTOMERGROUP')">
				<li><ycommerce:testId code="header_Login_link"><a href="<c:url value='/login'/>"><spring:theme code="header.link.login"/></a></ycommerce:testId></li>
			</sec:authorize>
			<sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')">
				<li><ycommerce:testId code="header_myAccount"><a href="<c:url value='/my-account'/>"><spring:theme code="header.link.account"/></a></ycommerce:testId></li>
			</sec:authorize>
			<sec:authorize access="hasRole('ROLE_B2BADMINGROUP')">
				<li><ycommerce:testId code="header_myCompany"><a href="<c:url value='/my-company/organization-management'/>"><spring:theme code="header.link.company"/></a></ycommerce:testId></li>
			</sec:authorize>
			
			
			<sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')"><li><ycommerce:testId code="header_signOut"><a href="<c:url value='/logout'/>"><spring:theme code="header.link.logout"/></a></ycommerce:testId></li></sec:authorize>
			<li><a href="<c:url value="/store-finder"/>"><spring:theme code="general.find.a.store" /></a></li>
		</ul>
		<ul class="language">
			<cms:slot var="link" contentSlot="${slots.ServiceNav}">
				<li><cms:component component="${link}"/></li>
			</cms:slot>
			<li class="language-select"><header:languageSelector languages="${languages}" currentLanguage="${currentLanguage}" /></li>
			<li class="language-currency"><header:currencySelector currencies="${currencies}" currentCurrency="${currentCurrency}" /></li>
		</ul>
		<div class="search">
			<form name="search_form" method="get" action="<c:url value="/search"/>">
				<spring:theme code="text.search" var="searchText"/>
				<label class="skip" for="search">${searchText}</label>
				<spring:theme code="search.placeholder" var="searchPlaceholder"/>
				<ycommerce:testId code="header_search_input">
					<input id="search" class="text" type="text" name="text" value="" maxlength="100" placeholder="${searchPlaceholder}"/>
				</ycommerce:testId>
				<ycommerce:testId code="header_search_button">
					<spring:theme code="img.searchButton" text="/" var="searchButtonPath"/>
					<input class="button" type="image" value="${searchText}" src="<c:url value="${searchButtonPath}"/>"  alt="${searchText}"/>
				</ycommerce:testId>
			</form>
		</div>
	</div>
	<div class="clear"></div>
</div>

<script>
	<c:if test="${request.secure}">
		<c:url value="/search/autocompleteSecure" var="autocompleteUrl"/>
	</c:if>
	<c:if test="${not request.secure}">
		<c:url value="/search/autocomplete" var="autocompleteUrl"/>
	</c:if>

	$(function() {
		$( "#search" ).autocomplete({
			source: function( request, response ) {
				$.getJSON(
						"${autocompleteUrl}", 
						{
							term : $('#search').val()
						},
						function(data) {
							response(data);
						}
					);
			},
			minLength: 2,
			open: function(event, ui) { $(".ui-menu").css("z-index", 10000); },
			close: function(event, ui) { $(".ui-menu").css("z-index", -1); },
			select: function(event, ui) { 
				if(ui.item) {
					$('#search').val(ui.item.value.trim());
				}
				document.forms['search_form'].submit();
			},
			autoFocus: false
		});
	});
</script>
