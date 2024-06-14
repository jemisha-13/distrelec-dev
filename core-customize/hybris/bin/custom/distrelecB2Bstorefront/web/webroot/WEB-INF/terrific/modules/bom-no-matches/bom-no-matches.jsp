<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:theme code="bomnomatches.mainTitle" var="sMainTitle" />
<spring:theme code="bomnomatches.title" var="sSubTitle" arguments="${fn:length(notMatchingProducts)}"/>
<spring:message code="product.articleNumber" var="sArticleNumber" />
<spring:theme code="bomnomatches.quantity" var="sQuantity" />
<spring:theme code="bomnomatches.reference" var="sReference" />
<spring:theme code="bomnomatches.manufacturer" var="sManufacturer" />

<c:if test="${not empty notMatchingProducts}">
	<div class="no-match-holder">
		<div class="head">
			<h2 class="base">
					${sMainTitle}
			</h2>
			<p>${sSubTitle}</p>
		</div>
		<div id="notMatchingProducts" class="table">
			<div class="table-header">
				<div class="table-header__item">
						${sArticleNumber}
				</div>
				<div class="table-header__item">
						${sQuantity}
				</div>
				<div class="table-header__item">
						${sReference}
				</div>
				<div class="table-header__item manu">
						${sManufacturer}
				</div>
			</div>
			<div class="table-body">
				<c:forEach items="${notMatchingProducts}" var="notMatchingProduct">
					<div class="mobile-header">
						<div class="mobile-header__item">
								${sArticleNumber}
						</div>
						<div class="mobile-header__item">
								${sQuantity}
						</div>
						<div class="mobile-header__item">
								${sReference}
						</div>
						<div class="mobile-header__item manu">
								${sManufacturer}
						</div>
					</div>
					<div class="item-holder">
						<div class="table-body__item">
								${notMatchingProduct[1]}
						</div>
						<div class="table-body__item">
								${notMatchingProduct[0]}
						</div>
						<div class="table-body__item">
								${notMatchingProduct[2]}
						</div>
						<div class="table-body__item">
								${notMatchingProduct[3]}
						</div>
					</div>
				</c:forEach>
			</div>
		</div>
	</div>
</c:if>