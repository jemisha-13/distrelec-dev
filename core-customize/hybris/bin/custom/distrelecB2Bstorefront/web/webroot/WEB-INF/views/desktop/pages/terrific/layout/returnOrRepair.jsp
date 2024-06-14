<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<spring:theme code="rma.guest.returnPage.subtitleOne" var="sSubtitleOne" />
<spring:theme code="rma.guest.returnPage.textOne" var="sIntroductionText" />
<spring:theme code="rma.guest.returnPage.bulletOne" var="sSectionOneBulletOne" />
<spring:theme code="rma.guest.returnPage.bulletTwo" var="sSectionOneBulletTwo" />
<spring:theme code="rma.guest.returnPage.subtitleTwo" var="sSubtitleTwo" />
<spring:theme code="rma.guest.returnPage.bulletOneSection" var="sSectionTwoBulletOne" />
<spring:theme code="rma.guest.returnPage.bulletTwoSection" var="sSectionTwoBulletTwo" />
<spring:theme code="rma.guest.returnPage.bulletThreeSection" var="sSectionTwoBulletThree" />
<spring:theme code="rma.guest.returnPage.bulletFourSection" var="sSectionTwoBulletFour" />
<spring:theme code="rma.guest.returnPage.formTitle" var="sFormTitle" />
<spring:theme code="rma.guest.returnPage.formParagraph" var="sFormParagraph" />
<spring:theme code="rma.guest.returnPage.formName" var="sFormName" />
<spring:theme code="rma.guest.returnPage.formEmail" var="sFormEmail" />
<spring:theme code="rma.guest.returnPage.formArticle" var="sFormArticle" />
<spring:theme code="rma.guest.returnPage.formMoreInformation" var="sFormMoreInformation" />
<spring:theme code="rma.guest.returnPage.formPhone" var="sFormPhone" />
<spring:theme code="rma.guest.returnPage.formQuantity" var="sFormQty" />
<spring:theme code="rma.guest.returnPage.formReason" var="sFormReason" />
<spring:theme code="rma.guest.returnPage.submitButton" var="sSubmitBtn" />

<views:page-default-md pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-content-with-navigation skin-layout-wide skin-layout-return-or-repair" >

	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb/>
	</div>

	<mod:global-messages/>

	<div class="md-content__holder">
		<div class="row">
			<div class="col-12 col-lg-8">
				<section class="content-hero">
					<cms:slot var="feature" contentSlot="${slots.Content}">
						<cms:component component="${feature}" />
					</cms:slot>
				</section>
				<section class="content-one">
					<h2>${sSubtitleOne}</h2>
					<p>${sIntroductionText}</p>

					<ul class="list-items">
						<li>${sSectionOneBulletOne}</li>
						<li>${sSectionOneBulletTwo}</li>
					</ul>
				</section>
				<section class="content-two">
					<h2>${sSubtitleTwo}</h2>
					<ul class="list-items">
						<li>${sSectionTwoBulletOne}</li>
						<li>${sSectionTwoBulletTwo}</li>
						<li>${sSectionTwoBulletThree}</li>
						<li>${sSectionTwoBulletFour}</li>
					</ul>
				</section>
				<section class="content-three">
					<h2>${sFormTitle}</h2>
					<p>${sFormParagraph}</p>
					<form:form method="post">
						<div class="input-holder">
							<label for="name">${sFormName} *</label>
							<input id="name" type="text" value=""/>
						</div>
						<div class="input-holder">
							<label for="email">${sFormEmail} *</label>
							<input id="email" type="text" value=""/>
						</div>
						<div class="input-holder">
							<label for="phone">${sFormPhone}</label>
							<input id="phone" type="text" value=""/>
						</div>
						<div class="input-holder">
							<label for="article">${sFormArticle} *</label>
							<input id="article" type="text" value=""/>
						</div>
						<div class="input-holder">
							<label for="quantity">${sFormQty} *</label>
							<input id="quantity" type="text" value=""/>
						</div>
						<div class="input-holder">
							<label for="reasons">${sFormReason} *</label>
							<input id="reasons" type="text" value=""/>
						</div>
						<div class="input-holder">
							<label for="moreinformation">${sFormMoreInformation} *</label>
							<textarea rows="3" cols="28" id="moreinformation"></textarea>
						</div>
						<div class="btn-holder">
							<button class="btn btn-primary btn-return-items">${sSubmitBtn}</button>
						</div>
					</form:form>
				</section>
			</div>
			<div class="col-12 col-lg-4">
				<mod:nav-content />
			</div>
		</div>
	</div>



</views:page-default-md>