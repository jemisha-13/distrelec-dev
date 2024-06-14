<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="orderHistoryEntries" required="true"
	type="java.util.List"%>
<%@ attribute name="itemHolderTitleKey" required="true"
	type="java.lang.String"%>
<%@ attribute name="isQuoteNegotiation" type="java.lang.Boolean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>

<div class="item_container_holder">
	<div class="title_holder">
		<div class="title">
			<div class="title-top">
				<span></span>
			</div>
		</div>
		<h1>
			<spring:theme code="${itemHolderTitleKey}" text="Approval Details" />
		</h1>
	</div>

	<div class="item_container">
		<c:choose>
			<c:when test="${not empty orderHistoryEntries}">
				<table>
					<thead>
						<tr>
							<th><spring:theme code="text.account.orderHistoryEntry.date"
									text="Date" /></th>
							<th><spring:theme code="text.account.orderHistoryEntry.user"
									text="User" /></th>
							<th><spring:theme
									code="text.account.orderHistoryEntry.email" text="Email" /></th>
							<th><spring:theme
									code="text.account.orderHistoryEntry.comment" text="Comment" />
							</th>
							<th><spring:theme
									code="text.account.orderHistoryEntry.status" text="Status" />
							</th>
							<c:if test="${isQuoteNegotiation eq true}">
								<th><spring:theme
										code="text.account.orderHistoryEntry.Quote.value" text="Value" />
								</th>
							</c:if>
						</tr>
					</thead>

					<tbody>
						<c:forEach items="${orderHistoryEntries}"
							var="orderHistoryEntryData">
							<tr>
								<td>${orderHistoryEntryData.timeStamp}</td>
								<td>${orderHistoryEntryData.ownerData.name}</td>
								<td>${orderHistoryEntryData.ownerData.uid}</td>
								<td>
									<c:choose>
										<c:when test="${empty orderHistoryEntryData.description}">
											<spring:theme code="text.notAvailable" text="NA" />	
										</c:when>
										<c:otherwise>
											${orderHistoryEntryData.description}
										</c:otherwise>
									</c:choose>		
								</td>
								<td width="20%">${orderHistoryEntryData.previousOrderVersionData.statusDisplay}</td>
								<c:if test="${isQuoteNegotiation eq true}">
									<td width="10%">${orderHistoryEntryData.previousOrderVersionData.totalPrice.formattedValue}</td>
								</c:if>
							</tr>
						</c:forEach>
					</tbody>

				</table>
			</c:when>
			<c:otherwise>
				  		<spring:theme code="text.company.noentries" text="No Entries." />	
			</c:otherwise>
		</c:choose>
	</div>
</div>
