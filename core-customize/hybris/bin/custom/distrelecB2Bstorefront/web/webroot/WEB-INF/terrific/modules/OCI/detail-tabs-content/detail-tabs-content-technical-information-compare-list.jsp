<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="maxVisibleOtherAttr" value="10" />

<%-- COMMON PRODUCT ATTRIBUTES --%>


<h3 class="base"><spring:message code="compare.item.attributes.common" /></h3>

<table class="feature b-valign-attr">
	<tbody>
		<c:if test="${empty product.commonAttributes}">
			<tr>
				<td class="td1" valign="top" colspan="2">
					<span class="notfound"><spring:message code="compare.item.noCommonAttributes" /></span>
				</td>
			</tr>
		</c:if>
		<c:forEach items="${product.commonAttributes}" var="feature">
			<tr class="feature-${position}">
				<td class="td1" valign="top">
					<span class="feature constrain">${feature.name}</span>
				</td>
				<td class="td2" valign="top">
					<c:choose>
						<c:when test="${feature.featureValuesWithUnit != null}">
							<c:forEach items="${feature.featureValuesWithUnit}" var="entry" varStatus="status">
								<span class="feature-value constrain">
									${entry.key.value}&nbsp;
									<c:choose>
										<c:when test="${feature.range}">
											${not status.last ? '-' : entry.value.symbol}
										</c:when>
										<c:otherwise>
											${entry.value.symbol}
											${not status.last ? '<br/>' : ''}
										</c:otherwise>
									</c:choose>
								</span>
							</c:forEach>
						</c:when>
						<c:otherwise>
							-
						</c:otherwise>
					</c:choose>

				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>


<%-- OTHER PRODUCT ATTRIBUTES --%>
<h3 class="base<c:if test="${not empty product.commonAttributes}"> border-top</c:if>"><spring:message code="compare.item.attributes.other" /></h3>
<table class="feature">
	<tbody>
		<c:if test="${empty product.otherAttributes}">
			<tr>
				<td class="td1" valign="top" colspan="2">
					<span class="notfound"><spring:message code="compare.item.noOtherAttributes" /></span>
				</td>
			</tr>
		</c:if>
		<c:forEach items="${product.otherAttributes}" var="feature" varStatus="attrStatus">
			<tr class="${attrStatus.count > maxVisibleOtherAttr ? 'row-hidden' : ''}">
				<td class="td1" valign="top">
					<span class="feature constrain">${feature.name}</span>
				</td>
				<td class="td2" valign="top">
					<c:forEach items="${feature.featureValues}" var="value" varStatus="status" >
						<span class="feature-value constrain">
							${value.value}&nbsp;
							<c:choose>
								<c:when test="${feature.range}">
									${not status.last ? '-' : feature.featureUnit.symbol}
								</c:when>
								<c:otherwise>
									${feature.featureUnit.symbol}
									${not status.last ? '<br/>' : ''}
								</c:otherwise>
							</c:choose>
						</span>
					</c:forEach>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<c:if test="${fn:length(product.otherAttributes) > maxVisibleOtherAttr}">
	<div class="row show-more"><a class="show-more-link btn-show-more" href="#"><span><spring:message code="product.tabs.show.more" /></span></a></div>
</c:if>
