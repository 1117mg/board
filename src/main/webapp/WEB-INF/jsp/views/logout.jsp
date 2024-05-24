<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Logout</title>
</head>
<body>
<%
    Cookie cookie = new Cookie("idx",null);
    cookie.setMaxAge(0);
    response.addCookie(cookie);
%>
<script>
    if (!alert('로그아웃 되었습니다')) document.location = 'main.jsp'
</script>
</body>
</html>