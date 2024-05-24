<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>메인페이지</title>
</head>
<body>
<h1>main</h1>
<%
    Cookie[] cookies = request.getCookies();

    for(Cookie c : cookies){
        out.print("Cookie Name : "+c.getName()+"<br>");
        out.print("Cookie Value : "+c.getValue()+"<br>");
        out.print("<hr>");
    }
%>

<input type="button" value="Logout" onclick="location.href='/logout'"><br/><br/>
</body>
</html>
