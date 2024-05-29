<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<head>
    <title>게시판 메인</title>
    <style>
        .button-container {
            margin-top: 20px;
        }
        .button-container input {
            margin-right: 10px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 8px;
        }
        th {
            background-color: #f2f2f2;
        }
    </style>
</head>
<body>
<h1>게시글 목록</h1>
<table>
    <thead>
    <tr>
        <th class="one wide">번호</th>
        <th class="ten wide">글제목</th>
        <th class="two wide">작성자</th>
        <th class="three wide">작성일</th>
    </tr>
    </thead>

    <tbody>
    <c:forEach var="board" items="${board}">
        <tr>
            <td><span>${board.bno}</span></td>
            <td><a href="/board/${board.bno}"><span>${board.title}</span></a></td>
            <td><span>${board.writer}</span></td>
            <td><span>${board.regdate}</span></td>
        </tr>
    </c:forEach>
    </tbody>
</table>

    <%
    // 쿠키 배열 가져오기
    Cookie[] cookies = request.getCookies();
    boolean isLoggedIn = false;

    // 쿠키 배열을 순회하며 idx 쿠키의 존재 여부 확인
    if (cookies != null) {
        for (Cookie c : cookies) {
            if (c.getName().equals("idx")) {
                // idx 쿠키가 존재하면 로그인 상태로 설정
                isLoggedIn = true;
                break; // 로그인 상태이므로 더 이상 반복할 필요가 없음
            }
        }
    }
%>

<div class="button-container">
    <%
        // 로그인 상태에 따라 버튼 표시
        if (isLoggedIn) {
    %>
    <input type="button" value="user 목록" onclick="location.href='/user/main'"><br/><br/>
    <input type="button" value="글 작성" onclick="location.href='/write'"><br/><br/>
    <input type="button" value="로그아웃" onclick="location.href='/logout'"><br/><br/>
    <%
    } else {
    %>
    <input type="button" value="로그인" onclick="location.href='/login'"><br/><br/>
    <input type="button" value="회원가입" onclick="location.href='/join'"><br/><br/>
    <%
        }
    %>
</div>

</html>


