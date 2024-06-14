<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="errorNoResults" required="true" type="java.lang.String" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>

<div class="item_container_holder">
	<div class="title_holder">
		<div class="title">
			<div class="title-top">
				<span></span>
			</div>
		</div>
		<h2><spring:theme code="storeFinder.find.a.store" /></h2>
	</div>
	<div class="item_container">
		<p><spring:theme code="storeFinder.use.this.form" /></p>
		<c:if test="${not empty errorNoResults}">
			<div class="form_field_error">
				<c:url value="/store-finder" var="storeFinderFormAction" />
				<form name="storefinder_form" method="get" action="${storeFinderFormAction}">
					<dl>
						<dt><label for="PostCode"><spring:theme code="storeFinder.postcode.town" /></label></dt>
						<dd>
							<ycommerce:testId code="storeFinder_search_box">
								<input type="text" class="text" id="PostCode" name="q" />
								<button class="form search">
									<span class="search-icon">
										<spring:theme code="storeFinder.search" />
									</span>
								</button>
							</ycommerce:testId>
						</dd>
					</dl>
				</form>
			<spring:theme code="storelocator.error.no.results.subtitle" />
			</div>
		</c:if>
		<c:if test="${empty errorNoResults}">
			<form name="storefinder_form" method="get" action="${storeFinderFormAction}">
				<dl>
					<dt><label for="PostCode"><spring:theme code="storeFinder.postcode.town" /></label></dt>
					<dd>
						<ycommerce:testId code="storeFinder_search_box">
							<input type="text" class="text" id="PostCode" name="q" />
							<button class="form search">
								<span class="search-icon">
									<spring:theme code="storeFinder.search" />
								</span>
							</button>
						</ycommerce:testId>
					</dd>
				</dl>
			</form>
		</c:if>
	</div>
</div>
