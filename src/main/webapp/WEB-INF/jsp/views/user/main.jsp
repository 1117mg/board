<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>사용자 목록</title>
</head>
<body>
<h1>사용자 목록</h1>
<table border="1">
  <thead>
  <tr>
    <th>유저번호</th>
    <th>아이디</th>
    <th>이름</th>
    <th>가입일</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach items="${users}" var="user">
    <tr>
      <td>${user.idx}</td>
      <td><a href="info/${user.userId}">${user.userId}</a></td>
      <td>${user.username}</td>
      <td>${user.regdate}</td>
    </tr>
  </c:forEach>
  </tbody>

  <button type="button" onclick="history.back()">뒤로가기</button>
</table>
</body>
</html>
