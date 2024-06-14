<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>

<json:object>
	<json:object name="manufacturerData">
		<json:array name="manufacturerlist" items="${manufacturers}"
			var="manufacturersEntry">
			<json:object>
			<json:property name="key" value="${manufacturersEntry.key}" />
			<json:array name="manuCat"
					items="${manufacturersEntry.manufacturerList}" var="manufacturersEntry1">
					<json:object>
						<json:property name="name" value="${manufacturersEntry1.name}" />
						<json:property name="nameEN" value="${manufacturersEntry1.nameEN}" />
						<json:property name="nameSeo"
							value="${manufacturersEntry1.nameSeo}" />
						<json:property name="code" value="${manufacturersEntry1.code}" />
						<json:property name="urlId" value="${manufacturersEntry1.urlId}" />
					</json:object>
				</json:array>
			</json:object>
		</json:array>
	</json:object>
</json:object>