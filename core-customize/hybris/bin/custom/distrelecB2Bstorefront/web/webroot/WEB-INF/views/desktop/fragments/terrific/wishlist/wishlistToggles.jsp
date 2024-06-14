<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>

<json:object>
	<json:array name="products">
		<c:forEach var="productToggle" items="${toggles}">
			<json:object>
				<json:property name="productId" value="${productToggle.key}" />
				<json:array name="productToggles" var="toggle" items="${productToggle.value}">
					<json:property name="${toggle}" value="${toggle}" />					
				</json:array>
			</json:object>
		</c:forEach>
	</json:array>
</json:object>
