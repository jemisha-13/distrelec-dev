<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
	<%-- colorbox CSS --%>
	<link rel="stylesheet" type="text/css" media="screen" href="<c:url value="/_ui/desktop/common/css/colorbox.css"/>"/>
	
	<%-- BeautyTips CSS --%>
	<link rel="stylesheet" type="text/css" media="screen" href="<c:url value="/_ui/desktop/common/css/jquery.bt.css"/>" />
	
	<%-- blueprintcss --%>
	<link rel="stylesheet" href="<c:url value="/_ui/desktop/blueprint/screen.css"/>" type="text/css" media="screen, projection" />

	<%-- jQuery UI CSS --%>
	<link rel="stylesheet" type="text/css" media="screen" href="<c:url value="/_ui/desktop/common/css/jquery.ui.stars.css"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="<c:url value="/_ui/desktop/common/css/jquery.ui.autocomplete-1.8.18.css"/>" />
	
	<%-- our site css --%>
	<link rel="stylesheet" type="text/css" media="screen" href="<c:url value="/_ui/desktop/common/css/common.css"/>" />
	
	<%-- theme specific css --%>
	<spring:theme code="css.changes" text="/" var="cssChangesPath"/>
	<spring:theme code="css.ie8" text="/" var="cssIE8Path"/>
	<spring:theme code="css.ie7" text="/" var="cssIE7Path"/>
	<link rel="stylesheet" type="text/css" media="screen" href="<c:url value="${cssChangesPath}"/>" />
	
	<%-- B2B css --%>
	<link rel="stylesheet" type="text/css" media="screen" href="<c:url value="/_ui/desktop/common/css/b2b.css"/>" />
	<%-- theme specific css --%>
	<spring:theme code="css.b2b.changes" text="/" var="cssB2BChangesPath"/>
	<link rel="stylesheet" type="text/css" media="screen" href="<c:url value="${cssB2BChangesPath}"/>" />
	
	<%-- treeview CSS --%>
	<link rel="stylesheet" type="text/css" media="screen" href="<c:url value="/_ui/desktop/common/css/jquery.treeview.css"/>"/>
