<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="categories" required="true" type="java.util.List" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

    <%-- Required tag libraries --%>
    <%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <%-- Module template selection --%>
    <terrific:mod name="categorynav" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
        <%@ include file="/WEB-INF/terrific/modules/categorynav/categorynav.jsp" %>
    </terrific:mod>
