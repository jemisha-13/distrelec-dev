<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>

<spring:theme code="bomdataimport.fileSizeDeniedMessage" var="sFileSizeDeniedMessage" />
<spring:theme code="bomdataimport.fileSizeMessage" var="sFileSizeMessage" />
<spring:theme code="bomdataimport.fileTypeDeniedMessage" var="sFileTypeDeniedMessage" />
<spring:theme code="bomdataimport.fileTypeMessage" var="sFileTypeMessage" />
<spring:theme code="import-tool.upload.textarea" var="sPlaceholder" />
<spring:theme code="bomdataimport.descriptionExample" var="sDescriptionExample" />
<spring:theme code="bomdataimport.copypastedescription"  var="sDescription"/>
<spring:theme code="bomdataimport.importmessage"  var="sImportMessage"/>
<spring:theme code="bomdataimport.introduction"  var="sIntroductionText"/>
<spring:theme code="bomdataimport.title" var="sImportToolSectionOneTitle" />
<spring:theme code="bomdataimport.fileimport" var="sImportToolSectionTwoTitle" />
<spring:theme code="bomdataimport.introduction" var="sUploadHelpTextOne" />
<spring:theme code="bomdataimport.downloaddescription" var="sUploadHelpTextTwo" />
<spring:theme code="bomdataimport.downloadtemplate" var="sDownloadTemplate" />

<a href="#bomneedhelp" class="bom-need-help bom-need-help--link"> <spring:theme code="text.needhelp" /> </a>


<div class="row row-holder">
	<div class="col-12 col-md-6 row-holder__item">
		<div class="form-box form-textimport">
			<form:form action="/bom-tool/review?${_csrf.parameterName}=${_csrf.token}" id="upload-form-text" modelAttribute="uploadForm" enctype="multipart/form-data" method="post">
				<form:errors path="*" />${errors}
				<div class="content">
					<h3 class="head">${sImportToolSectionOneTitle}</h3>
					<div class="content__item">
						<p>${sDescription}</p>
					</div>
					<div class="content__item">
						<form:textarea path="data" data-sample="${sPlaceholder}" />
					</div>
				</div>
				<div class="btn-holder">
					<button type="submit" class="mat-button mat-button__solid--action-green btn-text-contintue btn-continue"><spring:theme code="bomdataimport.continue" /><i class="fa fa-chevron-right" aria-hidden="true"></i></button>
				</div>
			</form:form>
		</div>
	</div>
	<div class="row-holder__item-partition">
		<span>or</span>
	</div>
	<div class="col-12 col-md-6  row-holder__item">
		<div class="form-box form-fileimport">
			<form:form action="/bom-tool/matching?${_csrf.parameterName}=${_csrf.token}" id="upload-form-text" modelAttribute="uploadForm" enctype="multipart/form-data" method="post">
				<div class="content">
					<h3 class="head">${sImportToolSectionTwoTitle}</h3>
					
					<c:set var="bomTemplateDownlodUrl" value="/_ui/all/data/DistrelecImportFileTemplate-${currentLanguage.isocode}.xls" />

					<div class="content__item content__item--${currentCountry.isocode}">
						<p>${sUploadHelpTextOne}</p>
						<a href="${bomTemplateDownlodUrl}" class="download-example boxy">
							<i class="fa fa-download" aria-hidden="true"></i><span class="right">${sDownloadTemplate}</span>
						</a>
					</div>
					<div class="content__item">
						<form:input type="file" path="file"
									data-file-types="xls,xlsx,csv,txt"
									data-max-file-size="1048576"
									data-file-size-denied-message="${sFileSizeDeniedMessage}"
									data-file-size-message="${sFileSizeMessage}"
									data-file-type-denied-message="${sFileTypeDeniedMessage}"
									data-file-type-message="${sFileTypeMessage}"
						/>

						<div class="advanced-upload">

							<a href="#" class="upload-file boxy">
								<div class="upload-file__item">

									<div class="browser-other">
										<span class="icon">
											<i class="fas fa-cloud-upload-alt" aria-hidden="true"></i>
										</span>
										<span class="text">
											<spring:theme code="bomdataimport.uploaddescription" />
										</span>


									</div>

									<div class="browser-ie mat-button mat-button__solid--action-blue" href="/welcome">
											<i class="fas fa-cloud-upload-alt" aria-hidden="true"></i>
											<spring:theme code="bomdataimport.uploaddescription" />
									</div>

									<span class="filename">sampledata.xlsx</span>

								</div>

							</a>
						</div>
						<div class="errors">
							<p></p>
						</div>
					</div>
				</div>
				<div class="btn-holder">
					<button type="submit" class="mat-button mat-button__solid--action-green btn-continue"><spring:theme code="bomdataimport.continue" /><i class="fa fa-chevron-right" aria-hidden="true"></i></button>
				</div>
			</form:form>
		</div>
	</div>
</div>

<div id="bomneedhelp" class="bom-need-help bom-need-help--info-content">
	<div class="bom-need-help bom-need-help--info">
		<cms:slot var="component" position="BomHelp">
			<cms:component component="${component}" evaluateRestriction="true" />
		</cms:slot>
	</div>
</div>
