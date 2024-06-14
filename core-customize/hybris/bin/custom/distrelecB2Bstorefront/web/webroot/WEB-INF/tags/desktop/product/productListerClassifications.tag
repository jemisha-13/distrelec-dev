<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty product.classifications}">
	<ul class="product-classification">
		<c:forEach items="${product.classifications}" var="classification">
			<li>
				<strong>${classification.name}</strong>
				<ul>
					<c:forEach items="${classification.features}" var="feature">
						<li>
							<strong>${feature.name}:</strong>
							<span>
								<c:forEach items="${feature.featureValues}" var="value" varStatus="status">
									${value.value}
									<c:choose>
										<c:when test="${feature.range}">
											${not status.last ? '-' : feature.featureUnit.symbol}
										</c:when>
										<c:otherwise>
											${feature.featureUnit.symbol}
											${not status.last ? '<br/>' : ''}
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</span>
						</li>
					</c:forEach>
				</ul>
			</li>
		</c:forEach>
	</ul>
</c:if>
