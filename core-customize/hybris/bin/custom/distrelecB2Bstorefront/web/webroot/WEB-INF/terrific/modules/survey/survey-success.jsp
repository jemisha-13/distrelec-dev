<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<div class="border-top border-bottom">
	<div class="inner row padding-gu">
		<div class="gu-12 register-option base">
			<br/>
			<div class="intro small">
				
				<p class="required">${survey.thankYouText}</p>
				
				<br/><br/>
							
				<a href="${not empty survey.successButtonAction ? survey.successButtonAction : '/'}" style="text-decoration:none">
					<button class="btn btn-primary" style="max-width: 200px;" type="button"><i></i> 					
						${not empty survey.successButtonText ? survey.successButtonText : 'Back to Home Page'}
					</button>
				</a>
				<br/>
				<br/>
				<br/>		
				<br/>		
			</div>
		</div>
	</div>
</div>
