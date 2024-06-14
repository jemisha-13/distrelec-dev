<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>

<json:object>
	<json:property name="exist" value="${exist}" />
	<json:property name="vat-id" value="${vat_id}" />
	<json:property name="erp-cid" value="${erp_cid}" />
</json:object>
