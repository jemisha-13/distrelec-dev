<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/form" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:url value="/${product.name}/p/${product.code}/reviewhtml/3" var="show3ReviewsActionlink" />
<c:url value="/${product.name}/p/${product.code}/reviewhtml/all" var="showAllReviewsActionlink" />
<script type="text/javascript">
/*<![CDATA[*/
	var showReviewForm = <c:out value="${showReviewForm}" default="false"/>;
	var allReviews = "all";
/*]]>*/
</script>

<script type="text/javascript">
/*<![CDATA[*/

	function showReviewsAction() {
		$.get('${show3ReviewsActionlink}', function(result){
			$('#reviews').html(result);
			$('#show_all_reviews_action').click(function(e){
				e.preventDefault();
				$.get('${showAllReviewsActionlink}', function(result){
					$('#reviews').html(result);
					$('#show_all_reviews_action').hide();
					$('#write_review_action').click(function(e){
						e.preventDefault();
						$('#reviews').hide();
						$('#write_reviews').show();
					});
				});
			});
			$('#write_review_action').click(function(e){
				e.preventDefault();
				$('#reviews').hide();
				$('#write_reviews').show();
				$('#reviewForm input[name=headline]').focus();
			});
		});
	}

	$(document).ready(function() {
		$('#read_reviews_action').click(function(e){
			e.preventDefault();
			showReviewsAction();
			$('#reviews').show();
			$('#write_reviews').hide();
		});

		$(".tab_03").click(function() {
			showReviewsAction();
		});

		$('#write_review_action').click(function(e){
			e.preventDefault();
			$('#reviews').hide();
			$('#write_reviews').show();
			$('#reviewForm input[name=headline]').focus();
		});
		if(showReviewForm) {
			$('#reviews').hide();
			$('#write_reviews').show();
		}
		$("#stars-wrapper").stars({
			inputType: "select"
		});
	});
/*]]>*/
</script>

<div id="reviews"></div>

<div id="write_reviews" style="display:none">
	<ul class="review_actions">
		<li><a href="#" id="read_reviews_action"><spring:theme code="review.back"/></a></li>
	</ul>
	<div class="write_review">
		<h3><spring:theme code="review.write.title"/></h3>
		<p><spring:theme code="review.write.description"/></p>
		<p><spring:theme code="review.required"/></p>
		<c:url value="/${product.name}/p/${product.code}/review" var="productReviewActionUrl"/>
		<form:form method="post" action="${productReviewActionUrl}" modelAttribute="reviewForm">
			<dl>
				<formUtil:formInputBox idKey="review.headline" labelKey="review.headline" path="headline" inputCSS="text" placeHolderKey="review.headline.placeholder" mandatory="true"/>
				<formUtil:formTextArea idKey="review.comment" labelKey="review.comment" path="comment" placeHolderKey="review.comment.placeholder"  areaCSS="textarea" mandatory="true"/>
				<spring:bind path="rating">
					<c:if test="${not empty status.errorMessages}">
						<span class="form_field_error">
					</c:if>
					<dt><span><spring:theme code="review.rating"/>*:</span></dt>
					<dd></dd>
						<div id="stars-wrapper">
							<form:select path="rating" >
								<form:option value='1'>1/5</form:option>
								<form:option value='2'>2/5</form:option>
								<form:option value='3'>3/5</form:option>
								<form:option value='4'>4/5</form:option>
								<form:option value='5'>5/5</form:option>
							</form:select>
						</div>
						<p><form:errors path="rating" /></p>
					<c:if test="${not empty status.errorMessages}">
						</span>
					</c:if>
					</spring:bind>
					<dt><label for="alias"><spring:theme code="review.alias"/>:</label></dt>
				<dd><form:input path="alias" /></dd>
			</dl>
			<div style="clear:both;"></div>
			<span style="display: block; clear: both;">
				<button class="form" type="submit" value="<spring:theme code="review.submit"/>"><spring:theme code="review.submit"/></button>
			</span>
		</form:form>
	</div>
</div>