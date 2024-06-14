<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="orderData" required="true"
	type="de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderData"%>
<%@ attribute name="orderHistoryEntries" required="true"
	type="java.util.List"%>
<%@ attribute name="isOrderDetailsPage" type="java.lang.Boolean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%--
	~ /*
	~  * [y] hybris Platform
	~  *
	~  * Copyright (c) 2000-2011 hybris AG
	~  * All rights reserved.
	~  *
	~  * This software is the confidential and proprietary information of hybris
	~  * ("Confidential Information"). You shall not disclose such Confidential
	~  * Information and shall use it only in accordance with the terms of the
	~  * license agreement you entered into with hybris.
	~  *
	~  */
--%>

<spring:url value="/my-account/quote/quoteOrderDecision"
	var="quoteOrderDecisionURL" />

<script type="text/javascript">
	$(document).ready(function() {
		$('#negotiate-quote-div').hide();
		bindToCancelQuoteButtonClick();
		bindToNegotiateQuoteButtonClick();
		bindToCancelClick();
		bindToAddAdditionalComment();
	});

	function displayNegotiateQuoteDiv() {
		$('#negotiate-quote-div').show();
		return false;
	}

	function bindToCancelQuoteButtonClick() {
		$('#cancelQuoteButton')
				.click(
						function() {
							displayNegotiateQuoteDiv();
							updateNogotiatiateQuoteDivLabel('<spring:theme code="checkout.summary.negotiateQuote.quotecancelreason"/>');
							$("#cancelQuoteButton").addClass('pressed');
							$("#negotiateQuoteButton").attr('disabled', true);
							$("#acceptQuoteButton").attr('disabled', true);
							$("#addAdditionalComment").attr('disabled', true);
							$('#selectedQuoteDecision').val(this.name);
							return false;
						});
	}

	function updateNogotiatiateQuoteDivLabel(label) {
		$('#negotiate-quote-div-label').html(label)
	}

	function bindToNegotiateQuoteButtonClick() {
		$('#negotiateQuoteButton')
				.click(
						function() {
							$("#negotiateQuoteButton").addClass('pressed');
							$("#cancelQuoteButton").attr('disabled', true);
							$("#acceptQuoteButton").attr('disabled', true);
							$("#addAdditionalComment").attr('disabled', true);
							$('#selectedQuoteDecision').val(this.name);
							displayNegotiateQuoteDiv();
							updateNogotiatiateQuoteDivLabel('<spring:theme code="checkout.summary.negotiateQuote.quoteReason"/>');

							return false;
						});
	}

	function submitQuoteDecision(selectedQuoteDecision) {
		$('#selectedQuoteDecision').attr("value", selectedQuoteDecision);
		document.getElementById("quoteOrderDecisionForm").submit();
	}

	function bindToCancelClick() {
		$('#cancelComment').click(function() {
			$('#negotiate-quote-div').hide();
			$("#cancelQuoteButton").removeClass('pressed');
			$("#negotiateQuoteButton").removeClass('pressed');
			$("#acceptQuoteButton").removeClass('pressed');

			$("#negotiateQuoteButton").removeAttr("disabled");
			$("#cancelQuoteButton").removeAttr("disabled");
			$("#acceptQuoteButton").removeAttr("disabled");
			$("#addAdditionalComment").removeAttr("disabled");
			$('#selectedQuoteDecision').val("");
			return false;
		});
	}

	function bindToAddAdditionalComment() {
		$('#addAdditionalComment')
				.click(
						function() {
							$("#addAdditionalComment").addClass('pressed');
							$("#negotiateQuoteButton").attr('disabled', true);
							$("#cancelQuoteButton").attr('disabled', true);
							$("#acceptQuoteButton").attr('disabled', true);
							$('#selectedQuoteDecision').val(this.name);
							displayNegotiateQuoteDiv();
							updateNogotiatiateQuoteDivLabel('<spring:theme code="checkout.summary.negotiateQuote.quoteReason"/>');
							return false;
						});
	}
