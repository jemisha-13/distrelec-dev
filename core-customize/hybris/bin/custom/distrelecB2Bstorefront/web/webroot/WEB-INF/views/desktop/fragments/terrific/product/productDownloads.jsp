<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<json:array name="productDownloads" items="${productDownloads}" var="productDownload">
	<json:object>
		<json:property name="title" value="${productDownload.title}" />
		<json:property name="code" value="${productDownload.code}" />
		<json:property name="description" value="${productDownload.description}" />

		<json:array name="downloads" items="${productDownload.downloads}" var="download">
			<json:object>
				<json:property name="downloadUrl" value="${download.downloadUrl}" />
				<json:property name="icon" value="${download.icon}" />
				<json:property name="mimeType" value="${download.mimeType}" />
				<json:property name="name" value="${download.name}" />
				<json:array name="languages" items="${download.languages}" var="language">
					<json:object>
						<json:property name="isocode" value="${language.isocode}" />
						<json:property name="isocodepim" value="${language.isocodePim}" />
						<json:property name="name" value="${language.name}" />
						<json:property name="nativeName" value="${language.nativeName}" />
					</json:object>
				</json:array>

			</json:object>
		</json:array>

        <json:array name="alternativeDownloads" items="${productDownload.alternativeDownloads}" var="alternativeDownload">
            <json:object>
                <json:property name="downloadUrl" value="${alternativeDownload.downloadUrl}" />
                <json:property name="icon" value="${alternativeDownload.icon}" />
                <json:property name="mimeType" value="${alternativeDownload.mimeType}" />
                <json:property name="name" value="${alternativeDownload.name}" />
                <json:array name="languages" items="${alternativeDownload.languages}" var="language">
                    <json:object>
                        <json:property name="isocode" value="${language.isocode}" />
                        <json:property name="isocodepim" value="${language.isocodePim}" />
                        <json:property name="name" value="${language.name}" />
                        <json:property name="nativeName" value="${language.nativeName}" />
                    </json:object>
                </json:array>
            </json:object>
        </json:array>

	</json:object>
</json:array>

