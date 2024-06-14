<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

Disallow: <c:url value="*?q=*&filter" />
Disallow: <c:url value="*&filterURL=*" />
Disallow: <c:url value="*/search" />
Disallow: <c:url value="*/cart" />
Disallow: <c:url value="*/checkout" />
Disallow: <c:url value="*/my-account" />
Disallow: <c:url value="*/login" />
Disallow: <c:url value="*//app/etc/local.xml" />
Disallow: <c:url value="*/medias/sys_master/*.gz" />
Disallow: <c:url value="*/availability*" />
Disallow: <c:url value="*/bom-tool-upload*" />
Disallow: <c:url value="*/bom-tool*" />
Disallow: <c:url value="*undefined*" />
Disallow: <c:url value="*/special-shops/*" />
Disallow: <c:url value="*/shop-in-shop/*" />
Disallow: <c:url value="*/Web/Downloads/*" />
Disallow: <c:url value="*/shopping/*" />
Disallow: <c:url value="/compliance-document/*" context="/" />
Disallow: <c:url value="*/rs-welcome" />
Disallow: <c:url value="*/rs-registration" />