</script>

<form:form method="post" id="quoteOrderDecisionForm"
	modelAttribute="quoteOrderDecisionForm" action="${quoteOrderDecisionURL}">
	<div class="item_container_holder">
		<div class="title_holder">
			<div class="title">
				<div class="title-top">
					<span></span>
				</div>
			</div>
			<h1>
				<spring:theme code="text.quotes.orderStatusDetails.label"
					text="Order Status Details" />
			</h1>
		</div>

		<div class="item_container">
			<div class="right top">
				<form:input type="hidden" name="orderCode" path="orderCode"
					value="${orderData.code}" />
				<form:input type="hidden" name="selectedQuoteDecision"
					path="selectedQuoteDecision" id="selectedQuoteDecision" />

				<c:if
					test="${orderData.status=='APPROVED_QUOTE' || orderData.status=='REJECTED_QUOTE'}">
					<button class="positive left pad_right place-order"
						name="NEGOTIATEQUOTE" id="negotiateQuoteButton">
						<spring:theme code="text.quotes.negotiateQuoteButton.displayName"
							text="Re-Quote" />
					</button>
				</c:if>
				<c:if test="${orderData.status eq 'APPROVED_QUOTE'}">
					<button class="positive left pad_right place-order"
						name="ACCEPTQUOTE" id="acceptQuoteButton"
						onClick="submitQuoteDecision('ACCEPTQUOTE');return false;">
						<spring:theme code="text.quotes.acceptQuoteButton.displayName"
							text="Accept Quote" />
					</button>

				</c:if>
				<c:if
					test="${orderData.status=='APPROVED_QUOTE' || orderData.status=='REJECTED_QUOTE' || orderData.status=='PENDING_QUOTE'}">
					<button class="positive right pad_right place-order"
						name="CANCELQUOTE" id="cancelQuoteButton">
						<spring:theme code="text.quotes.cancelQuoteButton.displayName"
							text="Cancel Quote" />
					</button>
				</c:if>

				<c:if test="${orderData.status=='PENDING_QUOTE'}">
					<button class="positive right pad_right place-order"
						name="ADDADDITIONALCOMMENT" id="addAdditionalComment">
						<spring:theme code="text.quotes.addAdditionalComment.displayName"
							text="Add Comment" />
					</button>
				</c:if>
				<br />

				<div class="span-10 right" id="negotiate-quote-div">
					<div>
						<div class="item_container_holder">
							<div class="title_holder">
								<div class="title">
									<div class="title-top">
										<span></span>
									</div>
								</div>
								<h2>
									<div id="negotiate-quote-div-label">
										<spring:theme
											code="checkout.summary.negotiateQuote.quoteReason" />
									</div>
								</h2>
							</div>
							<div class="item_container">
								<ul>
									<div class="your_cart">
										<form:input cssClass="text" id="comments" path="comments" />
									</div>
								</ul>
							</div>
						</div>
						<div class="item_container">
							<ul>
								<button class="positive right pad_right negotiateQuote"
									id="proceedButton">
									<spring:theme code="checkout.summary.negotiateQuote.proceed" />
								</button>
								<button class="positive right pad_right cancel"
									id="cancelComment">
									<spring:theme code="checkout.summary.negotiateQuote.cancel" />
								</button>
							</ul>
						</div>
					</div>
				</div>
			</div>

			<div class="span-60 left">
				<table class="table_budget">
					<tr>
						<td><spring:theme
								code="text.account.orderApprovalDetails.OrderNumber"
								text="Order number" />:</td>
						<td>${orderData.code}</td>
					</tr>
					<tr></tr>
					<tr>
						<td><spring:theme
								code="text.account.orderApprovalDetails.orderPlacedBy"
								text="Order placed by" />:</td>
						<td>${orderData.b2bCustomerData.name}</td>
					</tr>

					<tr>
						<td><spring:theme
								code="text.account.orderApprovalDetails.paidOntoAccount"
								text="Paid onto account" />:
						</td>
						<c:if test="${orderData.paymentType.code eq 'CARD'}">
							<td>${orderData.paymentInfo.cardNumber}</td>
						</c:if>
						<c:if test="${orderData.paymentType.code eq 'ACCOUNT'}">
							<td>${orderData.costCenter.code}</td>
						</c:if>
					</tr>

					<tr>
						<td><spring:theme
								code="text.account.orderApprovalDetails.purchaseOrderNumber"
								text="P.O.No" />:</td>
						<td>${orderData.purchaseOrderNumber}</td>
					</tr>


					<tr>
						<td><spring:theme
								code="text.account.orderApprovalDetails.parentBusinessUnit"
								text="Parent Business Unit" />:</td>
						<td>${orderData.costCenter.unit.uid}</td>
					</tr>


					<c:if test="${orderData.paymentType.code eq 'ACCOUNT'}">
						<tr>
							<td><spring:theme
									code="text.account.orderApprovalDetails.costCenter"
									text="Cost Center" /></td>
							<td>${orderData.costCenter.code}</td>
						</tr>
					</c:if>

					<tr>
						<td><spring:theme code="text.account.orderApproval.orderStatus"
							text="Order Status"/></td>
						<td>${orderData.statusDisplay}</td>
					</tr>
				</table>
			</div>


			<div class="span-20 left last">
				<div class="item_container">
					<table class="border">
						<thead>
							<tr>
								<th id="header1"><spring:theme
										code="text.quote.orderHistoryEntry.date" text="Date" /></th>

								<th id="header2"><spring:theme
										code="text.quote.orderHistoryEntry.status" text="Status" /></th>

								<th id="header3"><spring:theme
										code="text.quote.orderHistoryEntry.user" text="User" /></th>

								<th id="header4"><spring:theme
										code="text.quote.orderHistoryEntry.price" text="Cart Total" />
								</th>

								<th id="header5"><spring:theme
										code="text.quote.orderHistoryEntry.comment" text="Comment" />
								</th>
								<th id="header6"><spring:theme
										code="text.quote.orderHistoryEntry.quoteExpirationDate"
										text="Quote Expiration Date" /></th>

							</tr>
						</thead>
						<tbody>
							<c:forEach items="${orderHistoryEntries}"
								var="orderHistoryEntryData">
								<tr>
									<td headers="1">${orderHistoryEntryData.timeStamp}</td>
									<td headers="2">${orderHistoryEntryData.previousOrderVersionData.statusDisplay}</td>
									<td headers="3">${orderHistoryEntryData.ownerData.name}</td>
									<td headers="4">${orderHistoryEntryData.previousOrderVersionData.totalPrice.formattedValue}
									</td>
									<td headers="5">${orderHistoryEntryData.previousOrderVersionData.b2BComment.comment}
									</td>
									<td headers="6">${orderHistoryEntryData.previousOrderVersionData.quoteExpirationDate}
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	<br />

	<div class="span-20 left last">
		<div class="item_container_holder">
			<div class="title_holder">
				<div class="title">
					<div class="title-top">
						<span></span>
					</div>
				</div>
				<h1>
					<spring:theme code="text.quotes.comments.label"
						text="Quotes Comments Details" />
				</h1>
			</div>
			<div class="item_container">
				<table class="border" width="100%">
					<thead>
						<tr>
							<th id="header1" width="1px"><spring:theme
									code="text.quote.orderHistoryEntry.date" text="Date" /></th>

							<th id="header2" width="1px"><spring:theme
									code="text.quote.orderHistoryEntry.user" text="User" /></th>

							<th id="header3" width="5px"><spring:theme
									code="text.quote.orderHistoryEntry.comment" text="Comment" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${orderData.b2bCustomerDataList}"
							var="b2bComments">
							<tr>
								<td headers="1">${b2bComments.timeStamp}</td>
								<td headers="2">${b2bComments.ownerData.name}</td>
								<td headers="3">${b2bComments.comment}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>

</form:form>

