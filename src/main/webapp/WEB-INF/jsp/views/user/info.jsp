<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>사용자 정보</title>
</head>
<body>
<h1>사용자 정보</h1>
<table>
    <tr>
        <td>아이디:</td>
        <td>${user.userId}</td>
    </tr>
    <tr>
        <td>비밀번호:</td>
        <td>${user.password}</td>
    </tr>
    <tr>
        <td>이름:</td>
        <td>${user.username}</td>
    </tr>
    <tr>
        <td>가입일:</td>
        <td>${user.regdate}</td>
    </tr>

    <button type="button" onclick="history.back()">뒤로가기</button>
</table>
</body>
</html>
