<%@ page contentType="text/html" language="java" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
    <head>
        <title>${pageTitle}</title>
    </head>
    <body>
        <h1>${pageTitle}</h1>
        <hr size="1">
        <table width="100%">
            <tbody>
                <tr>
                    <td align="left">Filename</td>
                    <td align="center">Size</td>
                    <td align="right">Last Modified</td>
                </tr>
                <c:forEach items="${exportMedias}" var="exportMedia">
                    <tr>
                        <td align="left">
                            <a href="${exportMedia.location}">
                                <tt>${exportMedia.location}</tt>
                            </a>
                        </td>
                        <td align="right">${exportMedia.size}</td>
                        <td align="right">${exportMedia.lastModified}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </body>
</html>
