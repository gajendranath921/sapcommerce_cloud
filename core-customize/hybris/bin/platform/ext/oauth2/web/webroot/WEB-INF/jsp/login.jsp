<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Sign in &middot; hybris</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Le styles -->
    <c:url var="bootstrapCssUrl" value="/static/bootstrap/css/bootstrap.min.css"/>
    <link href="${fn:escapeXml(bootstrapCssUrl)}" rel="stylesheet">
    <c:url var="bootstrapResponsiveCssUrl" value="/static/bootstrap/css/bootstrap-responsive.min.css"/>
    <link href="${fn:escapeXml(bootstrapResponsiveCssUrl)}" rel="stylesheet">
    <style type="text/css">
        body {
            padding-top: 40px;
            padding-bottom: 40px;
            background-color: #f5f5f5;
        }

        .form-signin {
            max-width: 300px;
            padding: 19px 29px 29px;
            margin: 0 auto 20px;
            background-color: #fff;
            border: 1px solid #e5e5e5;
            -webkit-border-radius: 5px;
            -moz-border-radius: 5px;
            border-radius: 5px;
            -webkit-box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
            -moz-box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
            box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
        }

        .form-signin .form-signin-heading,
        .form-signin .checkbox {
            margin-bottom: 10px;
        }

        .form-signin input[type="text"],
        .form-signin input[type="password"] {
            font-size: 16px;
            height: auto;
            margin-bottom: 15px;
            padding: 7px 9px;
        }

    </style>
</head>

<body>

<div class="container">

    <c:url var="loginFormActionUrl" value="/login.do"/>
    <form id="loginForm" name="loginForm" class="form-signin" action="${fn:escapeXml(loginFormActionUrl)}" method="post">

        <c:if test="${not empty param.authentication_error}">
            <div class="alert alert-error">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
                <strong>Whoops!</strong> Your login attempt was not successful.
            </div>
        </c:if>

        <c:if test="${not empty param.authorization_error}">
            <div class="alert alert-error">
                <button type="button" class="close" data-dismiss="alert">&times;</button>
                <strong>Whoops!</strong> You are not permitted to access that resource.
            </div>
        </c:if>


        <h2 class="form-signin-heading">Please sign in</h2>
        <input type="text" name="username" class="input-block-level" autocomplete="off" placeholder="Email address">
        <input type="password" name="password" class="input-block-level" autocomplete="off" placeholder="Password">
        <input type="submit" class="btn btn-large btn-primary" value="Sign in"/>
    </form>

</div> <!-- /container -->

</body>
</html>
