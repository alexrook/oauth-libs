<%@page isErrorPage="true" contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Error:
            <c:out value="${requestScope['javax.servlet.error.status_code']}"/>
        </title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <%
            //http://docs.oracle.com/javaee/6/api/constant-values.html
        %>
        <c:if test="${requestScope['javax.servlet.error.status_code']==401}">

            <h1>Unauthorized</h1>
            <c:choose>

                <c:when test="${(requestScope['javax.servlet.error.request_uri'].contains('oauth'))||(requestScope['javax.servlet.error.request_uri'].contains('expbase'))}">
                    <p>
                        Access to this page requires 
                        <a href="<c:url  value='/#/google-oauth'/>">
                            authorization via Google OAuth
                        </a>
                    </p>
                </c:when>

                <c:otherwise>
                    <p>
                        Access to this page requires 
                        <a href="<c:url  value='/#/httpauth'/>">
                            valid username and password
                        </a>
                    </p>
                </c:otherwise>
            </c:choose>

        </c:if>

        <c:if test="${requestScope['javax.servlet.error.status_code']==400}">
            <h1>Sorry, bad request occurred</h1>
            <p>
                <c:out value="${requestScope['javax.servlet.error.exception'].message}"/>
            </p>
        </c:if>

    </body>
</html>
