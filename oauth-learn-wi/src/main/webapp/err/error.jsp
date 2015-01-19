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
        <% //http://docs.oracle.com/javaee/6/api/constant-values.html %>
        <c:if test="${requestScope['javax.servlet.error.status_code']==401}">

            <h1>Unauthorized</h1>
            <p>
                Server response: <span><c:out 
                        value="${requestScope['javax.servlet.error.message']}"/></span>
            </p>
            <p>
                Access to this page requires 
                <a href="<c:url  value='/#/google-oauth'/>">
                    authorization via Google OAuth
                </a>
            </p>

        </c:if>

        <c:if test="${requestScope['javax.servlet.error.status_code']==400}">
            <h1>Sorry, bad request occurred</h1>
            <p>
                <c:out value="${requestScope['javax.servlet.error.exception'].message}"/>
            </p>
        </c:if>

        <c:if test="${requestScope['javax.servlet.error.status_code']==404}">
            <h1>Sorry, the requested resource is unknown</h1>
            <p>
                <c:out value="${requestScope['javax.servlet.error.exception'].message}"/>
            </p>
            <p> 
                <a href="<c:url  value='/'/>">
                    Go to start page
                </a>
            </p>
        </c:if>

    </body>
</html>
